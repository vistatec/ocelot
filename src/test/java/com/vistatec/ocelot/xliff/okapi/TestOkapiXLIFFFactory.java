package com.vistatec.ocelot.xliff.okapi;

import java.io.File;

import org.junit.Test;
import static org.junit.Assert.*;

import com.vistatec.ocelot.xliff.XLIFFFactory;
import com.vistatec.ocelot.xliff.XLIFFVersion;

public class TestOkapiXLIFFFactory {

    @Test
    public void testDontBeFooledByItsVersionAttribute() throws Exception {
        File testFile = new File(getClass().getResource("/empty_xliff_12_with_itsVersion.xlf").toURI());
        XLIFFFactory factory = new OkapiXLIFFFactory();
        assertEquals(XLIFFVersion.XLIFF12, factory.detectXLIFFVersion(testFile));
    }
}
