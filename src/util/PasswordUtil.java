package util;


public class PasswordUtil {

    private static final boolean BCRYPT_ENABLED = false;

    private PasswordUtil() {}


    public static String hash(String plainText) {
        if (BCRYPT_ENABLED) {
            // return org.mindrot.jbcrypt.BCrypt.hashpw(plainText, org.mindrot.jbcrypt.BCrypt.gensalt(12));
        }

        return plainText;
    }


    public static boolean verify(String plainText, String stored) {
        if (BCRYPT_ENABLED) {
            // return org.mindrot.jbcrypt.BCrypt.checkpw(plainText, stored);
        }

        return plainText.equals(stored);
    }
}