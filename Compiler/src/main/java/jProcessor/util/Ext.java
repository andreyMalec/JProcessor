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

    public static Object[] copyOf(Object[] src, Object... added) {
        Object[] values = new Object[src.length + added.length];
        System.arraycopy(added, 0, values, 0, added.length);
        System.arraycopy(src, 0, values, added.length, src.length);
        return values;
    }

    public static void append(StringBuilder sb, Object... values) {
        for (Object value : values)
            sb.append(value);
    }

    public static void appendArguments(StringBuilder sb, String argument, int count) {
        for (int i = 0; i < count; i++) {
            sb.append(argument);
            if (i + 1 < count)
                sb.append(", ");
        }
    }

    public static void appendCall(StringBuilder sb, String argument, int argumentsCount) {
        sb.append("$L($L");
        if (argumentsCount > 0) {
            sb.append(", ");
            appendArguments(sb, argument, argumentsCount);
        }
        sb.append(")");
    }
}
