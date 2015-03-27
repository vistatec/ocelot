package com.vistatec.ocelot.tm.okapi;

import java.io.File;
import java.net.URISyntaxException;

public class OkapiTmTestHelpers {

    static File getTestOkapiTmDir() throws URISyntaxException {
        File packageDir = new File(OkapiTmTestHelpers.class.getResource("").toURI());
        return new File(packageDir, "test");
    }

    static void deleteDirectory(File dir) {
        if (dir.exists() && dir.isDirectory()) {
            for (File f : dir.listFiles()) {
                if (f.isDirectory()) {
                    deleteDirectory(f);
                } else {
                    f.delete();
                }
            }
            dir.delete();
        }
    }
}
