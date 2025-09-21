package org.ocelot.tunes4j.service;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.ocelot.tunes4j.event.FileChangeEvent;
import org.ocelot.tunes4j.event.FileChangeEventListener;
import org.ocelot.tunes4j.event.FileChangeEventNotifier;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AbstractExecutionThreadService;

import ch.qos.logback.classic.Logger;

public class JavaNioFolderMonitorService extends AbstractExecutionThreadService implements FolderWatcherService {

    private static Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(JavaNioFolderMonitorService.class);

    private WatchService folderWatcher;
    private FileChangeEventNotifier notifier;
    private Path watchedFolderPath;
    private WatchKey watchKey;

    public void addFileChangeListener(FileChangeEventListener listener) {
        notifier.registerListener(listener);
    }

    public void removeFileChangeListener(FileChangeEventListener listener) {
        notifier.unregisterListener(listener);
    }

    public void subscribeFolder(File folder) {
        try {
            this.watchedFolderPath = folder.toPath();
            logger.info("Subscribing to folder: {}", folder.getPath());
            this.watchKey = watchedFolderPath.register(folderWatcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        } catch (IOException e) {
            String msg = String.format("Error while subscribing folder: %s", folder.getPath());
            logger.error(msg, e);
        }
    }

    public JavaNioFolderMonitorService() {
        try {
            this.folderWatcher = FileSystems.getDefault().newWatchService();
            this.notifier = new FileChangeEventNotifier();
        } catch (IOException e) {
            logger.error("Error creating watch service:", e);
        }
    }

    @Override
    public void run() {
        while (isRunning()) {
            try {
                WatchKey key = folderWatcher.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    syncFiles(event, key);
                }
                boolean valid = key.reset();
                if (!valid) {
                    logger.warn("WatchKey is no longer valid");
                    break;
                }
            } catch (InterruptedException e) {
                logger.error("Watch service interrupted:", e);
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.error("Error in watch service:", e);
            }
        }
    }

    private void syncFiles(WatchEvent<?> event, WatchKey key) {
        WatchEvent.Kind<?> kind = event.kind();
        
        if (kind == ENTRY_CREATE || kind == ENTRY_DELETE || kind == ENTRY_MODIFY) {
            @SuppressWarnings("unchecked")
            WatchEvent<Path> ev = (WatchEvent<Path>) event;
            Path filename = ev.context();
            Path child = watchedFolderPath.resolve(filename);
            File file = child.toFile();

            if (ENTRY_CREATE == kind) {
                this.notifier.notifyListeners(new FileChangeEvent(FileChangeEvent.Type.ON_ADDNEW, file));
            } else if (ENTRY_DELETE == kind) {
                this.notifier.notifyListeners(new FileChangeEvent(FileChangeEvent.Type.ON_DELETE, file));
            } else if (ENTRY_MODIFY == kind) {
                this.notifier.notifyListeners(new FileChangeEvent(FileChangeEvent.Type.ON_CHANGE, file));
            }
        }
    }

    @Override
    protected void triggerShutdown() {
        try {
            if (watchKey != null) {
                watchKey.cancel();
            }
            if (folderWatcher != null) {
                folderWatcher.close();
            }
        } catch (IOException e) {
            logger.error("Error closing watch service:", e);
        }
    }

    public static void main(String[] args) {
        JavaNioFolderMonitorService monitor = new JavaNioFolderMonitorService();
        monitor.subscribeFolder(new File("/Users/hugo/Documents/prueba"));

        monitor.addFileChangeListener(new FileChangeEventListener() {
            @Override
            public void triggerOnAddNewFileEvent(FileChangeEvent event) {
                logger.info(format("Event Add New: %s", event));
            }

            @Override
            public void triggerOnChangeFileEvent(FileChangeEvent event) {
                logger.info(format("Event Change: %s", event));
            }

            @Override
            public void triggerOnDeleteFileEvent(FileChangeEvent event) {
                logger.info(format("Event Delete: %s", event));
            }
        });

        monitor.startAsync();
    }
}