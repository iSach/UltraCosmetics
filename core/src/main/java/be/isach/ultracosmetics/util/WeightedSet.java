package be.isach.ultracosmetics.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WeightedSet<T> {
    private Map<T,Integer> map = new HashMap<>();
    public WeightedSet() {}
    public WeightedSet(WeightedSet<T> copy) {
        this.map = new HashMap<>(copy.map);
    }
    public void add(T key, Integer value) {
        // add to existing value if present, otherwise store value as-is
        map.merge(key, value, (a, b) -> a + b);
    }

    public T getRandom() {
        // sums all values in map
        int sum = map.values().stream().collect(Collectors.summingInt(k -> k));
        if (sum < 1) return null;
        int index = ThreadLocalRandom.current().nextInt(sum);
        for (Entry<T,Integer> entry : map.entrySet()) {
            index -= entry.getValue();
            if (index <= 0) {
                return entry.getKey();
            }
        }
        // code shouldn't get here
        return null;
    }

    public int size() {
        return map.size();
    }

    public void clear() {
        map.clear();
    }

    public void filter(Function<T,Boolean> filterFunc) {
        map.keySet().removeIf(k -> filterFunc.apply(k));
    }
}
