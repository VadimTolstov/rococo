package guru.qa.niffler.utils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@ParametersAreNonnullByDefault
public class OauthUtils {
    private static final int CODE_VERIFIER_LENGTH = 128; // Длина code_verifier в байтах

    /**
     * Генерирует случайный code_verifier.
     *
     * @return Строка code_verifier.
     */
    public static String generateCodeVerifier() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] codeVerifier = new byte[CODE_VERIFIER_LENGTH];
        secureRandom.nextBytes(codeVerifier);
        return Base64UrlEncode(codeVerifier);
    }

    /**
     * Генерирует code_challenge на основе code_verifier.
     *
     * @param codeVerifier Строка code_verifier.
     * @return Строка code_challenge.
     */
    public static String generateCodeChallenge(String codeVerifier) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));
            return Base64UrlEncode(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Ошибка при генерации code_challenge", e);
        }
    }

    /**
     * Кодирует байты в строку Base64 URL-безопасным способом.
     *
     * @param bytes Массив байтов для кодирования.
     * @return Строка в формате Base64 URL-безопасного кодирования.
     */
    private static String Base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
