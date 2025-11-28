package org.ocelot.tunes4j.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ocelot.tunes4j.dto.Song;
import org.ocelot.tunes4j.service.AudioService.AudioLibraryStats;
import org.ocelot.tunes4j.playlist.service.PlaylistService;
import org.ocelot.tunes4j.playlist.service.PlaylistService.PlaylistLibraryStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Application Service - Cross-cutting Concerns and Application-wide Operations.
 *
 * Key responsibilities:
 * - File import operations
 * - Application configuration management
 * - System monitoring and diagnostics
 * - Cross-context coordination (Audio & Playlist services)
 * - Global application state management
 *
 * Acts as the application-service layer coordinating multiple bounded contexts.
 */
@Service
public class ApplicationService {

    private final AudioService audioService;
    private final PlaylistService playlistService;

    // Import tracking
    private final Map<String, ImportSession> activeImports = new HashMap<>();
    private long nextSessionId = 1;

    @Autowired
    public ApplicationService(AudioService audioService, PlaylistService playlistService) {
        this.audioService = audioService;
        this.playlistService = playlistService;
    }

    /**
     * Import files from a folder or selection.
     * Returns an import session ID for tracking progress.
     */
    public synchronized String startFileImport(File importFolder) {
        String sessionId = "import_" + nextSessionId++;

        ImportSession session = new ImportSession(sessionId, importFolder);
        activeImports.put(sessionId, session);

        // Start the import in background
        startImportProcess(session);

        return sessionId;
    }

    /**
     * Import media files from file list.
     */
    public synchronized String startFileImport(List<File> files) {
        String sessionId = "import_" + nextSessionId++;

        ImportSession session = new ImportSession(sessionId, files);
        activeImports.put(sessionId, session);

        // Start the import in background
        startBulkImportProcess(session);

        return sessionId;
    }

    /**
     * Get import progress for a session.
     */
    public ImportProgress getImportProgress(String sessionId) {
        ImportSession session = activeImports.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Import session not found: " + sessionId);
        }

