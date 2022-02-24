package be.isach.ultracosmetics.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class WeightedSet<T> {
    private Map<T,Integer> map = new HashMap<>();
    public void add(T key, Integer value) {
        // add to existing value if present, otherwise store value as-is
        map.merge(key, value, (a, b) -> a + b);
    }

    public T getRandom() {
        // sums all values in map
        int sum = map.values().stream().collect(Collectors.summingInt(k -> k));
        int index = ThreadLocalRandom.current().nextInt(sum);
        for (Entry<T,Integer> entry : map.entrySet()) {
            index -= entry.getValue();
            if (index <= 0) {
                return entry.getKey();
            }
        }
        // no keys I guess?
        return null;
    }

    public int size() {
        return map.size();
    }

    public void clear() {
        map.clear();
    }
}
