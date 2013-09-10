package com.spartansoftwareinc.plugins;

import com.spartansoftwareinc.vistatec.rwb.segment.SegmentController;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * View for managing active plugins.
 */
public class PluginManagerView extends JPanel implements Runnable, ActionListener, ItemListener {
    private static Logger LOG = LoggerFactory.getLogger(PluginManagerView.class);
    private JFrame frame;
    private JButton selectPluginDir;
    protected PluginManager pluginManager;
    private HashMap<JCheckBox, Plugin> checkboxToPlugin;
    protected SegmentController segmentController;

    public PluginManagerView(PluginManager pluginManager, Set<? extends Plugin> plugins, SegmentController segController) {
        super(new GridBagLayout());
        this.pluginManager = pluginManager;
        this.segmentController = segController;
        checkboxToPlugin = new HashMap<JCheckBox, Plugin>();
        setBorder(new EmptyBorder(10,10,10,10));

        GridBagConstraints gridBag = new GridBagConstraints();
        gridBag.anchor = GridBagConstraints.FIRST_LINE_START;

        selectPluginDir = new JButton("Set Plugin Directory");
        selectPluginDir.addActionListener(this);
        gridBag.gridx = 0;
        gridBag.gridy = 0;
        add(selectPluginDir, gridBag);

        JLabel title = new JLabel("Available Plugins:");
        title.setBorder(new EmptyBorder(10,0,0,0));
        gridBag.gridx = 0;
        gridBag.gridy = 1;
        add(title, gridBag);

        initPlugins(plugins);
    }

    public void initPlugins(Set<? extends Plugin> plugins) {
        GridBagConstraints gridBag = new GridBagConstraints();
        gridBag.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBag.gridwidth = 2;
        gridBag.gridx = 0;
        int gridy = 2;
        if (!checkboxToPlugin.isEmpty()) {
            for (JCheckBox pluginBox : checkboxToPlugin.keySet()) {
                remove(pluginBox);
            }
            checkboxToPlugin = new HashMap<JCheckBox, Plugin>();
        }
        for (Plugin plugin : plugins) {
            JCheckBox pluginBox = new JCheckBox(plugin.getPluginName());
            pluginBox.setSelected(pluginManager.isEnabled(plugin));
            pluginBox.addItemListener(this);
            gridBag.gridy = gridy++;
            add(pluginBox, gridBag);
            checkboxToPlugin.put(pluginBox, plugin);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();
        if (source.getClass().equals(JCheckBox.class)) {
            JCheckBox checkbox = (JCheckBox) source;
            pluginManager.setEnabled(checkboxToPlugin.get(checkbox),
                    e.getStateChange() == ItemEvent.SELECTED);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == selectPluginDir) {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    pluginManager.discover(fc.getSelectedFile());
                } catch (IOException ex) {
                    LOG.warn("Plugin directory IOException", ex);
                    JOptionPane.showMessageDialog(frame, "Error reading specified plugin directory.");
                }
            }
        }
    }

    @Override
    public void run() {
        frame = new JFrame("Plugin Manager");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.getContentPane().add(this);

        frame.pack();
        frame.setVisible(true);
    }
}
