package com.vistatec.ocelot.plugins;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

public class PluginLicenseManager {

	private HashMap<Licensable, Integer> pluginLicenseStatus = new HashMap<>();
	private File licenseDir;

	public PluginLicenseManager(File licenseDir) {
		this.licenseDir = licenseDir;
	}

	public boolean verifyPluginLicense(Plugin plugin) {

		boolean verified = true;
		if (plugin instanceof Licensable) {
			int status = ((Licensable) plugin).checkLicense(licenseDir);
			pluginLicenseStatus.put((Licensable) plugin, status);
			verified = status == Licensable.AUTHORIZED || status == Licensable.NOT_LICENSED;
		}
		return verified;
	}

	public List<PluginLicenseError> getPluginLicenseErrors() {

		List<PluginLicenseError> errors = new ArrayList<>();
		for (Entry<Licensable, Integer> licenseStatus : pluginLicenseStatus.entrySet()) {
			if (licenseStatus.getValue().intValue() != Licensable.AUTHORIZED
					&& licenseStatus.getValue().intValue() != Licensable.NOT_LICENSED) {
				errors.add(new PluginLicenseError((Plugin) licenseStatus.getKey(), ""));
			}
		}
		return errors;
	}

	public Integer getLicenseStatus(Licensable plugin) {
		return pluginLicenseStatus.get(plugin);
	}

	public void checkLicenseWithSwingWorker(JCheckBox checkbox, JLabel licenseLabel, Licensable plugin, PropertyChangeListener listener) {

		LicenseWorker worker = new LicenseWorker(checkbox, licenseLabel, plugin, licenseDir, pluginLicenseStatus);
		worker.addPropertyChangeListener(listener);
		worker.execute();
	}

	public String getTextForError(int error) {
		String text = null;
		switch (error) {
		case Licensable.AUTHORIZED:
			text = "Verified";
			break;
		case Licensable.CANNOT_VERIFY_LICENSE:
			text = "Impossible to verify.";
			break;
		case Licensable.NOT_AUTHORIZED:
			text = "Not Authorized";
			break;
		case Licensable.NOT_REGISTERED:
			text = "Not Registered";
			break;
		default:
			break;
		}
		return text;
	}

	public boolean isLicenseValid(Licensable plugin) {
		return pluginLicenseStatus.get(plugin) == null || pluginLicenseStatus.get(plugin) == Licensable.AUTHORIZED
				|| pluginLicenseStatus.get(plugin) == Licensable.NOT_LICENSED;
	}
}

class LicenseWorker extends SwingWorker<Integer, Integer> {

	private JCheckBox checkbox;
	private JLabel licenseLabel;
	private Licensable plugin;
	private File licenseDir;
	private HashMap<Licensable, Integer> pluginLicenseStatus;

	public LicenseWorker(JCheckBox checkbox, JLabel licenseLabel, Licensable plugin, File licenseDir,
			HashMap<Licensable, Integer> pluginLicenseStatus) {

		this.checkbox = checkbox;
		this.licenseLabel = licenseLabel;
		this.plugin = plugin;
		this.licenseDir = licenseDir;
		this.pluginLicenseStatus = pluginLicenseStatus;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		int status = ((Licensable) plugin).checkLicense(licenseDir);
		pluginLicenseStatus.put((Licensable) plugin, status);
		return status;
	}

	@Override
	protected void done() {
		try {
			int status = get();
			if (status != Licensable.AUTHORIZED && status != Licensable.NOT_LICENSED) {
				checkbox.setSelected(false);
			}
			licenseLabel.setIcon(null);
			LicenseUtils.setLicenseLabel((Plugin)plugin, licenseLabel, status);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
}
