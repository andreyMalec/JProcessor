package jProcessor.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Ext {
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

    public static void appendCommaSeparated(StringBuilder sb, String argument, int count) {
        for (int i = 0; i < count; i++) {
            sb.append(argument);
            if (i + 1 < count)
                sb.append(", ");
        }
    }
}
