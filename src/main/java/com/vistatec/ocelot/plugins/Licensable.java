package com.vistatec.ocelot.plugins;

import java.io.File;

public interface Licensable {
	
	public static final int NOT_LICENSED = 0;
	
	public static final int NOT_AUTHORIZED = 1;
	
	public static final int NOT_REGISTERED = 2;
	
	public static final int CANNOT_VERIFY_LICENSE = 3;
	
	public static final int AUTHORIZED = 4;
	
	public int checkLicense(File licenseDir);
	
	
	public class LicenseException extends Exception {
		
		private static final long serialVersionUID = -6141396521587518573L;

		private int errorCode;

		public LicenseException(int errorCode, String message, Throwable cause) {
			super(message, cause);
			this.errorCode = errorCode;
		}

		public LicenseException(int errorCode, String message) {
			super(message);
			this.errorCode = errorCode;
		}
		
		public int getErrorCode(){
			return errorCode;
		}
		
		
	}
}

