package org.ocelot.tunes4j.gui;

import java.awt.Color;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moandjiezana.toml.Toml;

/**
 * Loads theme definitions from TOML configuration files
 */
public class ThemeFileLoader {

    private static final Logger logger = LoggerFactory.getLogger(ThemeFileLoader.class);
    private static final String THEMES_DIR = "themes";
    private static final String THEME_FILE_EXTENSION = ".toml";

    /**
     * Load a single theme from TOML file
     */
    public static ThemeDefinition loadThemeFromFile(String themeFileName) {
        String filePath = THEMES_DIR + "/" + themeFileName + THEME_FILE_EXTENSION;

        try (InputStream inputStream = ThemeFileLoader.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                logger.debug("Theme file not found: {}", filePath);
                return null;
            }

            Toml toml = new Toml().read(new InputStreamReader(inputStream));
            return parseTomlTheme(toml);

        } catch (Exception e) {
            logger.warn("Failed to load theme from file: {}", themeFileName, e);
            return null;
        }
    }

    /**
     * Load all available theme files from resources/themes directory
     * This method attempts to dynamically discover theme files from the JAR resources
     */
    public static List<ThemeDefinition> loadAllThemes() {
        List<ThemeDefinition> themes = new ArrayList<>();
        themes.addAll(loadBuiltinThemes()); // Add built-in themes first

        // Since we can't reliably list JAR resources dynamically at runtime,
        // we'll try to load known theme file patterns from our JAR resources

        try {
            // Try to load specific theme files we know about
            // This is a limitation of JAR resources - we can't walk the directory
            String[] knownThemes = {"default.toml", "gruvbox_dark.toml", "gruvbox_light.toml", "solarized_light.toml", "dracula.toml"};

            for (String themeFile : knownThemes) {
                try {
                    ThemeDefinition theme = loadThemeFromFile(themeFile.replace(".toml", ""));
                    if (theme != null && theme.getName() != null) {
                        themes.add(theme);
                        logger.debug("Loaded theme: {}", theme.getName());
                    }
                } catch (Exception e) {
                    logger.debug("Theme file {} not found or failed to load: {}", themeFile, e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load theme files", e);
        }

        // Remove duplicates by name (builtin takes precedence)
        Map<String, ThemeDefinition> uniqueThemes = new HashMap<>();
        for (ThemeDefinition theme : themes) {
            if (theme != null && theme.getName() != null && !uniqueThemes.containsKey(theme.getName())) {
                uniqueThemes.put(theme.getName(), theme);
            }
        }

        logger.info("Loaded {} total themes: {}", uniqueThemes.size(),
            uniqueThemes.values().stream().map(ThemeDefinition::getName).toArray(String[]::new));

        return new ArrayList<>(uniqueThemes.values());
    }

    /**
     * Load built-in themes (fallback themes hardcoded)
     * Public for use by ThemeManager
     */
    public static List<ThemeDefinition> loadBuiltinThemes() {
        List<ThemeDefinition> builtin = new ArrayList<>();

        // Dark theme
        ThemeDefinition darkTheme = new ThemeDefinition();
        darkTheme.setName("Dark Theme");
        darkTheme.setVersion("1.0");
        darkTheme.setBackground(new Color(43, 43, 43));
        darkTheme.setForeground(new Color(224, 224, 224));
        darkTheme.setSelectionBackground(new Color(75, 110, 175));
        darkTheme.setSelectionForeground(Color.WHITE);
        darkTheme.setButtonBackground(new Color(60, 60, 60));
        darkTheme.setButtonForeground(new Color(224, 224, 224));
        darkTheme.setTextFieldBackground(new Color(69, 69, 69));
        darkTheme.setTextFieldForeground(new Color(224, 224, 224));
        darkTheme.setTableHeaderBackground(new Color(60, 60, 60));
        darkTheme.setTableHeaderForeground(new Color(224, 224, 224));
        darkTheme.setMenuBackground(new Color(43, 43, 43));
        darkTheme.setMenuForeground(new Color(224, 224, 224));
        darkTheme.setMenuItemBackground(new Color(43, 43, 43));
        darkTheme.setMenuItemForeground(new Color(224, 224, 224));
        darkTheme.setMenuItemSelectionBackground(new Color(75, 110, 175));
        darkTheme.setMenuItemSelectionForeground(Color.WHITE);
        builtin.add(darkTheme);

        // Light theme
        ThemeDefinition lightTheme = new ThemeDefinition();
        lightTheme.setName("Light Theme");
        lightTheme.setVersion("1.0");
        lightTheme.setBackground(Color.WHITE);
        lightTheme.setForeground(Color.BLACK);
        lightTheme.setSelectionBackground(new Color(184, 207, 229));
        lightTheme.setSelectionForeground(Color.BLACK);
        lightTheme.setButtonBackground(new Color(240, 240, 240));
        lightTheme.setButtonForeground(Color.BLACK);
        lightTheme.setTextFieldBackground(Color.WHITE);
        lightTheme.setTextFieldForeground(Color.BLACK);
        lightTheme.setTableHeaderBackground(new Color(240, 240, 240));
        lightTheme.setTableHeaderForeground(Color.BLACK);
        lightTheme.setMenuBackground(new Color(240, 240, 240));
        lightTheme.setMenuForeground(Color.BLACK);
        lightTheme.setMenuItemBackground(new Color(240, 240, 240));
        lightTheme.setMenuItemForeground(Color.BLACK);
        lightTheme.setMenuItemSelectionBackground(new Color(184, 207, 229));
        lightTheme.setMenuItemSelectionForeground(Color.BLACK);
        builtin.add(lightTheme);

        return builtin;
    }

    /**
     * Parse TOML structure into ThemeDefinition
     */
    private static ThemeDefinition parseTomlTheme(Toml toml) {
        try {
            ThemeDefinition theme = new ThemeDefinition();

            // Theme metadata
            Toml themeSection = toml.getTable("theme");
            if (themeSection != null) {
                theme.setName(themeSection.getString("name", "Unnamed Theme"));
                theme.setVersion(themeSection.getString("version", "1.0"));
                theme.setAuthor(themeSection.getString("author"));
                theme.setDescription(themeSection.getString("description"));
            }

            // Colors - main
            Toml colorsSection = toml.getTable("colors");
            if (colorsSection != null) {
                theme.setBackground(parseColor(colorsSection.getString("background"), null));
                theme.setForeground(parseColor(colorsSection.getString("foreground"), null));
                theme.setSelectionBackground(parseColor(colorsSection.getString("selection_bg"), theme.getBackground()));
                theme.setSelectionForeground(parseColor(colorsSection.getString("selection_fg"), theme.getForeground()));

                // Component-specific colors with fallbacks
                theme.setButtonBackground(parseColor(colorsSection.getString("button_bg"), theme.getBackground()));
                theme.setButtonForeground(parseColor(colorsSection.getString("button_fg"), theme.getForeground()));
                theme.setTextFieldBackground(parseColor(colorsSection.getString("text_field_bg"), theme.getBackground()));
                theme.setTextFieldForeground(parseColor(colorsSection.getString("text_field_fg"), theme.getForeground()));

                theme.setMenuBackground(parseColor(colorsSection.getString("menu_bg"), theme.getBackground()));
                theme.setMenuForeground(parseColor(colorsSection.getString("menu_fg"), theme.getForeground()));
                theme.setMenuItemBackground(parseColor(colorsSection.getString("menu_item_bg"), theme.getBackground()));
                theme.setMenuItemForeground(parseColor(colorsSection.getString("menu_item_fg"), theme.getForeground()));
                theme.setMenuItemSelectionBackground(parseColor(colorsSection.getString("menu_item_selection_bg"), theme.getSelectionBackground()));
                theme.setMenuItemSelectionForeground(parseColor(colorsSection.getString("menu_item_selection_fg"), theme.getSelectionForeground()));

                theme.setScrollbarBackground(parseColor(colorsSection.getString("scrollbar_bg"), theme.getBackground()));
                theme.setScrollbarThumb(parseColor(colorsSection.getString("scrollbar_thumb"), theme.getButtonBackground()));
            }

            // UI-specific overrides
            Toml uiSection = toml.getTable("ui");
            if (uiSection != null) {
                theme.setTableHeaderBackground(parseColor(uiSection.getString("table_header_bg"), theme.getButtonBackground()));
                theme.setTableHeaderForeground(parseColor(uiSection.getString("table_header_fg"), theme.getButtonForeground()));
                theme.setTableAlternateRowColor(parseColor(uiSection.getString("table_alternate_row_bg"), theme.getBackground()));

                // Tree-specific colors
                theme.setTreeBackground(parseColor(uiSection.getString("tree_bg"), theme.getBackground()));
                theme.setTreeForeground(parseColor(uiSection.getString("tree_fg"), theme.getForeground()));
                theme.setTreeSelectionBackground(parseColor(uiSection.getString("tree_selection_bg"), theme.getSelectionBackground()));
                theme.setTreeSelectionForeground(parseColor(uiSection.getString("tree_selection_fg"), theme.getSelectionForeground()));
                theme.setTreeLineColor(parseColor(uiSection.getString("tree_line_color"), theme.getForeground()));
            }

            return theme;

        } catch (Exception e) {
            logger.error("Failed to parse theme TOML", e);
            return null;
        }
    }

    /**
     * Parse color from string (supports #RRGGBB and named colors)
     */
    private static Color parseColor(String colorStr) {
        return parseColor(colorStr, null);
    }

    private static Color parseColor(String colorStr, Color fallback) {
        if (colorStr == null || colorStr.trim().isEmpty()) {
            return fallback;
        }

        try {
            // Remove whitespace
            colorStr = colorStr.trim();

            // Check for hex colors (#RRGGBB or #RGB)
            if (colorStr.startsWith("#")) {
                return Color.decode(colorStr);
            }

            // Check for named colors
            switch (colorStr.toLowerCase()) {
                case "white": return Color.WHITE;
                case "black": return Color.BLACK;
                case "red": return Color.RED;
                case "green": return Color.GREEN;
                case "blue": return Color.BLUE;
                case "yellow": return Color.YELLOW;
                case "orange": return Color.ORANGE;
                case "gray": return Color.GRAY;
                case "grey": return Color.GRAY;
                default:
                    logger.warn("Unknown color name: {}, using fallback", colorStr);
                    return fallback != null ? fallback : Color.BLACK;
            }

        } catch (Exception e) {
            logger.warn("Failed to parse color: {}, using fallback", colorStr);
            return fallback != null ? fallback : Color.BLACK;
        }
    }

    /**
     * Validate theme file syntax
     */
    public static boolean validateThemeFile(String themeFileName) {
        try {
            ThemeDefinition theme = loadThemeFromFile(themeFileName);
            return theme != null && theme.getName() != null && !theme.getName().trim().isEmpty();
        } catch (Exception e) {
            logger.warn("Theme validation failed for: {}", themeFileName, e);
            return false;
        }
    }
}
