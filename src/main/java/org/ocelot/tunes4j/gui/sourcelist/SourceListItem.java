package org.ocelot.tunes4j.gui.sourcelist;

import javax.swing.ImageIcon;

import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SourceListItem {
	
	private String name;
	private ImageIcon nodeIcon;
	private boolean editable;
	
	public SourceListItem(String name, ImageIcon nodeIcon) {
		verifyName(name);
		this.name = name;
		this.nodeIcon = nodeIcon;
	}
	
	private void verifyName(String name) {
		if (StringUtils.isEmpty(name)) {
			throw new IllegalArgumentException("Category name cannot be empty");
		}
	}
	
}
