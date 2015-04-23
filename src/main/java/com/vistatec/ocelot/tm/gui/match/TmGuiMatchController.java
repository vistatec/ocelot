package com.vistatec.ocelot.tm.gui.match;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.query.MatchType;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.tm.pensieve.common.TmHit;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.common.TranslationUnitVariant;

import org.apache.log4j.Logger;

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

	private ConcordanceSearchPanel concordancePanel;
	
	private TranslationsPanel translationsPanel;

	private JTabbedPane tmPanel;
	
	private Container tmPanelContainer;
	
	public TmGuiMatchController(final TmService tmService,
			final OcelotEventQueue eventQueue) {

		this.tmService = tmService;
		this.eventQueue = eventQueue;
	}

	public List<TmMatch> getFuzzyMatches(List<SegmentAtom> currentSelection) {
		List<TmMatch> matches = null;
		try {
			matches = tmService.getFuzzyTermMatches(currentSelection);
			// TODO delete --- only for test purpose
//			matches = createStubTmMatchList();
//			Collections.shuffle(matches);
		} catch (IOException e) {
			Logger.getLogger(TmGuiMatchController.class).trace(
					"Error while retrieving fuzzy matches.", e);

		}
		return matches;
	}

	public List<TmMatch> getConcordanceMatches(
			List<SegmentAtom> currentSelection) {

		List<TmMatch> matches = null;
		try {
			matches = tmService.getConcordanceMatches(currentSelection);
			// TODO delete --- only for test purpose
			matches = createStubTmMatchList();
		} catch (IOException e) {
			Logger.getLogger(TmGuiMatchController.class).trace(
					"Error while retrieving concordance search matches.", e);
			JOptionPane
					.showMessageDialog(
							concordancePanel.getAttachedComponent(),
							"An error has occured while finding concordance search matches.",
							"Concordance Search Error",
							JOptionPane.ERROR_MESSAGE);
		}
		return matches;
	}
	

	//TODO delete - only for test purpose
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
		hit = new TmHit(tu, MatchType.CONCORDANCE, 0.91f);
		match = new PensieveTmMatch("TM 1", hit);
		list.add(match);
		sourceFragment = new TextFragment("banana apple");
		targetFragment = new TextFragment("plátano manzana");
		source = new TranslationUnitVariant(sourceLocale, sourceFragment);
		target = new TranslationUnitVariant(targetLocale, targetFragment);
		tu = new TranslationUnit(source, target);
		hit = new TmHit(tu, MatchType.CONCORDANCE, 0.65f);
		match = new PensieveTmMatch("TM 3", hit);
		list.add(match);
		sourceFragment = new TextFragment(
				"Midway upon the journey of our life I found myself within a forest dark, For the straightforward pathway had been lost. Ah me! how hard a thing it is to say What was this forest savage, rough, and stern, Which in the very thought renews the fear. So bitter is it, death is little more; But of the good to treat, which there I found, Speak will I of the other things I saw there.");
		targetFragment = new TextFragment(
				"Nel mezzo del cammin di nostra vita mi ritrovai per una selva oscura, ché la diritta via era smarrita. Ahi quanto a dir qual era è cosa dura esta selva selvaggia e aspra e forte che nel pensier rinova la paura! Tant' è amara che poco è più morte; ma per trattar del ben ch'i' vi trovai, dirò de l'altre cose ch'i' v'ho scorte.");
		source = new TranslationUnitVariant(sourceLocale, sourceFragment);
		target = new TranslationUnitVariant(targetLocale, targetFragment);
		tu = new TranslationUnit(source, target);
		hit = new TmHit(tu, MatchType.CONCORDANCE, 0.55f);
		match = new PensieveTmMatch("TM 3", hit);
		list.add(match);
		return list;
	}

	public void performConcordanceSearch(final String text) {

		concordancePanel.setTextAndPerformConcordanceSearch(text);
	}

	public Component getConcordancePanel() {
		if (concordancePanel == null) {
			concordancePanel = new ConcordanceSearchPanel(this);
		}
		return concordancePanel.getAttachedComponent();
	}

	public void replaceTarget(final SegmentVariant newTarget) {

		if (currSelectedSegment != null) {
			SegmentVariant textContainerVariant = new TextContainerVariant(
					new TextContainer(newTarget.getDisplayText()));
			eventQueue.post(new SegmentTargetUpdateFromMatchEvent(
					currSelectedSegment, textContainerVariant));
		}
	}

	public synchronized void setSelectedSegment(OcelotSegment selectedSegment) {
		if(!selectedSegment.equals(currSelectedSegment)){
			this.currSelectedSegment = selectedSegment;
			if(translationsPanel != null){
				translationsPanel.setLoading();
				List<TmMatch> translations = getFuzzyMatches(selectedSegment.getSource().getAtoms());
				translationsPanel.setTranslationSearchResults(translations);
			}
		}
	}
	
	public Component getTmPanel(){
		
		if(tmPanel == null){
			tmPanel = new JTabbedPane();
			
		}
		if(translationsPanel == null){
			translationsPanel = new TranslationsPanel(this);
		}
		tmPanel.add(translationsPanel.getAttachedComponent());
		if(concordancePanel == null){
			concordancePanel = new ConcordanceSearchPanel(this);
		}
		tmPanel.add(concordancePanel.getAttachedComponent());
		tmPanel.addContainerListener(new ContainerListener() {
			
			@Override
			public void componentRemoved(ContainerEvent e) {
				System.out.println("Component Removed");		
				if(tmPanel.getTabCount() == 0){
					System.out.println("0 tabs");
					tmPanelContainer = tmPanel.getParent();
					tmPanelContainer.remove(tmPanel);
					tmPanelContainer.repaint();
				}
			}
			
			@Override
			public void componentAdded(ContainerEvent e) {
				System.out.println("Component Added");
				if(tmPanel.getTabCount() == 1){
					tmPanelContainer.add(tmPanel);
					tmPanelContainer.repaint();
					tmPanelContainer = null;
				}
			}
		});
		return tmPanel;
	}
	
	public Component getTabbedContainer(){
		return tmPanel;
	}
	
	public void selectConcordanceTab(){
		tmPanel.setSelectedComponent(concordancePanel.getAttachedComponent());
	}
	
	

}
