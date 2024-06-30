package io.github.maccoycookies.mccache.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Maccoy
 * @date 2024/6/30 22:54
 * Description reply for all types
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reply<T> {

    T value;
    ReplyType type;

    public static Reply<String> simpleString(String value) {
        return new Reply<>(value, ReplyType.SIMPLE_STRING);
    }

    public static Reply<String> bulkString(String value) {
        return new Reply<>(value, ReplyType.BULK_STRING);
    }

    public static Reply<Integer> integer(Integer value) {
        return new Reply<>(value, ReplyType.INT);
    }

    public static Reply<String> error(String value) {
        return new Reply<>(value, ReplyType.ERROR);
    }

    public static Reply<String[]> array(String[] array) {
        return new Reply<>(array, ReplyType.ARRAY);
    }

}
