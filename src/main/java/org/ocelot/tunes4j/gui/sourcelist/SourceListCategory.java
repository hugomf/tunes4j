package org.ocelot.tunes4j.gui.sourcelist;

import java.util.Collections;
import java.util.List;

import org.junit.experimental.categories.Categories;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;

import lombok.Data;

@Data
public class SourceListCategory {

	private String name;
	private boolean collapsable;
	private List<SourceListItem> items = Lists.newArrayList();
	
	
	public SourceListCategory(String name) {
		this(name, true);
	}
	
	public SourceListCategory(String name, boolean collapsable) {
		verifyName(name);
		this.name = name;
		this.collapsable = collapsable;
	}
	
	public void addItem(SourceListItem item) {
		this.items.add(item);
	}
	
	public void removeItem(SourceListItem item) {
		this.items.remove(item);
	}
	
	public List<SourceListItem> getItems() {
		return Collections.unmodifiableList(this.items);
	}

	private void verifyName(String name) {
		if (StringUtils.isEmpty(name)) {
			throw new IllegalArgumentException("Category name cannot be empty");
		}
	}

	public int getChildCount() {
		return this.items.size();
	}

	public boolean containsItem(SourceListItem item) {
		return this.items.contains(item);
	}

}
