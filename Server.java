import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.PrintWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.Scanner;

class ClientHandler extends Thread {
    private Socket mClientSocket;
    public ObjectInputStream mStreamFromClient;
    public ObjectOutputStream mStreamToClient;
    public String mIPAddress;
    public String mNickname;

    public String mActiveInvite;
    public Boolean mHasActiveChat = false;
    public ClientHandler mChatPartner;

    public ClientHandler(Socket socket) {
        mClientSocket = socket;
        try {
            mStreamFromClient = new ObjectInputStream(mClientSocket.getInputStream());
            mStreamToClient = new ObjectOutputStream(mClientSocket.getOutputStream());
            mStreamToClient.flush(); // Good to call this
            mIPAddress = socket.getInetAddress().getHostAddress();
            mNickname = Nicknames.get();

            send("Ai primit nickname-ul " + mNickname + "\n" + "Il poti schimba ulterior");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void send(TaskResult res) {
        try {
            mStreamToClient.writeObject(res);
            mStreamToClient.flush();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void send(String msg) {
        TaskResult res = new TaskResult();
        res.msg = msg;
        send(res);
    }

    public void disconnectPartner() {
        if (mHasActiveChat) {
            mChatPartner.send("Sorry, your partener choosen to chat with someone else.");
            mChatPartner.mHasActiveChat = false;
            mChatPartner.mChatPartner = null;
        }
    }

    public void run() {
        try {
            boolean stillConnected = true;
            while (stillConnected) {
                Message msg = (Message) mStreamFromClient.readObject();
                switch (msg.mType) {
                    case MSG_DISCONNECT:
                    {
                        System.out.println("client got disconnected..");
                        stillConnected = false;
                        mClientSocket.close();
                        Server.removeClient(this);
                    }
                    break;
                    case MSG_LIST:
                    {
                        String message = "Num clients connected = " + Server.mClientsList.size() + "\n";
                        for (ClientHandler ch: Server.mClientsList) {
                            if (ch.mNickname == mNickname) {
                                message += "(me) ";
                            }
                            message += ch.mNickname + " " + ch.mIPAddress + "\n";
                        }

                        send(message);
                    }
                    break;
                    case MSG_CONNECT:
                    {
                        mActiveInvite = msg.mArgs[0];
                        if (mNickname.equals(mActiveInvite)) {
                            send("you can't chat with yourself.. yet");
                            break;
                        }

                        ClientHandler partner = Server.getClient(mActiveInvite);
                        if (mNickname.equals(partner.mActiveInvite)) {
                            if (partner.mHasActiveChat) {
                                partner.disconnectPartner();
                            }

                            if (mHasActiveChat) {
                                disconnectPartner();
                            }

                            mHasActiveChat = true;
                            mChatPartner = partner;

                            partner.mHasActiveChat = true;
                            partner.mChatPartner = this;

                            send("Connection established. Everything you type, except commands like --<cmd>, will be sent to your partner");
                            partner.send("Connection established. Everything you type, except commands like --<cmd>, will be sent to your partner");
                        } else {
                            send("Waiting for " + mActiveInvite + " to accept your invite");
                            partner.send(String.format("%1$s has sent you a chat invitation\nType --connect %1$s to accept it", mNickname));
                        }
                    }
                    break;
                    case MSG_BROADCAST:
                    {
                        String broadcastMessage = String.format("Broadcast Message from %s: %s", mNickname, msg.mArgs[0]);

                        Server.broadcast(broadcastMessage);
                    }
                    break;
                    case MSG_CHAT:
                    {
                        if (mHasActiveChat) {
                            mChatPartner.send(
                                String.format("%s: %s", mChatPartner.mNickname, msg.mArgs[0])
                            );
                        } else {
                            send("Type --list first to see who is online\n Then select a person to chat with and type --connect <nickname>");
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            Server.removeClient(this);
            System.out.println(e);
        }
    }
}

class ServerConsole extends Thread {
    public void run() {
        Scanner scan = new Scanner(System.in);
        while (true) {
            String commandStr = scan.nextLine();
            if (commandStr.compareTo("showClients") == 0) {
                System.out.println("Num clients connected = " + Server.mClientsList.size());
                for (ClientHandler ch: Server.mClientsList) {
                    System.out.print(ch.mIPAddress + " ");
                }
            }
        }
    }
}

public class Server {
    /**
     * @param args the command line arguments
     */
    public static ServerSocket mServerSocket;
    public static ArrayList <ClientHandler> mClientsList;
    public static void main(String[] args) {
        try {
            mServerSocket = new ServerSocket(9090);
            mClientsList = new ArrayList<ClientHandler>();
            ServerConsole sc = new ServerConsole();
            sc.start();
            while (true) {
                Socket socket = mServerSocket.accept();
                ClientHandler ch = new ClientHandler(socket);
                addClient(ch);
                ch.start();
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    public static void broadcast(String msg) {
        System.out.println(msg);
        for (ClientHandler ch: Server.mClientsList) {
            System.out.printf("Trying to send love to %s\n", ch.mNickname);
            ch.send(msg);
        }
    }

    public static ClientHandler getClient(String nickname) {
        for (ClientHandler ch: Server.mClientsList) {
            if (ch.mNickname.equals(nickname)) {
                return ch;
            }
        }

        return null;
    }

    public static void sendToClient(String nickname, String msg) {
        for (ClientHandler ch: Server.mClientsList) {
            if (ch.mNickname.equals(nickname)) {
                System.out.println(nickname + msg);
                ch.send(msg);
                break;
            }
        }
    }

    // Basic sync. One other way is to have an active object
    public synchronized static void removeClient(ClientHandler ch) {
        mClientsList.remove(ch);
    }

    public synchronized static void addClient(ClientHandler ch) {
        mClientsList.add(ch);
    }
}
