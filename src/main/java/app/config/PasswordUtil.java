package app.config;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {


    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainPassword, String hashedPasswordFromDB) {
        return BCrypt.checkpw(plainPassword, hashedPasswordFromDB);
    }
}
