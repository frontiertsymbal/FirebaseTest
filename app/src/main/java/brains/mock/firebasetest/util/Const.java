package brains.mock.firebasetest.util;

public class Const {

    public static final long SPLASH_SCREEN_TIMEOUT = 3;
    public static final int MIN_PASSWORD_LENGTH = 6;

    public static final String PROVIDER_GOOGLE = "google";
    public static final String PROVIDER_FIREBASE = "firebase";


    //Database reference name
    public static final String REFERENCE_USER = "users";
    public static final String REFERENCE_CHAT = "chats";
    public static final String REFERENCE_MESSAGE = "messages";

    //General map key
    public static final String KEY_USER_UID = "userUID";
    public static final String KEY_MESSAGE_KEY = "MESSAGE_KEY";

    //User map key
    public static final String KEY_USER_NAME = "userName";
    public static final String KEY_NICK_NAME = "nickName";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_LAST_AVAILABLE_TIME = "lastAvailableTime";
    public static final String KEY_BIRTH_YEAR = "birthYear";
    public static final String KEY_BIRTH_MONTH = "birthMonth";
    public static final String KEY_BIRTH_DAY = "birthDay";
    public static final String KEY_PHONE_NUMBER = "phoneNumber";
    public static final String KEY_PROVIDER = "provider";
    public static final String KEY_CHAT_LIST = "chats";

    //Message map key
    public static final String KEY_MESSAGE = "MESSAGE";
    public static final String KEY_MESSAGE_STATE = "MESSAGE_STATE";

    //Chat map key
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
    public static final String KEY_LAST_MESSAGE_USER_NAME = "lastMessageUserName";
    public static final String KEY_MEMBERS = "members";

    //Intent extra
    public static final String EXTRA_USER_UID = "USER_UID";
    public static final String KEY_CHAT_NAME = "chatName";

}
