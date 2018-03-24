package org.ocelot.tunes4j.event;

import java.util.EventListener;


public abstract class  PlayerEventListener implements EventListener {
    public  void fireOnPlayEvent(PlayerEvent evt){}
    public  void fireOnPauseEvent(PlayerEvent evt){}
    public  void fireOnStopEvent(PlayerEvent evt){}
    public  void fireOnSeekEvent(PlayerEvent evt){}
    public  void fireOnOpenEvent(PlayerEvent evt){}
    public  void fireOnPlayerChangedEvent(PlayerEvent evt){}
}
