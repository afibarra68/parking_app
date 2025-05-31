package com.webstore.usersMs.config;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.webstore.usersMs.error.WbException;
import com.webstore.usersMs.error.handlers.enums.WbErrorCode;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

@Log4j2
@UtilityClass
public class HashUtils {

    public static final String ALG = "PBKDF2WithHmacSHA256";

    private static final int ITERATIONS = 10;

    public static Pair<String, String> getEncryptData(String text) throws WbException {

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(text.toCharArray(), salt, ITERATIONS, 128);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALG);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return new ImmutablePair<>(Hex.encodeHexString(hash), Hex.encodeHexString(salt));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new WbException(WbErrorCode.INVALID_HASHING);
        }
    }

    public static String getHashedText(String text, String salt) throws WbException {
        try {
            byte[] bytes = Hex.decodeHex(salt.toCharArray());
            KeySpec spec = new PBEKeySpec(text.toCharArray(), bytes, ITERATIONS, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALG);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Hex.encodeHexString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | DecoderException e) {
            throw new WbException(WbErrorCode.INVALID_HASHING);
        }
    }

}
