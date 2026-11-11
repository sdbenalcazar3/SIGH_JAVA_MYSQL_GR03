package edu.ec.monster.ORM_JAVA_GR03.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * Utilidades de seguridad para el login: hash SHA-256 de claves y generacion de tokens.
 */
public final class SeguridadUtil {

    private static final SecureRandom ALEATORIO = new SecureRandom();

    private SeguridadUtil() {
    }

    public static String sha256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("No se pudo inicializar SHA-256", ex);
        }
    }

    public static boolean esSha256(String texto) {
        return texto != null && texto.matches("^[a-fA-F0-9]{64}$");
    }

    /** Compara dos hash sin cortar en el primer caracter distinto. */
    public static boolean mismoHash(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return MessageDigest.isEqual(a.toLowerCase().getBytes(StandardCharsets.UTF_8),
                b.toLowerCase().getBytes(StandardCharsets.UTF_8));
    }

    public static String generarToken() {
        byte[] bytes = new byte[32];
        ALEATORIO.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}
