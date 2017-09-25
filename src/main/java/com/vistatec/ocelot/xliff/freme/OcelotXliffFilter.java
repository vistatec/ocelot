package com.vistatec.ocelot.xliff.freme;

import net.sf.okapi.common.IParameters;
import net.sf.okapi.common.skeleton.ISkeletonWriter;
import net.sf.okapi.filters.xliff.XLIFFFilter;

public class OcelotXliffFilter extends XLIFFFilter {

	private OcelotFilterParameters params;

	public OcelotXliffFilter() {

		params = new OcelotFilterParameters();
	}

	@Override
	public void setParameters(IParameters params) {

		this.params = (OcelotFilterParameters) params;
	}

	@Override
	public ISkeletonWriter createSkeletonWriter() {

		if (params.isManageFremeAnnotations()) {
			return new FremeXliffSkeletonWriter(params);
		} else {
			return super.createSkeletonWriter();
		}
	}

	@Override
	public OcelotFilterParameters getParameters() {

		return params;
	}
}
