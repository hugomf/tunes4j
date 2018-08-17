package org.ocelot.tunes4j.event;

import java.util.EventListener;

public interface SourceListItemSelectionListener extends EventListener {

    public void selectItem(ItemSelectionEvent event);

}
