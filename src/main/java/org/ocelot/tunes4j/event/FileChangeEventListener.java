package org.ocelot.tunes4j.event;

import java.util.EventListener;


public abstract class  FileChangeEventListener implements EventListener {
    public  void triggerOnAddNewFileEvent(FileChangeEvent event){}
    public  void triggerOnDeleteFileEvent(FileChangeEvent event){}
    public  void triggerOnChangeFileEvent(FileChangeEvent event){}
 
}
