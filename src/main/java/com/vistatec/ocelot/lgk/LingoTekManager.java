package com.vistatec.ocelot.lgk;

import java.awt.Window;
import java.io.File;

import javax.swing.JOptionPane;

import com.vistatec.ocelot.config.json.LingoTekConfig;

/**
 * This class manages all the processes related to LingoTek, such as the
 * download from LGK function.
 */
public class LingoTekManager {

	/**
	 * The LingoTek service.
	 */
	private LingoTekService service;

	/** The LingoTek configuration parameters. */
	private final LingoTekConfig config;

	/** States if this manager is enabled. */
	private boolean enabled;

	/**
	 * Constructor.
	 * 
	 * @param config
	 *            the configuration parameters.
	 */
	public LingoTekManager(LingoTekConfig config) {
		this.config = config;
		enabled = isConfigured();
		if (enabled) {
			service = new LingoTekService(config.getLingotekCMSRoute(),
					config.getLgkAPIKey());
		}
	}

	/**
	 * Performs the download process from LingoTek.
	 * 
	 * @param currentWindow
	 *            the current opened window.
	 * @param languageCode
	 *            the language code
	 * @return the file downloaded.
	 */
	public File downloadFile(Window currentWindow, String languageCode) {

		File downloadedFile = null;
		if (languageCode == null || languageCode.isEmpty()) {
			JOptionPane
					.showMessageDialog(
							currentWindow,
							"Please, insert a language code in the Provenance window and try again.",
							"Missing language code",
							JOptionPane.WARNING_MESSAGE);
		} else {

			DownloadFromLgkDialog dialog = new DownloadFromLgkDialog(
					currentWindow);
			dialog.open();

			String documentId = dialog.getDocumentId();
			dialog.dispose();
			if (documentId != null && !documentId.isEmpty()) {
				try {
					downloadedFile = service.downloadFile(documentId.trim(),
							languageCode);
				} catch (LingoTekServiceException e) {
					int messageType = JOptionPane.INFORMATION_MESSAGE;
					if (e.getSeverity() == LingoTekServiceException.SEVERITY_ERROR) {
						messageType = JOptionPane.ERROR_MESSAGE;
					} else if (e.getSeverity() == LingoTekServiceException.SEVERITY_WARNING) {
						messageType = JOptionPane.WARNING_MESSAGE;
					}
					JOptionPane.showMessageDialog(currentWindow,
							e.getMessage(), "Download from LTK", messageType);
				}
			}
		}
		return downloadedFile;
	}

	private boolean isConfigured() {
		return config != null && config.isComplete();
	}

	/**
	 * Checks if this manager is enabled. Typically this manager is enabled if
	 * and only if all the LingoTek configuration parameters are defined.
	 * 
	 * @return <code>true</code> if the manager is enabled; <code>false</code>
	 *         otherwise.
	 */
	public boolean isEnabled() {
		return enabled;
	}

}
