package com.vistatec.ocelot.xliff.okapi;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonPart;

import com.vistatec.ocelot.config.UserProvenance;

public class Xliff12HeaderWriter {

	private static final String PHASE_NAME = "ocelot_1";

	private static final String PROCESS_NAME = "ocelot_review";

	private static final String LQI_TOOL_ID = "lqi";

	public void writeHeader(StartSubDocument subDocument, Double time,
			UserProvenance userProvenance, String lqiConfiguration) {

		if (subDocument.getSkeleton() != null && insertHeader(time, userProvenance)) {
			GenericSkeleton skeleton = (GenericSkeleton) subDocument
					.getSkeleton();
			if (!skeleton.toString().contains(
					XliffDocumentConstants.HEADER_START)) {
				insertHeader(subDocument);
			}
			checkAndInsertPhase(subDocument, userProvenance, lqiConfiguration);
			checkAndInsertTime(subDocument, time);

		}
	}

	private boolean insertHeader(Double time, UserProvenance userProvenance) {

		return time != null
				|| (userProvenance != null && (userProvenance.getRevPerson() != null || userProvenance
						.getRevOrg() != null));
	}

	private void checkAndInsertTime(StartSubDocument subDocument, Double time) {

		if (time != null) {
			GenericSkeleton skeleton = (GenericSkeleton) subDocument
					.getSkeleton();
			int headerEndPartIndex = -1;
			GenericSkeletonPart countGroupPart = null;
			for (GenericSkeletonPart part : skeleton.getParts()) {
				if(part.getData().toString().contains(XliffDocumentConstants.COUNT_START)){
					countGroupPart = part;
					break;
				} else 	if (part.getData().toString()
						.contains(XliffDocumentConstants.HEADER_END)) {
					headerEndPartIndex = skeleton.getParts().indexOf(part);
					break;
				}
			}
			if(countGroupPart != null){
				StringBuilder newCountGroupString = new StringBuilder();
				int startCountIndex = countGroupPart.getData().indexOf(XliffDocumentConstants.COUNT_START);
				int endCountIndex = countGroupPart.getData().indexOf(XliffDocumentConstants.COUNT_END, startCountIndex);
				newCountGroupString.append(countGroupPart.getData().substring(0, startCountIndex));
				newCountGroupString.append(XliffDocumentConstants.COUNT_START + time + XliffDocumentConstants.COUNT_END);
				newCountGroupString.append(countGroupPart.getData().substring(endCountIndex + XliffDocumentConstants.COUNT_END.length()));
				countGroupPart.setData(newCountGroupString.toString());
			} else if (headerEndPartIndex >= 0) {
				skeleton.getParts().add(
						headerEndPartIndex,
						new GenericSkeletonPart(getCountGroupString(time),
								subDocument, LocaleId.EMPTY));
			}
		}
	}

	private String getCountGroupString(Double time) {
		StringBuilder countString = new StringBuilder();
		countString.append(XliffDocumentConstants.COUNT_GROUP_START);
		countString.append(XliffDocumentConstants.COUNT_START);
		countString.append(time);
		countString.append(XliffDocumentConstants.COUNT_END);
		countString.append(XliffDocumentConstants.COUNT_GROUP_END);
		return countString.toString();
	}
	

	private void checkAndInsertPhase(StartSubDocument subDocument,
			UserProvenance userProvenance, String lqiConfiguration) {

		if (userProvenance != null
				&& (userProvenance.getRevPerson() != null || userProvenance
						.getRevOrg() != null)) {
			Property phaseProperty = subDocument
					.getProperty(Property.XLIFF_PHASE);
			if (phaseProperty != null) {
				if (phaseProperty.getValue().contains(
						XliffDocumentConstants.PHASE_NAME_ATTR + "=\""
								+ PHASE_NAME + "\"")) {
					replacePhaseReviewerData(phaseProperty, userProvenance,
							lqiConfiguration);
				} else {
					int endPhaseGroupIndex = phaseProperty.getValue().indexOf(
							XliffDocumentConstants.PHASE_GROUP_END);
					StringBuilder newPropValue = new StringBuilder();
					newPropValue.append(phaseProperty.getValue().substring(0,
							endPhaseGroupIndex));
					newPropValue.append(getPhasePropertyValue(userProvenance,
							lqiConfiguration));
					newPropValue.append(phaseProperty.getValue().substring(
							endPhaseGroupIndex));
					subDocument.setProperty(new Property(Property.XLIFF_PHASE,
							newPropValue.toString()));
				}
			} else {
				StringBuilder phasePropValue = new StringBuilder();
				phasePropValue.append(XliffDocumentConstants.PHASE_GROUP_START);
				phasePropValue.append(getPhasePropertyValue(userProvenance,
						lqiConfiguration));
				phasePropValue.append(XliffDocumentConstants.PHASE_GROUP_END);
				subDocument.setProperty(new Property(Property.XLIFF_PHASE,
						phasePropValue.toString()));
				insertPhaseSelfPropertyPlaceholder(subDocument);
			}
			if (lqiConfiguration != null) {
				checkAndInsertTool(subDocument, lqiConfiguration);
			}
		}

	}

