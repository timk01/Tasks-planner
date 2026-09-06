package tasksplanner;

import java.security.SecureRandom;
import java.util.Base64;

public class Smth {
    public static void main(String[] args) {
        byte[] bytes = new byte[32]; // 256 бит
        new SecureRandom().nextBytes(bytes);

        String secret = Base64.getEncoder().encodeToString(bytes);

        System.out.println(secret);
    }
}
