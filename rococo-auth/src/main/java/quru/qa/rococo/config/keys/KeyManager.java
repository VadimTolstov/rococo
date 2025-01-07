package quru.qa.rococo.config.keys;

import com.nimbusds.jose.jwk.RSAKey;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * Класс, отвечающий за управление ключами шифрования.
 * Этот класс генерирует пару RSA-ключей (публичный и приватный) и предоставляет их
 * в формате, совместимом с библиотекой Nimbus JOSE JWT.
 */
@Component
public class KeyManager {

    /**
     * Генерирует пару RSA-ключей и возвращает их в виде объекта {@link RSAKey}.
     * Этот метод создает новый ключ каждый раз, когда вызывается.
     *
     * @return Объект {@link RSAKey}, содержащий публичный и приватный ключи, а также уникальный идентификатор ключа.
     * @throws NoSuchAlgorithmException Если алгоритм "RSA" не поддерживается в текущей среде выполнения.
     */
    public @Nonnull
    RSAKey rsaKey() throws NoSuchAlgorithmException {
        // Создаем генератор ключей для алгоритма RSA.
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");

        // Инициализируем генератор с длиной ключа 2048 бит (стандартное значение для RSA).
        generator.initialize(2048);

        // Генерируем пару ключей: публичный и приватный.
        KeyPair keyPair = generator.generateKeyPair();

        // Извлекаем публичный ключ из пары ключей.
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        // Извлекаем приватный ключ из пары ключей.
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        // Создаем объект RSAKey, используя публичный ключ, приватный ключ и уникальный идентификатор.
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)  // Добавляем приватный ключ.
                .keyID(UUID.randomUUID().toString())  // Генерируем уникальный идентификатор для ключа.
                .build();  // Собираем и возвращаем объект RSAKey.
    }
}