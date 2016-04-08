package com.vistatec.ocelot.tm.okapi;

import java.io.File;
import java.io.IOException;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.Version;
import com.vistatec.ocelot.events.OpenFileEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.segment.model.CodeAtom;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.services.SegmentService;
import com.vistatec.ocelot.tm.TmTmxWriter;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.encoder.EncoderContext;
import net.sf.okapi.common.encoder.QuoteMode;
import net.sf.okapi.common.encoder.XMLEncoder;
import net.sf.okapi.common.filterwriter.TMXWriter;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;

/**
 * Export currently open file as a TMX file using the Okapi TMXWriter.
 */
public class OkapiTmxWriter implements TmTmxWriter, OcelotEventQueueListener {
    private final String CREATION_TOOL = "Ocelot-Okapi-Tmx-Writer";
    private final String SEGMENTATION = "sentence";

    private final SegmentService segService;

    private boolean hasOpenFile = false;
    private LocaleId sourceLang, targetLang;
    private XMLEncoder attributeEncoder = new XMLEncoder("UTF-8", "\n",
                    true, true, false, QuoteMode.ALL); 

    public OkapiTmxWriter(SegmentService segService) {
        this.segService = segService;
        attributeEncoder.getParameters();
    }

    @Subscribe
    public void setOpenFileLangs(OpenFileEvent fileEvent) {
        this.sourceLang = fileEvent.getDocument().getSrcLocale();
        this.targetLang = fileEvent.getDocument().getTgtLocale();
        this.hasOpenFile = true;
    }

    @Override
    public void exportTmx(File tmx) throws IOException {
        if (!this.hasOpenFile) {
            throw new IOException("No open file to export to TMX!");
        }

        TMXWriter writer = new TMXWriter(tmx.getAbsolutePath());

        writer.writeStartDocument(this.sourceLang, this.targetLang, CREATION_TOOL,
                Version.SOURCE_VERSION, SEGMENTATION, null, null);

        for (int row = 0; row < segService.getNumSegments(); row++) {
            OcelotSegment segment = segService.getSegment(row);
            writer.writeTU(convertVariantToTextFrag(segment.getSource()),
                    convertVariantToTextFrag(segment.getTarget()),
                    Integer.toString(segment.getSegmentNumber()),
                    null);
        }
        writer.writeEndDocument();
        writer.close();
    }

    private TextFragment convertVariantToTextFrag(SegmentVariant segVar) {
        TextFragment tFrag = new TextFragment();
        for (SegmentAtom atom : segVar.getAtoms()) {
            if (atom instanceof CodeAtom) {
                CodeAtom cAtom = (CodeAtom) atom;
                // The TMXWriter does not escape inline code content
                Code c = new Code(TextFragment.TagType.PLACEHOLDER,
                        attributeEncoder.encode(cAtom.getData(), EncoderContext.INLINE),
                        cAtom.getVerboseData());
                tFrag.append(c);
            } else {
                tFrag.append(atom.getData());
            }
        }
        return tFrag;
    }
}
