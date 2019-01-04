package org.ocelot.tunes4j.gui.sourcelist;

import java.util.List;

import com.google.common.collect.Lists;


public class SourceListModel {
	
	private List<SourceListCategory> categories = Lists.newArrayList();
	
	public void addCategory(SourceListCategory category){
		this.categories.add(category);
	}
	
	public void addItemToCategory(SourceListCategory category, SourceListItem item){
		checkIfCategoryIsInTheModel(category);
		category.addItem(item);
	}
	
	public List<SourceListCategory> getCategories() {
		return this.categories;
	}
	
	public void checkIfCategoryIsInTheModel(SourceListCategory category) {
		if (categories.contains(category)) {
			throw new IllegalArgumentException("The category is not part of the model:" + category.getName());
		}
	}

	public void validateItemIsInModel(SourceListItem item) {
        boolean found = false;
        for (SourceListCategory category : this.categories) {
            found = category.containsItem(item);
            if (found) {
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("The given item is not part of this model.");
        }
    }


	
}