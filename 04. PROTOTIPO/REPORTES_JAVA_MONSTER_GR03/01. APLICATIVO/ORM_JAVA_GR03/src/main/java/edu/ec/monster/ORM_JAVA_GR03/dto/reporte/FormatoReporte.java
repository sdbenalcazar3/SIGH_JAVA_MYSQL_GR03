package edu.ec.monster.ORM_JAVA_GR03.dto.reporte;

import java.util.Locale;

/**
 * Formatos de exportacion soportados por el modulo de reportes SIGH GR03.
 */
public enum FormatoReporte {
    PDF("pdf", "application/pdf"),
    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final String extension;
    private final String mediaType;

    FormatoReporte(String extension, String mediaType) {
        this.extension = extension;
        this.mediaType = mediaType;
    }

    public String getExtension() {
        return extension;
    }

    public String getMediaType() {
        return mediaType;
    }

    /**
     * Convierte un texto de formato (case-insensitive) en {@link FormatoReporte}.
     * Si el valor es {@code null} o esta en blanco devuelve {@link #PDF} por omision.
     *
     * @throws IllegalArgumentException si el valor no corresponde a {@code pdf} ni {@code xlsx}
     */
    public static FormatoReporte desde(String valor) {
        if (valor == null || valor.isBlank()) {
            return PDF;
        }
        String normalizado = valor.trim().toLowerCase(Locale.ROOT);
        for (FormatoReporte formato : values()) {
            if (formato.extension.equals(normalizado)) {
                return formato;
            }
        }
        throw new IllegalArgumentException(
                "Formato de reporte no valido: '" + valor.trim() + "'. Los formatos permitidos son pdf y xlsx.");
    }
}
