package io.github.maccoycookies.mccache;

import lombok.*;

import java.util.*;
import java.util.stream.Stream;

/**
 * cache entries
 */
public class McCache {

    Map<String, CacheEntry<?>> map = new HashMap<>();

    // ========================== String start ==========================
    public String get(String key) {
        CacheEntry<String> cacheEntry = (CacheEntry<String>) map.get(key);
        return cacheEntry.getValue();
    }

    public void set(String key, String value) {
        map.put(key, new CacheEntry<>(value));
    }

    public int del(String ... keys) {
        return keys == null ? 0 : (int)Arrays.stream(keys).map(map::remove).filter(Objects::nonNull).count();
    }

    public int exists(String ... keys) {
        return keys == null ? 0 : (int)Arrays.stream(keys).map(map::containsKey).filter(x -> x).count();
    }

    public String[] mget(String ... keys) {
        return keys == null ? new String[0] :
                Arrays.stream(keys).map(this::get).toArray(String[]::new);
    }

    public void mset(String[] keys, String[] vals) {
        if (keys == null || keys.length == 0) return;
        for (int i = 0; i < keys.length; i++) {
            set(keys[i], vals[i]);
        }
    }

    public int incr(String key) {
        String str = get(key);
        int val = 0;
        try {
            if (str != null) {
                val = Integer.parseInt(str);
            }
            val++;
            set(key, String.valueOf(val));
        } catch (NumberFormatException exception) {
            throw exception;
        }
        return val;
    }

    public int decr(String key) {
        String str = get(key);
        int val = 0;
        try {
            if (str != null) {
                val = Integer.parseInt(str);
            }
            val--;
            set(key, String.valueOf(val));
        } catch (NumberFormatException exception) {
            throw exception;
        }
        return val;
    }

    public Integer strlen(String key) {
        return get(key) == null ? null : key.length();
    }

    // ========================== String end ==========================

    // ========================== List start ==========================