	private void checkAndInsertTool(StartSubDocument subDocument,
			String lqiConfiguration) {

		Property toolProp = subDocument.getProperty(Property.XLIFF_TOOL);
		if (toolProp != null) {
			if (!toolProp.getValue().contains(
					XliffDocumentConstants.TOOL_NAME_ATTR + "=\""
							+ lqiConfiguration + "\"")) {
				toolProp.setValue(toolProp.getValue()
						+ getLqiToolPropValue(lqiConfiguration));
			}
		} else {
			subDocument.setProperty(new Property(Property.XLIFF_TOOL,
					getLqiToolPropValue(lqiConfiguration)));
			insertToolSelfPropertyPlaceholder(subDocument);
		}

	}

	private void insertToolSelfPropertyPlaceholder(StartSubDocument subDocument) {

		GenericSkeleton skeleton = (GenericSkeleton) subDocument.getSkeleton();
		int headerEndPartIndex = -1;
		for (GenericSkeletonPart part : skeleton.getParts()) {
			if (part.getData().toString()
					.contains(XliffDocumentConstants.HEADER_END)) {
				headerEndPartIndex = skeleton.getParts().indexOf(part);
				break;
			}
		}
		if (headerEndPartIndex >= 0) {
			skeleton.getParts().add(
					headerEndPartIndex,
					new GenericSkeletonPart(
							getPropertyPlaceHolderString(Property.XLIFF_TOOL),
							subDocument, LocaleId.EMPTY));
		}
	}

	private String getLqiToolPropValue(String lqiConfiguration) {

		StringBuilder lqiToolProp = new StringBuilder();
		lqiToolProp.append(XliffDocumentConstants.TOOL_START.replace(">", ""));
		lqiToolProp.append(" ");
		lqiToolProp.append(XliffDocumentConstants.TOOL_ID_ATTR);
		lqiToolProp.append("=\"");
		lqiToolProp.append(LQI_TOOL_ID);
		lqiToolProp.append("\" ");
		lqiToolProp.append(XliffDocumentConstants.TOOL_NAME_ATTR);
		lqiToolProp.append("=\"");
		lqiToolProp.append(lqiConfiguration);
		lqiToolProp.append("\">");
		lqiToolProp.append(XliffDocumentConstants.TOOL_END);
		return lqiToolProp.toString();
	}

	private void insertPhaseSelfPropertyPlaceholder(StartSubDocument subDocument) {

		GenericSkeleton skeleton = (GenericSkeleton) subDocument.getSkeleton();
		int headerPartIndex = -1;
		for (GenericSkeletonPart part : skeleton.getParts()) {
			if (part.getData().toString()
					.contains(XliffDocumentConstants.HEADER_START)) {
				headerPartIndex = skeleton.getParts().indexOf(part);
				break;
			}
		}
		if (skeleton.getParts().get(headerPartIndex + 1).getData().toString()
				.contains(XliffDocumentConstants.SKEL_PROPERTY_NAME)) {
			headerPartIndex++;
		}
		skeleton.getParts().add(
				headerPartIndex + 1,
				new GenericSkeletonPart(
						getPropertyPlaceHolderString(Property.XLIFF_PHASE),
						subDocument, LocaleId.EMPTY));
	}

	private String getPropertyPlaceHolderString(String property) {
		return "[" + XliffDocumentConstants.SELF_PROP_PLACEHOLDER + property
				+ "]";
	}

