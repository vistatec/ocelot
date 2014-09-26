package com.vistatec.ocelot;

import java.io.File;
import java.io.FilenameFilter;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestXliffFileFilter {

    @Test
    public void test() {
        FilenameFilter filter = new XliffFileFilter();
        File dir = new File("foo");
        assertTrue(filter.accept(dir, "test.xlf"));
        assertTrue(filter.accept(dir, "test.XLF"));
        assertTrue(filter.accept(dir, "test.xliff"));
        assertTrue(filter.accept(dir, "test.XLIFF"));
        assertFalse(filter.accept(dir, "test.xl"));
        assertFalse(filter.accept(dir, "test.xlif"));
        assertFalse(filter.accept(dir, "test.xml"));
        assertFalse(filter.accept(dir, "test"));
    }
}
