package org.ocelot.tunes4j.event;

import java.util.EventListener;

public interface ProgressUpdateListener extends EventListener {

    public void updateProgress(PlayProgressEvent e);

}
