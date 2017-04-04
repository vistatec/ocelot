package com.vistatec.ocelot.gui.lqi;

import java.awt.Component;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JComboBox;

import org.junit.Assert;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.Scenario;
import org.netbeans.jemmy.operators.JComboBoxOperator;
import org.netbeans.jemmy.operators.JFrameOperator;

import com.vistatec.ocelot.Ocelot;
import com.vistatec.ocelot.config.ConfigurationManager;
import com.vistatec.ocelot.lqi.gui.ConfigurationItem;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;
import com.vistatec.ocelot.lqi.model.LQIGridConfigurations;

public class TestLqiConfigsTool implements Scenario {

	@Override
	public int runIt(Object arg0) {

		try {
			Ocelot.startOcelot();
			ConfigurationManager confManager = new ConfigurationManager();
			confManager.readAndCheckConfiguration(new File(System
			        .getProperty("user.home"), ".ocelot"));
			LQIGridConfigurations lqiGridConfs = confManager
			        .getLqiConfigService().readLQIConfig();
			Collections.sort(lqiGridConfs.getConfigurations(),
			        new Comparator<LQIGridConfiguration>() {

				        @Override
				        public int compare(LQIGridConfiguration o1,
				                LQIGridConfiguration o2) {
					        return o1.getName().compareTo(o2.getName());
				        }
			        });
			JFrameOperator ocelotMainFrame = new JFrameOperator("Ocelot");
			JComboBoxOperator lqiConfigsCombo = new JComboBoxOperator(
			        ocelotMainFrame, new ConfigComboboxChooser());
			Assert.assertEquals(lqiGridConfs.getConfigurations().size(),
			        lqiConfigsCombo.getItemCount());
			for (int i = 0; i < lqiConfigsCombo.getItemCount(); i++) {
				Assert.assertEquals(lqiGridConfs.getConfigurations().get(i)
				        .getName(), ((ConfigurationItem) lqiConfigsCombo
				        .getItemAt(i)).getConfiguration().getName());
			}

			Assert.assertEquals(
			        lqiGridConfs.getActiveConfiguration().getName(),
			        ((ConfigurationItem) lqiConfigsCombo.getSelectedItem())
			                .getConfiguration().getName());
			ConfigurationItem notActiveConf = getNotActiveConfigurationItem(lqiConfigsCombo);
			lqiConfigsCombo.setSelectedItem(notActiveConf);
			new QueueTool().waitEmpty(100);
			lqiGridConfs = confManager.getLqiConfigService().readLQIConfig();
			Assert.assertEquals(((ConfigurationItem)lqiConfigsCombo.getSelectedItem()).getConfiguration().getName(), lqiGridConfs.getActiveConfiguration().getName());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 1;
		}
		return 0;
	}
	
	private ConfigurationItem getNotActiveConfigurationItem(JComboBoxOperator lqiCombo){
		
		ConfigurationItem item = null;
		for(int i = 0; i<lqiCombo.getItemCount(); i++){
			if(!((ConfigurationItem)lqiCombo.getItemAt(i)).getConfiguration().isActive()){
				item = (ConfigurationItem) lqiCombo.getItemAt(i);
				break;
			}
		}
		return item;
	}

	public static void main(String[] argv) {
		String[] params = { "com.vistatec.ocelot.gui.lqi.TestLqiConfigsTool" };
		org.netbeans.jemmy.Test.main(params);
	}
}

class ConfigComboboxChooser implements ComponentChooser {

	@Override
	public boolean checkComponent(Component component) {

		return component instanceof JComboBox<?>
		        && ((JComboBox<?>) component).getSelectedItem() instanceof ConfigurationItem;
	}

	@Override
	public String getDescription() {
		return "Combo box listing all the configurations available for the LQI Grid.";
	}

}
