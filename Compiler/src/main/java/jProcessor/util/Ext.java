package jProcessor.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class Ext {
    public static <T> T firstOrNull(List<T> list, Predicate<T> predicate) {
        for (T obj : list)
            if (predicate.test(obj))
                return obj;

        return null;
    }

    public static <T> T findDuplicate(List<T> list) {
        Set<T> set = new HashSet<>();
        for (T object : list) {
            if (set.contains(object))
                return object;
            set.add(object);
        }
        return null;
    }
}
