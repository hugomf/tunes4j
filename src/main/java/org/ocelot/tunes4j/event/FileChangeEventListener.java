package org.ocelot.tunes4j.event;

public interface  FileChangeEventListener  {
    public  void triggerOnAddNewFileEvent(FileChangeEvent event);
    public  void triggerOnDeleteFileEvent(FileChangeEvent event);
    public  void triggerOnChangeFileEvent(FileChangeEvent event);
 
}