        return session.getProgress();
    }

    /**
     * Cancel an active import session.
     */
    public synchronized void cancelImport(String sessionId) {
        ImportSession session = activeImports.get(sessionId);
        if (session != null) {
            session.cancel();
        }
    }

    /**
     * Get all active import sessions.
     */
    public List<ImportProgress> getActiveImports() {
        return activeImports.values().stream()
            .map(ImportSession::getProgress)
            .toList();
    }

    /**
     * Configure automatic folder monitoring for music imports.
     */
    public void configureWatchFolder(File folder) {
        System.out.println("📁 APPLICATION SERVICE: Would configure watch folder: " + folder.getPath());
        // TODO: Implement folder monitoring using folderMonitor
        // folderMonitor.registerFolder(folder, this::onFileAdded, this::onFileRemoved);
    }

    /**
     * Cleanup completed import sessions.
     */
    public void cleanupCompletedImports() {
        activeImports.values().removeIf(session ->
            session.getProgress().getStatus() == ImportStatus.COMPLETED ||
            session.getProgress().getStatus() == ImportStatus.CANCELLED ||
            session.getProgress().getStatus() == ImportStatus.ERROR);
    }

    /**
     * Get comprehensive application statistics.
     */
    public ApplicationStats getApplicationStats() {
        AudioLibraryStats audioStats = audioService.getLibraryStats();
        PlaylistLibraryStats playlistStats = playlistService.getPlaylistStats();

        return new ApplicationStats(
            audioStats,
            playlistStats,
            getActiveImports().size(),
            System.currentTimeMillis() // Uptime would be calculated from app start
        );
    }

    /**
     * Scan and refresh the music library.
     * Useful when files have been added/removed externally.
     */
    public void refreshLibrary() {
        System.out.println("🔄 APPLICATION SERVICE: Library refresh requested");
        // TODO: Implement library refresh logic
        // - Scan configured music folders
        // - Compare with database
        // - Add new files, remove missing files
        // - Update metadata
    }

    /**
     * Perform database maintenance operations.
     */
    public void performDatabaseMaintenance() {
        System.out.println("🛠️ APPLICATION SERVICE: Database maintenance");
        // TODO: Implement database maintenance
        // - Optimize indexes
        // - Cleanup orphaned records
        // - Update statistics
        // - Vacuum database
    }

    /**
     * Get system information for diagnostics.
     */
    public SystemInfo getSystemInfo() {
        return new SystemInfo(
            System.getProperty("os.name"),
            System.getProperty("os.version"),
            System.getProperty("java.version"),
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().maxMemory() / 1024 / 1024, // MB
            Runtime.getRuntime().totalMemory() / 1024 / 1024  // MB
        );
    }

    /**
     * Export playlist data (for backup or sharing).
     */
    public byte[] exportPlaylists() {
        // TODO: Implement playlist export
        // Return JSON or XML data
        return new byte[0];
    }

    /**
     * Import playlist data.
     */
    public void importPlaylists(byte[] data) {
        // TODO: Implement playlist import
        // Parse and create playlists with relationships
    }

    /**
     * Handle file added event from folder monitoring.
     */
    private void onFileAdded(File file) {
        System.out.println("📁 APPLICATION SERVICE: File added: " + file.getName());
        // TODO: Process new file automatically
        // - Validate file type
        // - Extract metadata
        // - Add to database
        // - Update UI if needed
    }

    /**
     * Handle file removed event from folder monitoring.
     */
    private void onFileRemoved(File file) {
        System.out.println("📁 APPLICATION SERVICE: File removed: " + file.getName());
        // TODO: Handle file removal
        // - Find corresponding song record
        // - Mark as missing or remove from library
        // - Update UI if needed
    }

    /**
     * Start import process for folder.
     */
    private void startImportProcess(ImportSession session) {
        // Run import in background thread
        new Thread(() -> {
            try {
                session.setStatus(ImportStatus.IN_PROGRESS);

                // Simulate folder scanning and import
                File[] files = session.getFolder().listFiles((dir, name) ->
                    name.toLowerCase().endsWith(".mp3") ||
                    name.toLowerCase().endsWith(".flac") ||
                    name.toLowerCase().endsWith(".wav"));

                if (files != null) {
                    session.setTotalFiles(files.length);

                    for (int i = 0; i < files.length && !session.isCancelled(); i++) {
                        File file = files[i];
                        try {
                            // Simulate file processing
                            Song song = new Song();
                            song.setFileName(file.getName());
                            song.setPath(file.getParent());
                            // Extract metadata here in real implementation
                            song.setTitle("From import: " + file.getName());

                            audioService.addSong(song);
                            session.incrementProcessed();

                        } catch (Exception e) {
                            session.incrementErrors();
                        }
                    }
                }

                session.setStatus(session.isCancelled() ? ImportStatus.CANCELLED : ImportStatus.COMPLETED);

            } catch (Exception e) {
                session.setStatus(ImportStatus.ERROR);
            }
        }).start();
    }

    /**
     * Start bulk import process for file list.
     */
    private void startBulkImportProcess(ImportSession session) {
        // Similar to startImportProcess but for file list
        new Thread(() -> {
            try {
                session.setStatus(ImportStatus.IN_PROGRESS);
                session.setTotalFiles(session.getFiles().size());

                for (File file : session.getFiles()) {
                    if (session.isCancelled()) break;

                    try {
                        Song song = new Song();
                        song.setFileName(file.getName());
                        song.setPath(file.getParent());
                        song.setTitle("Bulk import: " + file.getName());

                        audioService.addSong(song);
                        session.incrementProcessed();

                    } catch (Exception e) {
                        session.incrementErrors();
                    }
                }

                session.setStatus(session.isCancelled() ? ImportStatus.CANCELLED : ImportStatus.COMPLETED);

            } catch (Exception e) {
                session.setStatus(ImportStatus.ERROR);
            }
        }).start();
    }

    // Nested classes for import management

    public enum ImportStatus {
        PENDING, IN_PROGRESS, COMPLETED, CANCELLED, ERROR
    }

    public static class ImportProgress {
        private final String sessionId;
        private final ImportStatus status;
        private final int totalFiles;
        private final int processedFiles;
        private final int errorCount;
        private final String folderPath;

        public ImportProgress(String sessionId, ImportStatus status, int totalFiles,
                            int processedFiles, int errorCount, String folderPath) {
            this.sessionId = sessionId;
            this.status = status;
            this.totalFiles = totalFiles;
            this.processedFiles = processedFiles;
            this.errorCount = errorCount;
            this.folderPath = folderPath;
        }

        // Getters
        public String getSessionId() { return sessionId; }
        public ImportStatus getStatus() { return status; }
        public int getTotalFiles() { return totalFiles; }
        public int getProcessedFiles() { return processedFiles; }
        public int getErrorCount() { return errorCount; }
        public String getFolderPath() { return folderPath; }
    }

    public static class ApplicationStats {
        private final AudioLibraryStats audioStats;
        private final PlaylistLibraryStats playlistStats;
        private final int activeImports;
        private final long uptimeMs;

        public ApplicationStats(AudioLibraryStats audioStats, PlaylistLibraryStats playlistStats,
                              int activeImports, long uptimeMs) {
            this.audioStats = audioStats;
            this.playlistStats = playlistStats;
            this.activeImports = activeImports;
            this.uptimeMs = uptimeMs;
        }

        // Getters
        public AudioLibraryStats getAudioStats() { return audioStats; }
        public PlaylistLibraryStats getPlaylistStats() { return playlistStats; }
        public int getActiveImports() { return activeImports; }
        public long getUptimeMs() { return uptimeMs; }
    }

    public static class SystemInfo {
        private final String osName;
        private final String osVersion;
        private final String javaVersion;
        private final int cpuCores;
        private final long maxMemoryMB;
        private final long totalMemoryMB;

        public SystemInfo(String osName, String osVersion, String javaVersion,
                         int cpuCores, long maxMemoryMB, long totalMemoryMB) {
            this.osName = osName;
            this.osVersion = osVersion;
            this.javaVersion = javaVersion;
            this.cpuCores = cpuCores;
            this.maxMemoryMB = maxMemoryMB;
            this.totalMemoryMB = totalMemoryMB;
        }

        // Getters
        public String getOsName() { return osName; }
        public String getOsVersion() { return osVersion; }
        public String getJavaVersion() { return javaVersion; }
        public int getCpuCores() { return cpuCores; }
        public long getMaxMemoryMB() { return maxMemoryMB; }
        public long getTotalMemoryMB() { return totalMemoryMB; }
    }

    private static class ImportSession {
        private final String sessionId;
        private final File folder;
        private final List<File> files;
        private final long startTime;
        private volatile ImportStatus status;
        private volatile int totalFiles;
        private volatile int processedFiles;
        private volatile int errorCount;
        private volatile boolean cancelled;

        public ImportSession(String sessionId, File folder) {
            this.sessionId = sessionId;
            this.folder = folder;
            this.files = new ArrayList<>();
            this.startTime = System.currentTimeMillis();
            this.status = ImportStatus.PENDING;
        }

        public ImportSession(String sessionId, List<File> files) {
            this.sessionId = sessionId;
            this.folder = null;
            this.files = new ArrayList<>(files);
            this.startTime = System.currentTimeMillis();
            this.status = ImportStatus.PENDING;
        }

        // Getters and setters
        public void setStatus(ImportStatus status) { this.status = status; }
        public void setTotalFiles(int totalFiles) { this.totalFiles = totalFiles; }
        public void incrementProcessed() { processedFiles++; }
        public void incrementErrors() { errorCount++; }
        public void cancel() { cancelled = true; }
        public boolean isCancelled() { return cancelled; }

        public File getFolder() { return folder; }
        public List<File> getFiles() { return files; }

        public ImportProgress getProgress() {
            return new ImportProgress(
                sessionId,
                status,
                totalFiles,
                processedFiles,
                errorCount,
                folder != null ? folder.getPath() : "Bulk Import"
            );
        }
    }
}
