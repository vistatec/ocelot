package com.vistatec.ocelot;

import org.junit.*;

import static org.junit.Assert.*;

public class TestObjectUtils {

    @Test
    public void testSafeEquals() {
        assertTrue(ObjectUtils.safeEquals(null, null));
        Object o = new Object();
        assertFalse(ObjectUtils.safeEquals(null, o));
        assertFalse(ObjectUtils.safeEquals(o, null));
        assertTrue(ObjectUtils.safeEquals(0, 0));
        String s1 = new String("hi"), s2 = new String("hi"), s3 = new String("bye");
        assertTrue(ObjectUtils.safeEquals(s1, s2));
        assertFalse(ObjectUtils.safeEquals(s1, s3));
    }
}
