import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.Console;
import java.util.Scanner;
import javax.swing.JOptionPane;
import java.util.Arrays;


class ClientInput extends Thread {
    private Socket mClientSocket;

    public ClientInput(Socket socket) {
        mClientSocket = socket;
    }

    public void run() {
        try {
            ObjectInputStream streamFromServer = new ObjectInputStream(mClientSocket.getInputStream());
            while (true) {
                TaskResult result = (TaskResult) streamFromServer.readObject();
                System.out.println(result.msg);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

public class Client {
    // Returning null if message format is incorrect
    public static Message parseMessage(String msgText) {
        final String[] tokens = msgText.split(" ");
        Message msg = new Message();
        switch (tokens[0]) {
            case "--help":
            {
                msg.mType = Message.MsgType.MSG_HELP;
            }
            break;
            case "--connect": case "--c":
            {
                if (tokens.length != 2) {
                    System.out.println("Usage: --connect <username>");
                    return null;
                }

                msg.mType = Message.MsgType.MSG_CONNECT;
                msg.mArgs[0] = tokens[1];
            }
            break;
            case "--list":
            {
                msg.mType = Message.MsgType.MSG_LIST;
            }
            break;
            case "--nick":
            {
                if (tokens.length != 2) {
                    System.out.println("Usage: --nick <new_nickname>");
                    return null;
                }

                msg.mType = Message.MsgType.MSG_NICK;
                msg.mArgs[0] = tokens[0];
            }
            break;
            case "--broadcast":
            {
                if (tokens.length == 1) {
                    System.out.println("Usage: --broadcast <msg>");
                    return null;
                }

                msg.mType = Message.MsgType.MSG_BROADCAST;
                msg.mArgs[0] = String.join(" ", Arrays.copyOfRange(tokens, 1, tokens.length));
            }
            break;
            case "--disconnect":
            {
                msg.mType = Message.MsgType.MSG_DISCONNECT;
            }
            break;
            default:
            {
                msg.mType = Message.MsgType.MSG_CHAT;
                msg.mArgs[0] = msgText;
            }
        }

        return msg;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            //JOptionPane.showInputDialog("enter IP " + "(running on port 9090");
            String serverAddress = "localhost";
            Socket clientSocket = new Socket(serverAddress, 9090);
            ClientInput ci = new ClientInput(clientSocket);
            ci.start();

            ObjectOutputStream streamToServer = new ObjectOutputStream(clientSocket.getOutputStream());
            Scanner scan = new Scanner(System.in);
            while (true) {
                // Read a message from client
                String msgText = scan.nextLine();
                Message msg = parseMessage(msgText);
                if (msg.mType == Message.MsgType.MSG_INVALID) {
                    System.out.println("Incorrect message format, try again");
                } else {
                    // Writting message to server
                    streamToServer.writeObject(msg);

                    // Normally we should wait for disconnect confirm for server but this is fine too.
                    if (msg.mType == Message.MsgType.MSG_DISCONNECT)
                        break;
                }
            }
            //JOptionPane.showMessageDialog(null, answer);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
