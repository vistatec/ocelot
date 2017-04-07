package com.vistatec.ocelot;

import java.io.File;
import java.io.FilenameFilter;

public class XliffFileFilter implements FilenameFilter {
    @Override
    public boolean accept(File dir, String name) {
        name = name.toLowerCase();
        return name.endsWith(".xlf") || name.endsWith(".xliff") ||
               name.endsWith(".sdlxliff");
    }
}
