package com.vistatec.ocelot.tm.okapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.FileUtil;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.filters.tmx.TmxFilter;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.common.TranslationUnitVariant;
import net.sf.okapi.tm.pensieve.writer.PensieveWriter;

/**
 * Parse TMs in the TMX 1.4 file format and index the segments for use in
 * Pensieve.
 */
public class OkapiTmTmxImporter {
    private LocaleId sourceLocale, targetLocale;

    public void parse(File tmx, PensieveWriter writer) throws IOException {
        List<String> locales = FileUtil.guessLanguages(tmx.getAbsolutePath());
        sourceLocale = (locales.size() >= 1) ?
                LocaleId.fromString(locales.get(0)) : LocaleId.EMPTY;
        targetLocale = (locales.size() >= 2) ?
                LocaleId.fromString(locales.get(1)) : LocaleId.EMPTY;

        RawDocument rawDoc = new RawDocument(new FileInputStream(tmx), "UTF-8",
                sourceLocale, targetLocale);

        try (TmxFilter filter = new TmxFilter()) {
            filter.open(rawDoc);
            while (filter.hasNext()) {
                Event event = filter.next();

                if (event.isTextUnit()) {
                    ITextUnit tu = event.getTextUnit();
                    indexTranslationUnit(tu, writer);
                }

            }
        }
    }

    private void indexTranslationUnit(ITextUnit tu, PensieveWriter writer) {
        TextContainer srcTu = tu.getSource();
        TextContainer tgtTu = tu.getTarget(targetLocale);

        if (srcTu == null || tgtTu == null) {
            return;
        }

        TranslationUnit pensieveTu = new TranslationUnit(
                new TranslationUnitVariant(sourceLocale, srcTu.getUnSegmentedContentCopy()),
                new TranslationUnitVariant(targetLocale, tgtTu.getUnSegmentedContentCopy()));

        writer.indexTranslationUnit(pensieveTu);
    }
}
