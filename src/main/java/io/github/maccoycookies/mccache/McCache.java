package io.github.maccoycookies.mccache;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * cache entries
 */
public class McCache {

    Map<String, String> map = new HashMap<>();

    public String get(String key) {
        return map.get(key);
    }

    public void set(String key, String value) {
        map.put(key, value);
    }

    public int del(String ... keys) {
        return keys == null ? 0 : (int)Arrays.stream(keys).map(map::remove).filter(Objects::nonNull).count();
    }

    public int exists(String ... keys) {
        return keys == null ? 0 : (int)Arrays.stream(keys).map(map::containsKey).filter(x -> x).count();
    }
}
