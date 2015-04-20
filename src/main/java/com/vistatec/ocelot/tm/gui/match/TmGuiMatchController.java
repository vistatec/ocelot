package com.vistatec.ocelot.tm.gui.match;

import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.tm.pensieve.common.TmHit;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.common.TranslationUnitVariant;

import com.vistatec.ocelot.events.SegmentTargetUpdateFromMatchEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.segment.model.okapi.TextContainerVariant;
import com.vistatec.ocelot.tm.TmMatch;
import com.vistatec.ocelot.tm.TmService;
import com.vistatec.ocelot.tm.gui.TmGuiController;
import com.vistatec.ocelot.tm.okapi.PensieveTmMatch;

public class TmGuiMatchController extends TmGuiController {

	private TmService tmService;
	
	private OcelotEventQueue eventQueue;
	
	private OcelotSegment currSelectedSegment;
	
	private ConcordanceDetachablePanel concordancePanel;
	
	public TmGuiMatchController(final TmService tmService, final OcelotEventQueue eventQueue) {

		this.tmService = tmService;
		this.eventQueue = eventQueue;
	}

	public List<TmMatch> getFuzzyMatches(List<SegmentAtom> currentSelection) {
		List<TmMatch> matches = null;
		try {
			matches = tmService.getFuzzyTermMatches(currentSelection);
			// TODO delete --- only for test purpose
			matches = createStubTmMatchList();
		} catch (IOException e) {
			// TODO prompt error message to the user.
		}
		return matches;
	}

	public List<TmMatch> getConcordanceMatches(
			List<SegmentAtom> currentSelection) {

		List<TmMatch> matches = null;
		try {
			matches = tmService.getConcordanceMatches(currentSelection);
		} catch (IOException e) {
			// TODO prompt error message to the user.
		}
		return matches;
	}

	private List<TmMatch> createStubTmMatchList() {

		List<TmMatch> list = new ArrayList<TmMatch>();

		LocaleId sourceLocale = new LocaleId(Locale.ENGLISH);
		TextFragment sourceFragment = new TextFragment("apple orange pear");
		LocaleId targetLocale = new LocaleId(new Locale("es-ES"));
		TextFragment targetFragment = new TextFragment("manzana narajna pera");
		TranslationUnitVariant source = new TranslationUnitVariant(
				sourceLocale, sourceFragment);
		TranslationUnitVariant target = new TranslationUnitVariant(
				targetLocale, targetFragment);
		TranslationUnit tu = new TranslationUnit(source, target);
		TmHit hit = new TmHit(tu, MatchType.CONCORDANCE, 1);
		TmMatch match = new PensieveTmMatch("TM 1", hit);
		list.add(match);
		sourceFragment = new TextFragment("orange apple pear");
		targetFragment = new TextFragment("narajna manzana pera");
		source = new TranslationUnitVariant(sourceLocale, sourceFragment);
		target = new TranslationUnitVariant(targetLocale, targetFragment);
		tu = new TranslationUnit(source, target);
		hit = new TmHit(tu, MatchType.CONCORDANCE, 1);
		match = new PensieveTmMatch("TM 1", hit);
		list.add(match);
		sourceFragment = new TextFragment("banana apple");
		targetFragment = new TextFragment("plátano manzana");
		source = new TranslationUnitVariant(sourceLocale, sourceFragment);
		target = new TranslationUnitVariant(targetLocale, targetFragment);
		tu = new TranslationUnit(source, target);
		hit = new TmHit(tu, MatchType.CONCORDANCE, 1);
		match = new PensieveTmMatch("TM 3", hit);
		list.add(match);
		return list;
	}

	public void performConcordanceSearch(final String text){
		
		concordancePanel.setTextAndPerformConcordanceSearch(text);
	}
	
	public void clearConcordancePanel(){
		
		
	}
	
	public Component getConcordancePanel() {
		if(concordancePanel == null){
			concordancePanel = new ConcordanceDetachablePanel(this);
		}
		return concordancePanel.getAttachedComponent();
	}
	
	public void replaceTarget(final SegmentVariant newTarget ){
		
		if(currSelectedSegment != null){
			SegmentVariant textContainerVariant = new TextContainerVariant(new TextContainer(newTarget.getDisplayText()));
			eventQueue.post(new SegmentTargetUpdateFromMatchEvent(currSelectedSegment, textContainerVariant));
		}
	}
	
	public void setSelectedSegment(OcelotSegment selectedSegment){
		this.currSelectedSegment = selectedSegment;
	}

}
