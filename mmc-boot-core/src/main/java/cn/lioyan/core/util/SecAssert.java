package cn.lioyan.core.util;

/**
 * SecAssert
 *
 * @author com.lioyan
 * @since 2022/4/13 15:14
 */
public interface SecAssert {

    static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    static void hasText(String text, String message) {
        if (NullUtil.isNull(text)) {
            throw new IllegalArgumentException(message);
        }
    }

    static void state(boolean state, String message) {
        if (!state) {
            throw new IllegalArgumentException(message);
        }
    }
}
