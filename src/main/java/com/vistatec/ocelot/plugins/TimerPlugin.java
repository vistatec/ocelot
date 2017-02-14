package com.vistatec.ocelot.plugins;

import java.awt.Component;

public interface TimerPlugin extends Plugin {
	
	public void startTimer();
	
	public double stopTimer();
	
	public void resetTimer();
	
	public double getSeconds();
	
	public double getMinutes();
	
	public double getHours();
	
	public Component getTimerWidget();
	
	public void recordUserActivity();
	
}


