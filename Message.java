import java.io.Serializable;

public class Message implements Serializable {
    public enum MsgType {
        MSG_INVALID,
        MSG_DISCONNECT,
        MSG_ADD,
        MSG_MUL,
        MSG_POW,
        MSG_HELP,
        MSG_NICK,
        MSG_CONNECT,
        MSG_CHAT,
        MSG_LIST,
        MSG_BROADCAST
    };

    public MsgType mType;
    public String[] mArgs;

    Message() {
        mType = MsgType.MSG_INVALID;
        mN = 0;
        mArgs = new String[1];
     }

    // TODO: as you can see ideally we should have a base class for message then multiple derived class depending on message type
    public int mN;
    public int[] mNumbers;
    public int mA, mB;
}
