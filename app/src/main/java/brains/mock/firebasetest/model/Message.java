package brains.mock.firebasetest.model;

import brains.mock.firebasetest.util.Const;

import java.util.HashMap;
import java.util.Map;

public class Message {

    private String key;
    private String message;
    private long timestamp;
    private String userUID;
    private String messageState;

    public Message() {
    }

    public Message(String key, String message, long timestamp, String userUID) {
        this.key = key;
        this.message = message;
        this.timestamp = timestamp;
        this.userUID = userUID;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put(Const.KEY_MESSAGE_KEY, key);
        messageMap.put(Const.KEY_MESSAGE, message);
        messageMap.put(Const.KEY_TIMESTAMP, timestamp);
        messageMap.put(Const.KEY_MESSAGE_STATE, messageState);
        return messageMap;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getMessageState() {
        return messageState;
    }

    public void setMessageState(String messageState) {
        this.messageState = messageState;
    }

    @Override
    public String toString() {
        return "Message{" +
                "key='" + key + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", userUID='" + userUID + '\'' +
                ", messageState=" + messageState +
                '}';
    }
}