	private String getPhasePropertyValue(UserProvenance userProvenance,
			String lqiConfName) {

		StringBuilder phasePropValue = new StringBuilder();
		phasePropValue.append(XliffDocumentConstants.PHASE_START.replace(">",
				""));
		phasePropValue.append(" ");
		phasePropValue.append(XliffDocumentConstants.PHASE_NAME_ATTR);
		phasePropValue.append("=\"");
		phasePropValue.append(PHASE_NAME);
		phasePropValue.append("\" ");
		phasePropValue.append(XliffDocumentConstants.PROCESS_NAME_ATTR);
		phasePropValue.append("=\"");
		phasePropValue.append(PROCESS_NAME);
		phasePropValue.append("\"");
		if (userProvenance != null) {
			if (userProvenance.getRevPerson() != null) {
				phasePropValue.append(" ");
				phasePropValue.append(XliffDocumentConstants.CONTACT_NAME_ATTR);
				phasePropValue.append("=\"");
				phasePropValue.append(userProvenance.getProvRef());
				phasePropValue.append("\"");
			}
			if (userProvenance.getRevOrg() != null) {
				phasePropValue.append(" ");
				phasePropValue.append(XliffDocumentConstants.COMPANY_NAME_ATTR);
				phasePropValue.append("=\"");
				phasePropValue.append(userProvenance.getRevOrg());
				phasePropValue.append("\"");
			}
		}
		if (lqiConfName != null) {
			phasePropValue.append(" ");
			phasePropValue.append(XliffDocumentConstants.TOOL_ID_ATTR);
			phasePropValue.append("=\"");
			phasePropValue.append(LQI_TOOL_ID);
			phasePropValue.append("\"");

		}
		phasePropValue.append(">");
		phasePropValue.append(XliffDocumentConstants.PHASE_END);

		return phasePropValue.toString();
	}

	private void replacePhaseReviewerData(Property phaseProperty,
			UserProvenance userProvenance, String lqiConfiguration) {

		String phasePropValue = phaseProperty.getValue();
		StringBuilder newPhasePropValue = new StringBuilder();
		int targetPhaseStartIndex = phasePropValue
				.indexOf(XliffDocumentConstants.PHASE_START.replace(">", "")
						+ " " + XliffDocumentConstants.PHASE_NAME_ATTR + "=\""
						+ PHASE_NAME);
		int targetPhaseEndIndex = phasePropValue.indexOf(
				XliffDocumentConstants.PHASE_END, targetPhaseStartIndex);
		newPhasePropValue.append(phasePropValue.substring(0,
				targetPhaseStartIndex));
		newPhasePropValue.append(getPhasePropertyValue(userProvenance,
				lqiConfiguration));
		newPhasePropValue.append(phasePropValue.substring(targetPhaseEndIndex
				+ XliffDocumentConstants.PHASE_END.length()));
		phaseProperty.setValue(newPhasePropValue.toString());
	}

	private void insertHeader(StartSubDocument subDocument) {

		GenericSkeleton skeleton = (GenericSkeleton) subDocument.getSkeleton();
		GenericSkeletonPart bodyPart = null;
		for (GenericSkeletonPart skelPart : skeleton.getParts()) {
			if (skelPart.getData().toString()
					.contains(XliffDocumentConstants.BODY_START)) {
				bodyPart = skelPart;
				break;
			}
		}
		if (bodyPart != null) {
			String partString = bodyPart.getData().toString();
			int bodyIndex = partString
					.indexOf(XliffDocumentConstants.BODY_START);
			int partIndexInSkeleton = skeleton.getParts().indexOf(bodyPart);
			if (bodyIndex > 0) {
				bodyPart.setData(partString.substring(0, bodyIndex));
				partIndexInSkeleton++;
			}
			StringBuilder headerStartSkelPart = new StringBuilder();
			headerStartSkelPart.append(XliffDocumentConstants.HEADER_START);
			headerStartSkelPart.append("\n");
			skeleton.getParts().add(
					partIndexInSkeleton++,
					new GenericSkeletonPart(headerStartSkelPart.toString(),
							subDocument, LocaleId.EMPTY));
			StringBuilder headerEndSkelPart = new StringBuilder();
			headerEndSkelPart.append(XliffDocumentConstants.HEADER_END);
			headerEndSkelPart.append("\n");
			skeleton.getParts().add(
					partIndexInSkeleton++,
					new GenericSkeletonPart(headerEndSkelPart.toString(),
							subDocument, LocaleId.EMPTY));
			if (bodyIndex > 0) {
				skeleton.getParts().add(
						partIndexInSkeleton++,
						new GenericSkeletonPart(
								partString.substring(bodyIndex), subDocument,
								LocaleId.EMPTY));
			}

		}
	}
}
