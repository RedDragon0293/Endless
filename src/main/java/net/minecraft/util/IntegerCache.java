package net.minecraft.util;

public class IntegerCache {
    private static final Integer[] CACHE = new Integer[65535];

    public static Integer getInteger(int integer) {
        return integer >= 0 && integer < CACHE.length ? CACHE[integer] : new Integer(integer);
    }

    static {
        int i = 0;

        for (int j = CACHE.length; i < j; ++i) {
            CACHE[i] = i;
        }
    }
}
