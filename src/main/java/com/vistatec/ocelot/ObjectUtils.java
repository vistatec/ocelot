package com.vistatec.ocelot;

public class ObjectUtils {

    /**
     * Safely check equality (with equals()) of two object instances,
     * @param o1
     * @param o2
     * @return
     */
    public static boolean safeEquals(Object o1, Object o2) {
        if (o1 == o2) return true;
        if (o1 == null && o2 != null) return false;
        if (o1 != null && o2 == null) return false;
        return o1.equals(o2);
    }
}
