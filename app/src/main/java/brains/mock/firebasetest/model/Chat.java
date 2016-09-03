package brains.mock.firebasetest.model;

import com.google.firebase.database.IgnoreExtraProperties;

import brains.mock.firebasetest.util.Const;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Chat {

    private String key;

    private String chatName;
    private String lastMessage;
    private long timeStamp;
    private String lastMessageUserName;
    private Map<String, Boolean> members;

    public Chat() {
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> chatMap = new HashMap<>();
        chatMap.put(Const.KEY_CHAT_NAME, chatName);
        chatMap.put(Const.KEY_LAST_MESSAGE, lastMessage);
        chatMap.put(Const.KEY_TIMESTAMP, timeStamp);
        chatMap.put(Const.KEY_LAST_MESSAGE_USER_NAME, lastMessageUserName);
        chatMap.put(Const.KEY_MEMBERS, members);
        return chatMap;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getLastMessageUserName() {
        return lastMessageUserName;
    }

    public void setLastMessageUserName(String lastMessageUserName) {
        this.lastMessageUserName = lastMessageUserName;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, Boolean> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Boolean> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "key='" + key + '\'' +
                ", chatName='" + chatName + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", timeStamp=" + timeStamp +
                ", lastMessageUserName='" + lastMessageUserName + '\'' +
                ", members=" + members +
                '}';
    }
}
