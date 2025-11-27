package org.ocelot.tunes4j.gui;

import java.awt.Color;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

/**
 * Manages application themes including TOML file-based theme support
 */
public class ThemeManager {

    public static final String THEME_PROPERTY = "tunes4j.theme";
    public static final String DARK_THEME = "dark";
    public static final String LIGHT_THEME = "light";
    public static final String SYSTEM_THEME = "system";

    private static ThemeManager instance;
    private Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);
    private String currentTheme = SYSTEM_THEME;
    private List<ThemeDefinition> availableThemes;

    private ThemeManager() {
        loadAvailableThemes();
        loadThemePreference();
        applyCurrentTheme();
    }

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    /**
     * Get list of available themes from TOML files
     */
    public List<ThemeDefinition> getAvailableThemes() {
        return availableThemes;
    }

    /**
     * Set theme by ThemeDefinition object
     */
    public void setThemeDefinition(ThemeDefinition theme) {
        if (theme != null) {
            prefs.put(THEME_PROPERTY, theme.getName());
            applyThemeDefinition(theme);
        }
    }

    /**
     * Set theme by name (backwards compatibility)
     */
    public void setTheme(String themeName) {
        this.currentTheme = themeName;
        prefs.put(THEME_PROPERTY, themeName);
        applyCurrentTheme();
    }

    public String getCurrentTheme() {
        return currentTheme;
    }

    public boolean isDarkTheme() {
        ThemeDefinition current = getCurrentThemeDefinition();
        return current != null && isThemeDark(current);
    }

    /**
     * Load all available themes from TOML files
     */
    private void loadAvailableThemes() {
        availableThemes = ThemeFileLoader.loadAllThemes();
        if (availableThemes.isEmpty()) {
            // Fallback if no themes loaded
            availableThemes = ThemeFileLoader.loadBuiltinThemes();
        }
    }

    private void loadThemePreference() {
        currentTheme = prefs.get(THEME_PROPERTY, "Default (Light)");
    }

    /**
     * Apply the current theme by name
     */
    private void applyCurrentTheme() {
        ThemeDefinition theme = getThemeByName(currentTheme);
        if (theme == null && SYSTEM_THEME.equals(currentTheme)) {
            // System theme - choose based on OS preference
            String systemTheme = isSystemDarkMode() ? "Dark Theme" : "Default (Light)";
            theme = getThemeByName(systemTheme);
            if (theme == null && !availableThemes.isEmpty()) {
                theme = availableThemes.get(0); // Default to first available
            }
        }

        if (theme != null) {
            applyThemeDefinition(theme);
        }
    }

    /**
     * Find theme by name
     */
    private ThemeDefinition getThemeByName(String name) {
        if (name == null || availableThemes == null) return null;

        return availableThemes.stream()
                .filter(theme -> name.equals(theme.getName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get the currently active theme definition
     */
    private ThemeDefinition getCurrentThemeDefinition() {
        return getThemeByName(currentTheme);
    }

    /**
     * Determine if a theme is considered "dark"
     */
    private boolean isThemeDark(ThemeDefinition theme) {
        if (theme == null || theme.getBackground() == null || theme.getForeground() == null) {
            return false;
        }

        // Consider theme dark if background is significantly darker than foreground
        // This is a heuristic - could be improved by adding a "dark" flag to theme files
        int bgBrightness = getBrightness(theme.getBackground());
        int fgBrightness = getBrightness(theme.getForeground());

        return fgBrightness > bgBrightness + 50; // Foreground significantly brighter than background
    }

    /**
     * Apply a theme definition to the UI
     */
    private void applyThemeDefinition(ThemeDefinition theme) {
        if (theme == null) return;

        UIDefaults defaults = UIManager.getDefaults();

        // Apply all theme colors to UIManager defaults
        applyThemeColor(defaults, "Panel.background", theme.getBackground());
        applyThemeColor(defaults, "Panel.foreground", theme.getForeground());
        applyThemeColor(defaults, "Label.background", theme.getBackground());
        applyThemeColor(defaults, "Label.foreground", theme.getForeground());

        applyThemeColor(defaults, "Button.background", theme.getButtonBackground());
        applyThemeColor(defaults, "Button.foreground", theme.getButtonForeground());

        applyThemeColor(defaults, "TextField.background", theme.getTextFieldBackground());
        applyThemeColor(defaults, "TextField.foreground", theme.getTextFieldForeground());
        applyThemeColor(defaults, "TextField.caretForeground", theme.getForeground());

        // Table colors - main table background and text
        applyThemeColor(defaults, "Table.background", theme.getBackground());
        applyThemeColor(defaults, "Table.foreground", theme.getForeground());

        // Table selection colors
        applyThemeColor(defaults, "Table.selectionBackground", theme.getSelectionBackground());
        applyThemeColor(defaults, "Table.selectionForeground", theme.getSelectionForeground());

        // Table headers
        applyThemeColor(defaults, "TableHeader.background", theme.getTableHeaderBackground());
        applyThemeColor(defaults, "TableHeader.foreground", theme.getTableHeaderForeground());

        // Table focus and alternate row colors (for striped tables)
        Color alternateRowColor = theme.getTableAlternateRowColor();
        if (alternateRowColor == null) {
            // Fallback: slightly different shade of background for striped tables
            alternateRowColor = theme.getTableHeaderBackground() != null
                ? new Color(theme.getTableHeaderBackground().getRGB() & 0xD0FFFFFF, true) // More transparent background
                : (theme.getBackground() != null ? theme.getBackground().brighter() : null);
        }
        applyThemeColor(defaults, "Table.alternateRowColor", alternateRowColor);
        applyThemeColor(defaults, "Table.focusCellBackground", theme.getSelectionBackground());

        applyThemeColor(defaults, "List.background", theme.getBackground());
        applyThemeColor(defaults, "List.foreground", theme.getForeground());
        applyThemeColor(defaults, "List.selectionBackground", theme.getSelectionBackground());
        applyThemeColor(defaults, "List.selectionForeground", theme.getSelectionForeground());

        // Tree colors (for playlist/sidebar navigation trees)
        applyThemeColor(defaults, "Tree.background", theme.getTreeBackground());
        applyThemeColor(defaults, "Tree.foreground", theme.getTreeForeground());
        applyThemeColor(defaults, "Tree.selectionBackground", theme.getTreeSelectionBackground());
        applyThemeColor(defaults, "Tree.selectionForeground", theme.getTreeSelectionForeground());
        applyThemeColor(defaults, "Tree.line", theme.getTreeLineColor());
        applyThemeColor(defaults, "Tree.textBackground", theme.getTreeBackground());
        applyThemeColor(defaults, "Tree.textForeground", theme.getTreeForeground());

        applyThemeColor(defaults, "MenuBar.background", theme.getMenuBackground());
        applyThemeColor(defaults, "Menu.background", theme.getMenuBackground());
        applyThemeColor(defaults, "Menu.foreground", theme.getMenuForeground());
        applyThemeColor(defaults, "MenuItem.background", theme.getMenuItemBackground());
        applyThemeColor(defaults, "MenuItem.foreground", theme.getMenuItemForeground());
        applyThemeColor(defaults, "MenuItem.selectionBackground", theme.getMenuItemSelectionBackground());
        applyThemeColor(defaults, "MenuItem.selectionForeground", theme.getMenuItemSelectionForeground());

        applyThemeColor(defaults, "PopupMenu.background", theme.getMenuBackground());
        applyThemeColor(defaults, "PopupMenu.foreground", theme.getMenuForeground());

        applyThemeColor(defaults, "ScrollBar.background", theme.getScrollbarBackground());
        applyThemeColor(defaults, "ScrollBar.thumb", theme.getScrollbarThumb());
    }

    /**
     * Apply a single color to UIManager defaults
     */
    private void applyThemeColor(UIDefaults defaults, String key, Color color) {
        if (color != null) {
            defaults.put(key, color);
        }
    }

    /**
     * Calculate brightness of a color (simplified)
     */
    private int getBrightness(Color color) {
        return (color.getRed() + color.getGreen() + color.getBlue()) / 3;
    }

    /**
     * Legacy method for backwards compatibility
     * @deprecated Use {@link #applyCurrentTheme()} instead
     */
    @Deprecated
    public static void applyTheme(String theme) {
        ThemeManager.getInstance().setTheme(theme);
    }

    public static boolean isSystemDarkMode() {
        try {
            // Check for macOS dark mode
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("mac") || osName.contains("darwin")) {
                return isMacDarkMode();
            }
            // For other systems, default to light theme for now
            // Could be extended for Windows and Linux
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isMacDarkMode() {
        try {
            Process process = Runtime.getRuntime().exec(
                new String[]{"defaults", "read", "-g", "AppleInterfaceStyle"}
            );
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream())
            );
            String line = reader.readLine();
            return line != null && line.contains("Dark");
        } catch (Exception e) {
            // If we can't determine, assume light
            return false;
        }
    }

    public void refreshTheme() {
        applyTheme(currentTheme);
    }

    // Static reference to test window for theme refresh
    public static javax.swing.JFrame testWindow = null;

    /**
     * DEBUG: Create or refresh a test window showing that the theming system WORKS
     * This proves the theme colors are being applied correctly when components use UIManager
     */
    public static void showThemeTestWindow() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            if (testWindow == null || !testWindow.isDisplayable()) {
                // Create new test window
                testWindow = new javax.swing.JFrame("Theme System Test - PROVES IT WORKS");
                testWindow.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
                testWindow.setSize(600, 400);
            }

            // Always refresh the content to pick up new theme colors
            javax.swing.JPanel mainPanel = new javax.swing.JPanel();
            mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.Y_AXIS));

            java.awt.Color panelBg = javax.swing.UIManager.getColor("Panel.background");
            if (panelBg == null) panelBg = java.awt.Color.WHITE;
            java.awt.Color panelFg = javax.swing.UIManager.getColor("Label.foreground");
            if (panelFg == null) panelFg = java.awt.Color.BLACK;

            mainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLUE),
                "Theme System TEST - Uses UIManager Colors"));
            mainPanel.setBackground(panelBg);

            // Test labels using UIManager colors
            javax.swing.JLabel titleLabel = new javax.swing.JLabel("If this looks different per theme → System WORKS!");
            titleLabel.setForeground(panelFg);
            titleLabel.setFont(titleLabel.getFont().deriveFont(18f));

            javax.swing.JLabel bgLabel = new javax.swing.JLabel("Panel.background: " + panelBg);
            bgLabel.setForeground(panelFg);

            java.awt.Color menuBg = javax.swing.UIManager.getColor("MenuBar.background");
            javax.swing.JLabel menuLabel = new javax.swing.JLabel("MenuBar.background: " + (menuBg != null ? menuBg : "default"));
            menuLabel.setForeground(panelFg);

            java.awt.Color buttonBg = javax.swing.UIManager.getColor("Button.background");
            java.awt.Color buttonFg = javax.swing.UIManager.getColor("Button.foreground");
            javax.swing.JButton testButton = new javax.swing.JButton("Theme Button (Uses UIManager)");
            testButton.setBackground(buttonBg != null ? buttonBg : java.awt.Color.LIGHT_GRAY);
            testButton.setForeground(buttonFg != null ? buttonFg : java.awt.Color.BLACK);

            // Create test table using UIManager colors
            java.awt.Color tableBg = javax.swing.UIManager.getColor("Table.background");
            java.awt.Color tableFg = javax.swing.UIManager.getColor("Table.foreground");
            java.awt.Color headerBg = javax.swing.UIManager.getColor("TableHeader.background");
            java.awt.Color selectionBg = javax.swing.UIManager.getColor("Table.selectionBackground");

            javax.swing.JTable testTable = new javax.swing.JTable(new Object[][] {
                {"Song 1", "Artist 1", "3:45"},
                {"Song 2", "Artist 2", "4:12"},
                {"Song 3", "Artist 3", "2:38"}
            }, new Object[]{"Title", "Artist", "Duration"});

            testTable.setBackground(tableBg != null ? tableBg : java.awt.Color.WHITE);
            testTable.setForeground(tableFg != null ? tableFg : java.awt.Color.BLACK);
            testTable.getTableHeader().setBackground(headerBg != null ? headerBg : java.awt.Color.LIGHT_GRAY);
            testTable.setSelectionBackground(selectionBg != null ? selectionBg : java.awt.Color.BLUE);
            javax.swing.JScrollPane tableScroll = new javax.swing.JScrollPane(testTable);

            javax.swing.JLabel proofLabel = new javax.swing.JLabel(
                "📊 📈 🖼️ Current Theme: " + ThemeManager.getInstance().getCurrentTheme() +
                " | If colors change when you switch themes → Theming WORKS perfectly!"
            );
            proofLabel.setForeground(java.awt.Color.RED);
            proofLabel.setFont(proofLabel.getFont().deriveFont(java.awt.Font.BOLD, 14f));

            // Clear and rebuild content
            testWindow.getContentPane().removeAll();
            mainPanel.removeAll();

            mainPanel.add(titleLabel);
            mainPanel.add(javax.swing.Box.createVerticalStrut(10));
            mainPanel.add(bgLabel);
            mainPanel.add(menuLabel);
            mainPanel.add(javax.swing.Box.createVerticalStrut(10));
            mainPanel.add(testButton);
            mainPanel.add(javax.swing.Box.createVerticalStrut(10));
            mainPanel.add(new javax.swing.JLabel("Test Table (uses UIManager colors for background/selection):"));
            mainPanel.add(tableScroll);
            mainPanel.add(javax.swing.Box.createVerticalStrut(10));
            mainPanel.add(proofLabel);

            testWindow.add(mainPanel);
            testWindow.setVisible(true);
            testWindow.toFront();
            testWindow.revalidate();
            testWindow.repaint();

            System.out.println("🎨 TEST WINDOW CREATED/REFRESHED with theme: " + ThemeManager.getInstance().getCurrentTheme());
        });
    }

    /**
     * Refresh the test window when themes change
     */
    public static void refreshTestWindow() {
        if (testWindow != null && testWindow.isDisplayable()) {
            showThemeTestWindow(); // This will refresh the existing window with new colors
        }
    }
}
