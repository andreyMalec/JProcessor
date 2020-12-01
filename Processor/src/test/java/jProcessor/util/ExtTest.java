package jProcessor.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static jProcessor.util.Ext.filter;
import static jProcessor.util.Ext.map;
import static org.junit.Assert.assertEquals;

public class ExtTest {
    @Test
    public void testMap() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        List<String> mapped = map(list, it -> String.valueOf(it * it));
        assertEquals("1", mapped.get(0));
        assertEquals("4", mapped.get(1));
        assertEquals("9", mapped.get(2));
    }

    @Test
    public void testArrayMap() {
        Integer[] arr = new Integer[3];
        arr[0] = 1;
        arr[1] = 2;
        arr[2] = 3;
        Object[] mapped = map(arr, it -> String.valueOf(it * it));
        assertEquals("1", mapped[0]);
        assertEquals("4", mapped[1]);
        assertEquals("9", mapped[2]);
    }

    @Test
    public void testFilter() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        List<Integer> mapped = filter(list, it -> it >= 2);
        assertEquals(2, (int) mapped.get(0));
        assertEquals(3, (int) mapped.get(1));
    }
}
