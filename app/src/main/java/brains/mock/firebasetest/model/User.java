package brains.mock.firebasetest.model;

import brains.mock.firebasetest.util.Const;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String userUID;
    private String userName;
    private String nickName;
    private String email;
    private long lastAvailableTime;
    private int birthYear;
    private int birthMonth;
    private int birthDay;
    private String phoneNumber;
    private String provider;
    private Map<String, Boolean> chats;

    public User() {
    }

    public User(String userUID, String userName, String email, long lastAvailableTime, String provider) {
        this.userUID = userUID;
        this.userName = userName;
        this.email = email;
        this.lastAvailableTime = lastAvailableTime;
        this.provider = provider;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put(Const.KEY_USER_UID, userUID);
        userMap.put(Const.KEY_USER_NAME, userName);
        userMap.put(Const.KEY_NICK_NAME, nickName);
        userMap.put(Const.KEY_EMAIL, email);
        userMap.put(Const.KEY_LAST_AVAILABLE_TIME, lastAvailableTime);
        userMap.put(Const.KEY_BIRTH_YEAR, birthYear);
        userMap.put(Const.KEY_BIRTH_MONTH, birthMonth);
        userMap.put(Const.KEY_BIRTH_DAY, birthDay);
        userMap.put(Const.KEY_PHONE_NUMBER, phoneNumber);
        userMap.put(Const.KEY_PROVIDER, provider);
        userMap.put(Const.KEY_CHAT_LIST, chats);
        return userMap;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getLastAvailableTime() {
        return lastAvailableTime;
    }

    public void setLastAvailableTime(long lastAvailableTime) {
        this.lastAvailableTime = lastAvailableTime;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public int getBirthMonth() {
        return birthMonth;
    }

    public void setBirthMonth(int birthMonth) {
        this.birthMonth = birthMonth;
    }

    public int getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(int birthDay) {
        this.birthDay = birthDay;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Map<String, Boolean> getChats() {
        return chats;
    }

    public void setChats(Map<String, Boolean> chats) {
        this.chats = chats;
    }

    @Override
    public String toString() {
        return "User{" +
                "userUID='" + userUID + '\'' +
                ", userName='" + userName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", email='" + email + '\'' +
                ", lastAvailableTime=" + lastAvailableTime +
                ", birthYear=" + birthYear +
                ", birthMonth=" + birthMonth +
                ", birthDay=" + birthDay +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", provider='" + provider + '\'' +
                ", chats=" + chats +
                '}';
    }
}