    public Integer lpush(String key, String[] vals) {
        if (vals == null) return 0;
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>) map.get(key);
        if (entry == null) {
            entry = new CacheEntry<>(new LinkedList<>());
            this.map.put(key, entry);
        }
        LinkedList<String> exists = entry.getValue();
        Arrays.stream(vals).forEach(exists::addFirst);
        return vals.length;
    }

    public String[] lpop(String key, int count) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>) map.get(key);
        if (entry == null) return null;
        LinkedList<String> exists = entry.getValue();
        if (exists == null) return null;
        int len = Math.min(exists.size(), count);
        String[] res = new String[len];
        for (int i = 0; i < len; i++) {
            res[i] = exists.removeFirst();
        }
        return res;
    }

    public Integer rpush(String key, String[] vals) {
        if (vals == null) return 0;
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>) map.get(key);
        if (entry == null) {
            entry = new CacheEntry<>(new LinkedList<>());
            this.map.put(key, entry);
        }
        LinkedList<String> exists = entry.getValue();
        Arrays.stream(vals).forEach(exists::addLast);
        return vals.length;
    }

    public String[] rpop(String key, int count) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>) map.get(key);
        if (entry == null) return null;
        LinkedList<String> exists = entry.getValue();
        if (exists == null) return null;
        int len = Math.min(exists.size(), count);
        String[] res = new String[len];
        for (int i = 0; i < len; i++) {
            res[i] = exists.removeLast();
        }
        return res;
    }

    public Integer llen(String key) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>) map.get(key);
        if (entry == null) return null;
        return entry.getValue().size();
    }

    public String lindex(String key, Integer index) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>) map.get(key);
        if (entry == null || entry.getValue().isEmpty()) return null;
        if (entry.getValue().size() <= index) return null;
        return entry.getValue().get(index);
    }

    public String[] lrange(String key, int start, int end) {
        CacheEntry<LinkedList<String>> entry = (CacheEntry<LinkedList<String>>) map.get(key);
        if (entry == null || entry.getValue().isEmpty()) return null;
        LinkedList<String> exists = entry.getValue();
        if (exists.size() <= start) return null;
        if (end >= exists.size()) end = exists.size() - 1;
        int len = Math.min(exists.size(), end - start + 1);
        String[] res = new String[len];
        for (int i = 0; i < len; i++) {
            res[i] = exists.get(start + i);
        }
        return res;
    }

    // ========================== List end ==========================

    // ========================== Set start ==========================

    public Integer sadd(String key, String[] vals) {
        if (vals == null) return 0;
        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>) map.get(key);
        if (entry == null) {
            entry = new CacheEntry<>(new LinkedHashSet<>());
            this.map.put(key, entry);
        }
        LinkedHashSet<String> exists = entry.getValue();
        exists.addAll(Arrays.asList(vals));
        return vals.length;
    }

    public String[] smembers(String key) {
        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>) map.get(key);
        if (entry == null) {
            return null;
        }
        return entry.getValue().toArray(String[]::new);
    }

    public Integer scard(String key) {
        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>) map.get(key);
        if (entry == null) return null;
        return entry.getValue().size();
    }

    public Integer sismembers(String key, String val) {
        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>) map.get(key);
        if (entry == null) return 0;
        return entry.getValue().contains(val) ? 1 : 0;
    }

    public Integer srem(String key, String[] val) {
        if (val == null) return 0;
        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>) map.get(key);
        if (entry == null) return 0;
        return (int) Arrays.stream(val).map(entry.getValue()::remove).filter(x -> x).count();
    }

    Random random = new Random();

    public String[] spop(String key, int count) {
        CacheEntry<LinkedHashSet<String>> entry = (CacheEntry<LinkedHashSet<String>>) map.get(key);
        if (entry == null) return null;
        LinkedHashSet<String> exists = entry.getValue();
        if (exists == null) return null;
        int len = Math.min(exists.size(), count);
        String[] res = new String[len];
        for (int i = 0; i < len; i++) {
            String obj = exists.toArray(String[]::new)[random.nextInt(exists.size())];
            exists.remove(obj);
            res[i] = obj;
        }
        return res;
    }

    // ========================== Set end ==========================

    // ========================== Hash start ==========================

    public Integer hset(String key, String[] hkeys, String[] hvals) {
        if (hkeys == null || hkeys.length == 0) return 0;
        if (hvals == null || hvals.length == 0) return 0;
        CacheEntry<LinkedHashMap<String, String>> entry = (CacheEntry<LinkedHashMap<String, String>>) map.get(key);
        if (entry == null) {
            entry = new CacheEntry<>(new LinkedHashMap<>());
            this.map.put(key, entry);
        }
        LinkedHashMap<String, String> exists = entry.getValue();
        for (int i = 0; i < hkeys.length; i++) {
            exists.put(hkeys[i], hvals[i]);
        }
        return hkeys.length;
    }

    public String hget(String key, String hkey) {
        CacheEntry<LinkedHashMap<String, String>> entry = (CacheEntry<LinkedHashMap<String, String>>) map.get(key);
        if (entry == null) return null;
        return entry.getValue().get(hkey);
    }

    public String[] hgetall(String key) {
        CacheEntry<LinkedHashMap<String, String>> entry = (CacheEntry<LinkedHashMap<String, String>>) map.get(key);
        if (entry == null) return null;
        return entry.getValue().entrySet().stream()
                .flatMap(e -> Stream.of(e.getKey(), e.getValue())).toArray(String[]::new);
    }

    public String[] hmget(String key, String[] hkeys) {
        CacheEntry<LinkedHashMap<String, String>> entry = (CacheEntry<LinkedHashMap<String, String>>) map.get(key);
        if (entry == null) return null;
        return hkeys == null ? new String[0] :
                Arrays.stream(hkeys).map(entry.getValue()::get).toArray(String[]::new);
    }

    public Integer hlen(String key) {
        CacheEntry<LinkedHashMap<String, String>> entry = (CacheEntry<LinkedHashMap<String, String>>) map.get(key);
        if (entry == null) return 0;
        return entry.getValue().size();
    }

    public Integer hexists(String key, String hkey) {
        CacheEntry<LinkedHashMap<String, String>> entry = (CacheEntry<LinkedHashMap<String, String>>) map.get(key);
        if (entry == null) return 0;
        return entry.getValue().containsKey(hkey) ? 1 : 0;
    }

    public Integer hdel(String key, String[] hkeys) {
        CacheEntry<LinkedHashMap<String, String>> entry = (CacheEntry<LinkedHashMap<String, String>>) map.get(key);
        if (entry == null) return 0;
        return hkeys == null ? 0 :
                (int) Arrays.stream(hkeys).map(entry.getValue()::remove).filter(Objects::nonNull).count();
    }

    // ========================== Hash end ==========================

    // ========================== ZSet start ==========================

    public Integer zadd(String key, String[] vals, double[] scores) {
        CacheEntry<LinkedHashSet<ZsetEntry>> entry = (CacheEntry<LinkedHashSet<ZsetEntry>>) map.get(key);
        if (entry == null) {
            entry = new CacheEntry<>(new LinkedHashSet<>());
            this.map.put(key, entry);
        }
        LinkedHashSet<ZsetEntry> exists = entry.getValue();
        for (int i = 0; i < vals.length; i++) {
            exists.add(new ZsetEntry(vals[i], scores[i]));
        }
        return vals.length;
    }

    public Integer zcard(String key) {
        CacheEntry<LinkedHashSet<?>> entry = (CacheEntry<LinkedHashSet<?>>) map.get(key);
        if (entry == null) return null;
        return entry.getValue().size();
    }

    public Integer zcount(String key, double min, double max) {
        CacheEntry<LinkedHashSet<ZsetEntry>> entry = (CacheEntry<LinkedHashSet<ZsetEntry>>) map.get(key);
        if (entry == null) return null;
        return (int) entry.getValue().stream().filter(x -> x.getScore() >= min && x.getScore() <= max).count();
    }

    public Double zscore(String key, String val) {
        CacheEntry<LinkedHashSet<ZsetEntry>> entry = (CacheEntry<LinkedHashSet<ZsetEntry>>) map.get(key);
        if (entry == null) return null;
        return entry.getValue().stream().filter(x -> x.getValue().equals(val)).map(ZsetEntry::getScore).findFirst().orElse(null);
    }

    public Integer zrank(String key, String val) {
        CacheEntry<LinkedHashSet<ZsetEntry>> entry = (CacheEntry<LinkedHashSet<ZsetEntry>>) map.get(key);
        if (entry == null) return null;
        Double zscore = zscore(key, val);
        if (zscore == null) return null;
        return (int) entry.getValue().stream().filter(z -> z.getScore() < zscore).count();
    }

    public Integer zrem(String key, String[] vals) {
        if (vals == null) return 0;
        CacheEntry<LinkedHashSet<ZsetEntry>> entry = (CacheEntry<LinkedHashSet<ZsetEntry>>) map.get(key);
        if (entry == null) return null;
        return (int) Arrays.stream(vals).map(x -> entry.getValue().removeIf(z -> z.getValue().equals(x)))
                .filter(x -> x).count();
    }

    // ========================== ZSet end ==========================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CacheEntry<T> {
        private T value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ZsetEntry<T> {
        private String value;
        private double score;
    }
}
