package com.vistatec.ocelot;

import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.events.LQIAdditionEvent;
import com.vistatec.ocelot.events.LQIRemoveEvent;
import com.vistatec.ocelot.events.SegmentEditEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.storage.gui.EditedSegmentsNoLqiWarningFrame;

public class SegmentErrorChecker implements OcelotEventQueueListener {

	private final Logger logger = LoggerFactory.getLogger(SegmentErrorChecker.class);
	
	private List<EditedSegment> editedSegmentsList;
	
	private EditedSegmentsNoLqiWarningFrame dialog;

	
	public SegmentErrorChecker() {
		editedSegmentsList = new ArrayList<EditedSegment>();
	}

	@Subscribe
	public void onSegmentEdited(SegmentEditEvent event) {

		logger.debug("Segment edited");
		switch (event.getEditType()) {
		case SegmentEditEvent.TARGET_CHANGED:
			logger.debug("Target changed");
			segmentEdited(event.getSegment().getSegmentNumber());
			break;
		case SegmentEditEvent.TARGET_RESET:
			logger.debug("Target reset");
			segmentReset(event.getSegment().getSegmentNumber());
			break;
		default:
			break;
		}
	}

	@Subscribe
	public void onLqiAdded(LQIAdditionEvent event) {
		logger.debug("LQI Added");
		lqiAdded(event.getSegment().getSegmentNumber(), event.getLQI());
	}

	@Subscribe
	public void onLqiDeleted(LQIRemoveEvent event) {
		logger.debug("LQI Deleted");
		lqiRemoved(event.getSegment().getSegmentNumber(), event.getLQI());
	}

	private void segmentEdited(int segNumber) {

		EditedSegment editedSegment = getEditedSegmentObject(segNumber);
		editedSegment.setSegmentEdited(true);
	}

	private void segmentReset(int segNumber) {

		EditedSegment editedSegment = getEditedSegmentObject(segNumber);
		editedSegment.setSegmentEdited(false);
	}

	private void lqiAdded(int segNumber, LanguageQualityIssue lqi) {

		EditedSegment editedSegment = getEditedSegmentObject(segNumber);
		editedSegment.addLqi(lqi);
	}

	private void lqiRemoved(int segNumber, LanguageQualityIssue lqi) {
		EditedSegment editedSegment = getEditedSegmentObject(segNumber);
		editedSegment.removeLqi(lqi);
	}

	private EditedSegment getEditedSegmentObject(int segNumber) {
		EditedSegment editedSegment = new EditedSegment(segNumber);
		if (editedSegmentsList.contains(editedSegment)) {
			editedSegment = editedSegmentsList.get(editedSegmentsList.indexOf(editedSegment));
		} else {
			editedSegmentsList.add(editedSegment);
		}
		return editedSegment;
	}

	public void clear() {
		System.out.println("Clear");
		editedSegmentsList.clear();
	}

	private List<Integer> getEditedSegNumbersWithNoLqi() {

		List<Integer> segNumbers = new ArrayList<>();
		for (EditedSegment editedSeg : editedSegmentsList) {
			if (editedSeg.isSegmentEdited()
					&& (editedSeg.getCreatedLqiList() == null || editedSeg.getCreatedLqiList().isEmpty())) {
				segNumbers.add(editedSeg.getSegmentNumber());
			}
		}
		return segNumbers;
	}
	
	public boolean checkIncompleteEditedSegments(Window currentFrame, OcelotEventQueue eventQueue){
		logger.debug("Checking for incomplete edited segments...");
		if(dialog != null){
			dialog.close();
		}
		boolean segmentsOk = true;
		List<Integer> incompleteSegmentNumbers = getEditedSegNumbersWithNoLqi();
		if(!incompleteSegmentNumbers.isEmpty()){
			logger.debug("Found " + incompleteSegmentNumbers.size() + " incomplete segments.");
			segmentsOk = false;
			dialog = new EditedSegmentsNoLqiWarningFrame(currentFrame, incompleteSegmentNumbers, eventQueue);
			SwingUtilities.invokeLater(dialog);
		}
		return segmentsOk;
	}

}

class EditedSegment {

	private int segmentNumber;

	private boolean segmentEdited;

	private List<LanguageQualityIssue> createdLqiList;

	public EditedSegment(int segmentNumber) {
		this.segmentNumber = segmentNumber;
	}

	public boolean isSegmentEdited() {
		return segmentEdited;
	}

	public void setSegmentEdited(boolean segmentEdited) {
		this.segmentEdited = segmentEdited;
	}

	public List<LanguageQualityIssue> getCreatedLqiList() {
		return createdLqiList;
	}

	public void setCreatedLqiList(List<LanguageQualityIssue> createdLqiList) {
		this.createdLqiList = createdLqiList;
	}

	public void addLqi(LanguageQualityIssue lqi) {
		if (createdLqiList == null) {
			createdLqiList = new ArrayList<LanguageQualityIssue>();
		}
		createdLqiList.add(lqi);
	}

	public void removeLqi(LanguageQualityIssue lqi) {
		if (createdLqiList != null && createdLqiList.contains(lqi)) {
			createdLqiList.remove(lqi);
		}
	}

	public int getSegmentNumber() {
		return segmentNumber;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EditedSegment) {
			return ((EditedSegment) obj).segmentNumber == segmentNumber;
		} else {
			return super.equals(obj);
		}
	}

	@Override
	public int hashCode() {
		return segmentNumber;
	}
}
