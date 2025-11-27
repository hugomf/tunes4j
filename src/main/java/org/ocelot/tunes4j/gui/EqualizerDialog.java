package org.ocelot.tunes4j.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ocelot.tunes4j.dsp.Equalizer;
import org.ocelot.tunes4j.player.Tunes4JAudioPlayer;

/**
 * Professional 10-band audio equalizer dialog with real-time control
 *
 * @author Cline
 */
public class EqualizerDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private Tunes4JAudioPlayer audioPlayer;
    private Equalizer equalizer;

    // UI Components
    private JSlider[] bandSliders = new JSlider[Equalizer.NUM_BANDS];
    private JLabel[] frequencyLabels = new JLabel[Equalizer.NUM_BANDS];
    private JLabel[] gainLabels = new JLabel[Equalizer.NUM_BANDS];
    private JComboBox<String> presetComboBox;
    private JButton resetButton;

    // Prevent recursive combo box updates
    private boolean updatingPresetSelection = false;

    // Layout
    private static final int SLIDER_HEIGHT = 200;
    private static final int SLIDER_WIDTH = 40;
    private static final int LABEL_HEIGHT = 20;

    public EqualizerDialog(JFrame parent, Tunes4JAudioPlayer audioPlayer) {
        super(parent, "Audio Equalizer", false); // Modal dialog
        this.audioPlayer = audioPlayer;

        // Handle null audio player (for testing/demo purposes)
        if (audioPlayer != null) {
            this.equalizer = audioPlayer.getEqualizer();
        } else {
            // Create a dummy equalizer for testing UI components
            this.equalizer = new Equalizer(44100.0, 2, 4096);
        }

        initializeUI();
        updateUI();
        setupListeners();

        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setResizable(false);

        // Make the dialog visible
        setVisible(true);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Title and description
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // Main equalizer controls
        JPanel equalizerPanel = createEqualizerPanel();
        add(equalizerPanel, BorderLayout.CENTER);

        // Preset and control buttons
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getParent());
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("10-Band Visual Equalizer", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));

        String desc = "<html>Real-time spectrum analysis & visualization<br><br>" +
                     "<b>Note:</b> This equalizer enriches the visual experience " +
                     "by processing spectrum data used for professional LCD style bars.</html>";
        JLabel descriptionLabel = new JLabel(desc, SwingConstants.CENTER);
        descriptionLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
        descriptionLabel.setForeground(Color.GRAY);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(descriptionLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createEqualizerPanel() {
        JPanel panel = new JPanel(null); // Absolute positioning for custom layout
        panel.setPreferredSize(new Dimension(600, 290));
        panel.setBorder(BorderFactory.createTitledBorder("Frequency Bands"));

        // Create sliders and labels for each band
        for (int i = 0; i < Equalizer.NUM_BANDS; i++) {
            int x = 50 + i * 55; // Center the bands horizontally

            // Frequency label (top)
            frequencyLabels[i] = new JLabel(formatFrequency(Equalizer.getBandFrequency(i)));
            frequencyLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            frequencyLabels[i].setBounds(x - 20, 10, 40, LABEL_HEIGHT);
            frequencyLabels[i].setFont(new Font("Monospaced", Font.PLAIN, 10));
            panel.add(frequencyLabels[i]);

            // Gain slider (middle)
            bandSliders[i] = createBandSlider();
            bandSliders[i].setBounds(x - 15, 35, SLIDER_WIDTH, SLIDER_HEIGHT);
            bandSliders[i].setOrientation(SwingConstants.VERTICAL);
            bandSliders[i].putClientProperty("JSlider.isFilled", Boolean.TRUE);
            panel.add(bandSliders[i]);

            // Gain value label (bottom)
            gainLabels[i] = new JLabel("0dB");
            gainLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            gainLabels[i].setBounds(x - 20, SLIDER_HEIGHT + 40, 40, LABEL_HEIGHT);
            gainLabels[i].setFont(new Font("Monospaced", Font.PLAIN, 10));
            panel.add(gainLabels[i]);
        }

        // Add range indicators
        addRangeLabels(panel);

        return panel;
    }

    private JSlider createBandSlider() {
        JSlider slider = new JSlider(
            (int)Equalizer.MIN_GAIN * 2,  // -40 (scaled for precision)
            (int)Equalizer.MAX_GAIN * 2,  // +40 (scaled for precision)
            0                             // Default 0dB
        );

        slider.setMajorTickSpacing(10); // 5dB ticks
        slider.setMinorTickSpacing(2);  // 1dB ticks
        slider.setPaintTicks(true);
        slider.setPaintLabels(false);
        slider.setSnapToTicks(false);

        // Custom labels for key points
        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        labels.put(0, new JLabel("0"));
        labels.put(20, new JLabel("+"));
        labels.put(-20, new JLabel("-"));
        labels.put(40, new JLabel("+"));
        labels.put(-40, new JLabel("-"));
        slider.setLabelTable(labels);

        return slider;
    }

    private void addRangeLabels(JPanel panel) {
        // Gain range indicators
        Font smallFont = new Font("Dialog", Font.PLAIN, 10);

        // Left side labels
        JLabel maxLabel = new JLabel("+20dB");
        maxLabel.setFont(smallFont);
        maxLabel.setBounds(15, 35, 35, LABEL_HEIGHT);
        maxLabel.setVerticalAlignment(SwingConstants.TOP);
        panel.add(maxLabel);

        Label zeroLabel = new Label("0dB");
        zeroLabel.setFont(smallFont);
        zeroLabel.setBounds(15, SLIDER_HEIGHT / 2 + 25, 35, LABEL_HEIGHT);
        panel.add(zeroLabel);

        Label minLabel = new Label("-20dB");
        minLabel.setFont(smallFont);
        minLabel.setBounds(15, SLIDER_HEIGHT + 15, 35, LABEL_HEIGHT);
        panel.add(minLabel);
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        // Preset combo box - handle null audioPlayer
        String[] presets;
        String currentPreset;
        if (audioPlayer != null) {
            presets = audioPlayer.getEqualizerPresetNames();
            currentPreset = audioPlayer.getEqualizerPreset();
        } else {
            presets = equalizer.getPresetNames();
            currentPreset = equalizer.getCurrentPreset();
        }
        presetComboBox = new JComboBox<>(presets);
        presetComboBox.setSelectedItem(currentPreset);
        panel.add(new JLabel("Presets:"));
        panel.add(presetComboBox);

        // Control buttons
        resetButton = new JButton("Reset");
        JButton closeButton = new JButton("Close");

        panel.add(resetButton);
        panel.add(closeButton);

        // Button actions
        closeButton.addActionListener(e -> setVisible(false));

        return panel;
    }

    private void setupListeners() {
        // Band slider listeners
        for (int i = 0; i < Equalizer.NUM_BANDS; i++) {
            final int bandIndex = i;
            bandSliders[i].addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    double gainValue = bandSliders[bandIndex].getValue() / 2.0; // Convert back from scaled value
                    if (audioPlayer != null) {
                        audioPlayer.setEqualizerBandGain(bandIndex, gainValue);
                    } else {
                        equalizer.setBandGain(bandIndex, gainValue);
                    }
                    updateGainLabel(bandIndex, gainValue);
                }
            });
        }

        // Preset combo box listener
        presetComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Prevent recursive updates when programmatically setting selection
                if (updatingPresetSelection) {
                    return;
                }

                String selectedPreset = (String) presetComboBox.getSelectedItem();
                System.out.println("User selected preset: " + selectedPreset);
                if (selectedPreset != null) {
                    if (audioPlayer != null) {
                        audioPlayer.loadEqualizerPreset(selectedPreset);
                        System.out.println("Loaded preset through audioPlayer: " + selectedPreset);
                    } else {
                        equalizer.loadPreset(selectedPreset);
                        System.out.println("Loaded preset directly in equalizer: " + selectedPreset);
                    }

                    // Update UI after preset load
                    String currentPreset = equalizer.getCurrentPreset();
                    System.out.println("Current equalizer preset after load: " + currentPreset);

                    // Prevent recursive call during UI update
                    updatingPresetSelection = true;
                    try {
                        presetComboBox.setSelectedItem(currentPreset);
                        System.out.println("Updated combo box selection to: " + currentPreset);
                        updateUI();
                    } finally {
                        updatingPresetSelection = false;
                    }
                }
            }
        });

        // Reset button listener
        resetButton.addActionListener(e -> {
            if (equalizer != null) {
                equalizer.reset();
                presetComboBox.setSelectedItem("Flat");
                updateUI();
            }
        });
    }

    private void updateUI() {
        // Update slider positions and gain labels
        if (presetComboBox != null) {
            for (int i = 0; i < Equalizer.NUM_BANDS; i++) {
                double gainValue = equalizer.getBandGain(i);
                bandSliders[i].setValue((int)(gainValue * 2)); // Scale for precision
                updateGainLabel(i, gainValue);
            }

            // Update preset combo box - prevent recursive action listener
            String currentPreset = equalizer.getCurrentPreset();
            if (!updatingPresetSelection) {
                presetComboBox.setSelectedItem(currentPreset);
            }
        }
    }

    private void updateGainLabel(int band, double gainValue) {
        gainLabels[band].setText(String.format("%.1fdB", gainValue));
    }

    private String formatFrequency(double freq) {
        if (freq < 1000) {
            return String.format("%.0fHz", freq);
        } else {
            return String.format("%.0fk", freq / 1000.0);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            updateUI(); // Refresh UI when dialog becomes visible
        }
        super.setVisible(visible);
    }
}
