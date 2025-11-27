# Tunes4J Theme Files

This directory contains TOML-based theme files that customize the appearance of Tunes4J.

## File Structure

Theme files use the `.toml` extension and follow a structured format:

```toml
[theme]
name = "My Custom Theme"
version = "1.0"
author = "Your Name"
description = "Description of your theme"

[colors]
# Basic color scheme
background = "#282828"
foreground = "#EBDBB2"
selection_bg = "#689D6A"
selection_fg = "#EBDBB2"

# Component-specific colors (optional)
button_bg = "#504945"
button_fg = "#EBDBB2"
text_field_bg = "#3C3836"
text_field_fg = "#EBDBB2"

# Menu colors (optional)
menu_bg = "#282828"
menu_fg = "#EBDBB2"
menu_item_bg = "#282828"
menu_item_fg = "#EBDBB2"
menu_item_selection_bg = "#458588"
menu_item_selection_fg = "#EBDBB2"

# Scrollbars (optional)
scrollbar_bg = "#282828"
scrollbar_thumb = "#7C6F64"

[ui]
# UI-specific overrides for special components
table_header_bg = "#504945"
table_header_fg = "#EBDBB2"
table_alternate_row_bg = "#32302F"  # Striped table background

# Tree-specific colors (for playlist/navigation trees)
tree_bg = "#282828"
tree_fg = "#EBDBB2"
tree_selection_bg = "#458588"
tree_selection_fg = "#EBDBB2"
tree_line_color = "#504945"  # Lines connecting tree nodes
```

## Color Formats

Colors can be specified as:
- **Hex colors**: `#RRGGBB` (e.g., `#282828`)
- **Short hex**: `#RGB` (e.g., `#FFF` for white)
- **Named colors**: `white`, `black`, `red`, `green`, `blue`, `yellow`, `orange`, `gray`, `grey`

## Optional Sections

All sections except `[theme]` and basic `[colors]` are optional. Colors will fall back to derived values if not specified.

## Creating Your Own Theme

1. Copy an existing theme file (like `gruvbox_dark.toml`)
2. Rename it with a unique, descriptive name
3. Modify the colors according to your preference
4. Test by selecting your theme from View menu in Tunes4J

## Theme Priority

1. **Built-in themes** have the highest priority if they conflict with file-based themes
2. **File-based themes** are loaded from this directory
3. **Unique theme names** prevent conflicts
4. **Fallback themes** ensure the application always looks good

## Sharing Themes

Theme files are simple TOML files, making them easy to share with other Tunes4J users. Just share your `.toml` file and others can place it in their `themes/` directory.
