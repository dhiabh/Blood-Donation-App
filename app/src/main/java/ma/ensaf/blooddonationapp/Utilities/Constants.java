package ma.ensaf.blooddonationapp.Utilities;

import java.util.HashMap;

public class Constants {
    public static final int SPLASH_SCREEN = 2300;

    // User keys
    public static final String KEY_PREFERENCE_NAME = "bloodDonationAppPreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_USER = "user";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONE_NUMBER = "phoneNumber";
    public static final String KEY_BLOOD_GROUP = "bloodGroup";
    public static final String KEY_TYPE = "type";
    public static final String KEY_SEARCH = "search";
    public static final String KEY_PROFILE_PICTURE_URL = "profilePictureUrl";
    public static final String KEY_ANONYMOUS = "anonymous";
    public static final String KEY_FCM_TOKEN ="fcmToken";

    // Blood Groups
    public static final String VALUE_A_PLUS = "A+";
    public static final String VALUE_A_MINUS = "A-";
    public static final String VALUE_B_PLUS = "B+";
    public static final String VALUE_B_MINUS = "B-";
    public static final String VALUE_AB_PLUS = "AB+";
    public static final String VALUE_AB_MINUS = "AB-";
    public static final String VALUE_O_PLUS = "O+";
    public static final String VALUE_O_MINUS = "O-";

    // Email

    public static final String KEY_COLLECTION_EMAILS = "emails";
    public static final String EMAIL = "leilaicarus@gmail.com";
    public static final String PASSWORD = "leila_icarus_333";

    // Notification keys
    public static final String KEY_COLLECTION_NOTIFICATIONS = "notifications";
    public static final String KEY_RECEIVER_ID = "receiverId";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String TEXT = "text";
    public static final String DATE = "date";

    public static final String KEY_TABLE_CHAT="chat";
    public static final String KEY_MESSAGE="message";
    public static final String KEY_TIMESTAMP="timestamp";
    public static final String KEY_SENDER_NAME="senderName";
    public static final String KEY_RECEIVER_NAME="receiverName";
    public static final String KEY_SENDER_IMAGE="senderImage";
    public static final String KEY_RECEIVER_IMAGE="receiverImage";

    public static final String KEY_TABLE_CONVERSATIONS="conversations";
    public static final String KEY_LAST_MESSAGE="lastMessage";
    public static final String KEY_AVAILABILITY="availability";

    public static final String REMOTE_MSG_AUTHORIZATION="Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE="Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS="registration_ids";


    public static HashMap<String,String> remoteMsgHeaders = null;
    public static HashMap<String,String> getRemoteMsgHeaders(){
        if(remoteMsgHeaders==null){
            remoteMsgHeaders=new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAAq9xJWLY:APA91bHPv4_5d7lcF4iu-4m6IR4ddDUnKLjARb2rx6WoeqErDTSElt8icLmhrtBlwDEfFFYiCuRIulGGXai127IMV8uH9-LyXxUw1m3xyr6VJJoBqG11vB6sPtPHbQYozNaKGvVO9Qd9"

            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return  remoteMsgHeaders;
    }
}
