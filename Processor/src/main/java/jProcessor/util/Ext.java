package jProcessor.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class Ext {
    @SuppressWarnings("unchecked")
    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        T[] filtered = (T[]) new Object[list.size()];

        int length = 0;
        for (T obj : list)
            if (predicate.test(obj))
                filtered[length++] = obj;

        return Arrays.asList(Arrays.copyOf(filtered, length));
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> filter(Set<T> list, Predicate<T> predicate) {
        T[] filtered = (T[]) new Object[list.size()];

        int length = 0;
        for (T obj : list)
            if (predicate.test(obj))
                filtered[length++] = obj;

        return Arrays.asList(Arrays.copyOf(filtered, length));
    }

    @SuppressWarnings("unchecked")
    public static <T, E> List<E> map(List<T> list, Function<T, E> mapper) {
        E[] filtered = (E[]) new Object[list.size()];

        int length = 0;
        for (T obj : list)
            filtered[length++] = mapper.apply(obj);

        return Arrays.asList(Arrays.copyOf(filtered, length));
    }

    @SuppressWarnings("unchecked")
    public static <T, E> Object[] map(T[] arr, Function<T, E> mapper) {
        E[] filtered = (E[]) new Object[arr.length];

        int length = 0;
        for (T obj : arr)
            filtered[length++] = mapper.apply(obj);

        return Arrays.copyOf(filtered, length);
    }

    public static <T> List<T> toList(Collection<T> collection) {
        return new ArrayList<>(collection);
    }
}
