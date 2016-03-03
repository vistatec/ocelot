package com.vistatec.ocelot.tm.okapi;

import java.io.File;
import java.net.URISyntaxException;

import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;

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

    /**
     * JMock custom action for a mock method that just returns the first
     * argument passed to the method.
     */
    static class ReturnFirstArgument extends CustomAction {

        public ReturnFirstArgument() {
            super("Return First Argument");
        }

        public ReturnFirstArgument(String description) {
            super(description);
        }

        @Override
        public Object invoke(Invocation invocation) throws Throwable {
            return invocation.getParameter(0);
        }
    }
}
