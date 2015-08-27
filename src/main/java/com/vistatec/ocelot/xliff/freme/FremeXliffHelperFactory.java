package com.vistatec.ocelot.xliff.freme;

public class FremeXliffHelperFactory {

	private static final String XLIFF_1_2_VERSION = "1.2";
	
	private static final String XLIFF_2_0_VERSION = "2.0";
	
	public static FremeXliffHelper createHelper(final String version){
		
		FremeXliffHelper helper = null;
		if(version != null){
			switch (version) {
			case XLIFF_1_2_VERSION:
				helper = new FremeXliff1_2Helper();
				break;
			case XLIFF_2_0_VERSION:
				helper = new FremeXliff2_0Helper();
				break;
			default:
				break;
			}
		}
		return helper;
	}
}
