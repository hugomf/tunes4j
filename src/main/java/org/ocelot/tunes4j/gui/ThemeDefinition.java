package org.ocelot.tunes4j.gui;

import java.awt.Color;

/**
 * Theme definition loaded from external TOML files
 */
public class ThemeDefinition {

    private String name;
    private String version;
    private String author;
    private String description;

    // UI Colors
    private Color background;
    private Color foreground;
    private Color selectionBackground;
    private Color selectionForeground;

    // Component-specific colors
    private Color buttonBackground;
    private Color buttonForeground;
    private Color textFieldBackground;
    private Color textFieldForeground;
    private Color tableHeaderBackground;
    private Color tableHeaderForeground;
    private Color menuBackground;
    private Color menuForeground;
    private Color menuItemBackground;
    private Color menuItemForeground;
    private Color menuItemSelectionBackground;
    private Color menuItemSelectionForeground;
    private Color scrollbarThumb;
    private Color scrollbarBackground;

    // Table-specific colors for striped/zebra tables
    private Color tableAlternateRowColor;

    // Tree-specific colors for tree lists/navigation
    private Color treeBackground;
    private Color treeForeground;
    private Color treeSelectionBackground;
    private Color treeSelectionForeground;
    private Color treeLineColor; // Color of connecting lines between nodes

    public ThemeDefinition() {
        // Default constructor
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Color getBackground() { return background; }
    public void setBackground(Color background) { this.background = background; }

    public Color getForeground() { return foreground; }
    public void setForeground(Color foreground) { this.foreground = foreground; }

    public Color getSelectionBackground() { return selectionBackground; }
    public void setSelectionBackground(Color selectionBackground) { this.selectionBackground = selectionBackground; }

    public Color getSelectionForeground() { return selectionForeground; }
    public void setSelectionForeground(Color selectionForeground) { this.selectionForeground = selectionForeground; }

    public Color getButtonBackground() { return buttonBackground; }
    public void setButtonBackground(Color buttonBackground) { this.buttonBackground = buttonBackground; }

    public Color getButtonForeground() { return buttonForeground; }
    public void setButtonForeground(Color buttonForeground) { this.buttonForeground = buttonForeground; }

    public Color getTextFieldBackground() { return textFieldBackground; }
    public void setTextFieldBackground(Color textFieldBackground) { this.textFieldBackground = textFieldBackground; }

    public Color getTextFieldForeground() { return textFieldForeground; }
    public void setTextFieldForeground(Color textFieldForeground) { this.textFieldForeground = textFieldForeground; }

    public Color getTableHeaderBackground() { return tableHeaderBackground; }
    public void setTableHeaderBackground(Color tableHeaderBackground) { this.tableHeaderBackground = tableHeaderBackground; }

    public Color getTableHeaderForeground() { return tableHeaderForeground; }
    public void setTableHeaderForeground(Color tableHeaderForeground) { this.tableHeaderForeground = tableHeaderForeground; }

    public Color getMenuBackground() { return menuBackground; }
    public void setMenuBackground(Color menuBackground) { this.menuBackground = menuBackground; }

    public Color getMenuForeground() { return menuForeground; }
    public void setMenuForeground(Color menuForeground) { this.menuForeground = menuForeground; }

    public Color getMenuItemBackground() { return menuItemBackground; }
    public void setMenuItemBackground(Color menuItemBackground) { this.menuItemBackground = menuItemBackground; }

    public Color getMenuItemForeground() { return menuItemForeground; }
    public void setMenuItemForeground(Color menuItemForeground) { this.menuItemForeground = menuItemForeground; }

    public Color getMenuItemSelectionBackground() { return menuItemSelectionBackground; }
    public void setMenuItemSelectionBackground(Color menuItemSelectionBackground) { this.menuItemSelectionBackground = menuItemSelectionBackground; }

    public Color getMenuItemSelectionForeground() { return menuItemSelectionForeground; }
    public void setMenuItemSelectionForeground(Color menuItemSelectionForeground) { this.menuItemSelectionForeground = menuItemSelectionForeground; }

    public Color getScrollbarThumb() { return scrollbarThumb; }
    public void setScrollbarThumb(Color scrollbarThumb) { this.scrollbarThumb = scrollbarThumb; }

    public Color getScrollbarBackground() { return scrollbarBackground; }
    public void setScrollbarBackground(Color scrollbarBackground) { this.scrollbarBackground = scrollbarBackground; }

    public Color getTableAlternateRowColor() { return tableAlternateRowColor; }
    public void setTableAlternateRowColor(Color tableAlternateRowColor) { this.tableAlternateRowColor = tableAlternateRowColor; }

    public Color getTreeBackground() { return treeBackground; }
    public void setTreeBackground(Color treeBackground) { this.treeBackground = treeBackground; }

    public Color getTreeForeground() { return treeForeground; }
    public void setTreeForeground(Color treeForeground) { this.treeForeground = treeForeground; }

    public Color getTreeSelectionBackground() { return treeSelectionBackground; }
    public void setTreeSelectionBackground(Color treeSelectionBackground) { this.treeSelectionBackground = treeSelectionBackground; }

    public Color getTreeSelectionForeground() { return treeSelectionForeground; }
    public void setTreeSelectionForeground(Color treeSelectionForeground) { this.treeSelectionForeground = treeSelectionForeground; }

    public Color getTreeLineColor() { return treeLineColor; }
    public void setTreeLineColor(Color treeLineColor) { this.treeLineColor = treeLineColor; }

    @Override
    public String toString() {
        return name != null ? name : "Unnamed Theme";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThemeDefinition that = (ThemeDefinition) o;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
