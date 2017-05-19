package com.vistatec.ocelot.plugins;

import java.awt.Color;

import javax.swing.JLabel;

public class LicenseUtils {
	
	public static void setLicenseLabel(Plugin plugin, JLabel licenseLabel, Integer licenseStatus){
    	
    	String text = "-";
    	Color foreground = licenseLabel.getForeground();
    	if(licenseStatus != null){
    		switch (licenseStatus) {
			case Licensable.AUTHORIZED:
				text = "Verified";
				foreground = new Color(8, 175, 36);
				break;
			case Licensable.CANNOT_VERIFY_LICENSE:
				text = "Impossible to verify.";
				foreground = Color.RED;
				break;
			case Licensable.NOT_AUTHORIZED:
				text = "Not Authorized";
				foreground = Color.RED;
				break;
			case Licensable.NOT_REGISTERED:
				text = "Not Registered";
				
				foreground = Color.RED;
				break;
			default:
				break;
			}
    	}
    	licenseLabel.setText(text);
    	licenseLabel.setForeground(foreground);
    }

}
