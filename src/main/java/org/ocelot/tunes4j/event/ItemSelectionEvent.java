package org.ocelot.tunes4j.event;

import java.util.EventObject;

import org.ocelot.tunes4j.gui.sourcelist.SourceListItem;


@SuppressWarnings("serial")
public class ItemSelectionEvent extends EventObject {

	private SourceListItem item;
	
	public ItemSelectionEvent(Object source, SourceListItem item)  {
		super(source);
		this.item = item;
	}
	
	public SourceListItem getSourceListItem() {
		return item;
	}
}
