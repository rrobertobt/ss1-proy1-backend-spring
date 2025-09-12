package usac.cunoc.bpmn.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Article type enumeration for BPMN catalog system
 * Represents the different types of analog music articles supported
 */
@Getter
@RequiredArgsConstructor
public enum ArticleType {

    VINYL("vinyl", "Disco de Vinilo", "Analog vinyl record"),
    CASSETTE("cassette", "Cassette", "Magnetic tape cassette"),
    CD("cd", "Disco Compacto", "Compact disc");

    private final String code;
    private final String displayName;
    private final String description;

    /**
     * Get ArticleType from string code
     */
    public static ArticleType fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

        for (ArticleType type : ArticleType.values()) {
            if (type.code.equalsIgnoreCase(code.trim())) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unknown article type: " + code);
    }

    /**
     * Check if the given code is valid
     */
    public static boolean isValidCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }

        try {
            fromCode(code);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Get all valid type codes
     */
    public static String[] getValidCodes() {
        return new String[] {
                VINYL.code,
                CASSETTE.code,
                CD.code
        };
    }

    @Override
    public String toString() {
        return this.code;
    }
}