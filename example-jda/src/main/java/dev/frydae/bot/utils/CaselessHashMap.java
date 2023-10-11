package dev.frydae.bot.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is a normal {@link HashMap String map}, but the keys are forced lowercase.
 */
public class CaselessHashMap<T> extends HashMap<String, T> {
    public CaselessHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public CaselessHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public CaselessHashMap() {
    }

    public CaselessHashMap(Map<? extends String, ? extends T> m) {
        super(m);
    }

    @Override
    public T put(String key, T value) {
        return super.put(key.toLowerCase(), value);
    }

    @Override
    public T get(Object key) {
        return super.get(((String) key).toLowerCase());
    }

    @Override
    public void putAll(Map<? extends String, ? extends T> m) {
        for (Map.Entry<? extends String, ? extends T> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public T remove(Object key) {
        return super.remove(((String) key).toLowerCase());
    }

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(((String) key).toLowerCase());
    }
}

