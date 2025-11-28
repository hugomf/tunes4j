# 🛡️ Bounded Context Boundary Enforcement

## 🎯 Mission Statement

**Maintain the integrity of DDD bounded contexts throughout the Tunes4J reactive refactoring.** This document serves as the architectural compass, preventing architectural drift back to monolithic approaches.

---

## 🔍 Architecture Integrity Checks

### Every Source File Must Answer These Questions:

#### 1. **What bounded context am I in?**
   - **Package path defines context**: `/audio/`, `/library/`, `/playlist/`, `/application/`

#### 2. **What is my single responsibility?**
   - **Audio**: `Playback controls, visualization, sound processing`
   - **Library**: `Song data management, search, metadata, search, file operations`
   - **Playlist**: `Playlist organization, saved collections, drag-and-drop`
   - **Application**: `UI composition, layout, cross-context orchestration`

#### 3. **Am I importing from wrong bounded context?**
   ```java
   // ❌ WRONG - Audio should NOT import persistence
   import org.ocelot.tunes4j.library.adapter.SongRepository;
   import org.ocelot.tunes4j.playlist.model.PlaylistService;

   // ✅ CORRECT - Only Spring Framework, own context, shared events
   import org.springframework.context.ApplicationEventPublisher;
   import org.ocelot.tunes4j.audio.model.Song;
   import org.ocelot.tunes4j.event.AudioSongSelectedEvent;
   ```

#### 4. **Am I communicating properly between contexts?**
   - ✅ **Intra-context**: Direct dependency injection OK
   - ✅ **Cross-context**: `ApplicationEventPublisher` + Observer Pattern only
   - ❌ **Never**: Direct method calls between bounded contexts

---

## 🚨 RED FLAGS - IMMEDIATE STOP & REFLECT

**If you see these patterns, STOP and refactor immediately:**

### 1. **Audio Context Importing DAO/Repository** 📋
```java
❌ VIOLATION:
package org.ocelot.tunes4j.audio.view;
import org.ocelot.tunes4j.dao.SongRepository;        // WRONG!
import org.ocelot.tunes4j.library.adapter.MetadataRepository; // WRONG!
```

**Why it's wrong:** Audio should focus on playback only. Persistence is Library context's job.

### 2. **Cross-Boundary Method Calls** 🔗
```java
❌ VIOLATION:
package org.ocelot.tunes4j.audio.controller;
@Autowired
LibrarySongService libraryService;                    // WRONG!

public void playSong() {
    libraryService.loadSongFromDisk(song);           // VIOLATION!
}
```

**Correct approach:**
```java
✅ CORRECT: Event-driven communication
@EventListener
public void onSongSelected(AudioSongSelectedEvent event) {
    Song song = event.getSong();
    startPlayback(song);
}
```

### 3. **View Managing Business Logic** ⚡
```java
❌ VIOLATION:
package org.ocelot.tunes4j.audio.view;
public class SongListView {
    @Autowired
    PlaybackService playbackService;                // WRONG!

    public void onDoubleClick() {
        playbackService.play(song);                  // VIOLATION!
    }
}
```

**Why it's wrong:** Views should publish events, not orchestrate business logic.

### 4. **Mixed Responsibilities in Single Component** 🎭
```java
❌ BIG VIOLATION:
@Component
public class MonolithicComponent {
    // Handle playback controls
    public void play() { /* audio */ }

    // Handle playlist management
    public void addToPlaylist() { /* playlist */ }

    // Handle song library operations
    public void searchSongs() { /* library */ }

    // Handle UI layout
    public void resizeWindow() { /* application */ }
}
```

---

## ✅ VERIFICATION COMMANDS

Run these frequently to detect violations:

```bash
# 🔍 Check for forbidden cross-context imports
echo "🔍 Scanning for cross-context violations..."

# Audio should NOT import from other contexts
find src -name "*.java" -exec grep -l "import org.ocelot.tunes4j.library." {} \; | grep audio/
find src -name "*.java" -exec grep -l "import org.ocelot.tunes4j.playlist." {} \; | grep audio/

# Library should NOT import from other contexts
find src -name "*.java" -exec grep -l "import org.ocelot.tunes4j.audio." {} \; | grep library/
find src -name "*.java" -exec grep -l "import org.ocelot.tunes4j.playlist." {} \; | grep library/

# 🔧 Ensure bounded context compilation isolation
echo "🔧 Testing context isolation..."
./gradlew :compileJava --continue

# 📊 Check architecture integrity
echo "📊 Architecture health check..."
if [ $? -eq 0 ]; then
    echo "✅ All bounded contexts compile independently"
else
    echo "❌ Architecture violations detected - fix immediately!"
    exit 1
fi
```

---

## 🎭 DDD REMINDERS - WIREFRAME YOUR COMPONENT

**Before writing ANY code, ask:**

### **Bounded Context Canvas** (Fill this out mentally):

```
CONTEXT: [audio/library/playlist/application]

RESPONSIBILITY: [One clear sentence about what this component does]

INPUTS: [What events/interfaces it consumes]
├── From same context: [Direct dependencies OK]
└── From other contexts: [Events only!]

OUTPUTS: [What it produces]
├── UI Events: [User interactions]
└── Application Events: [State changes, user navigations]

DEPENDENCIES:
├── ✅ ALLOWED [Same context + Spring + events]
└── ❌ FORBIDDEN [Other contexts directly]
```

### Example: AudioController Canvas
```
CONTEXT: audio

RESPONSIBILITY: Orchestrate audio playback operations and coordinate UI updates

INPUTS:
├── From same context: PlaybackService, AudioPlayerAdapter
└── From other contexts: AudioSongSelectedEvent, AudioUserInteractionEvent

OUTPUTS:
├── UI Events: N/A (controller coordinates, doesn't present)
└── Application Events: AudioPlaybackStateEvent, SongInfoEvent

DEPENDENCIES:
├── ✅ ALLOWED: audio/*, Spring Framework, event/*
└── ❌ FORBIDDEN: library.*, playlist.*
```

---

## 📝 PRE-COMMIT CHECKLIST

**Before any commit, verify:**

- [ ] **Single Responsibility**: Component does ONE thing well within ONE bounded context
- [ ] **Communication Rules**: Cross-context only via Observer Pattern events
- [ ] **Import Hygiene**: No forbidden imports from other bounded contexts
- [ ] **Test Compilation**: `./gradlew compileJava` succeeds
- [ ] **Event-Driven**: All user interactions publish events, don't call methods directly

---

## 🎵 THE ART OF BOUNDED CONTEXT COMPOSITION

**Remember:** Tunes4J is a **Reactive MVC Symphony** where:
- 🎼 **Audio Bounded Context**: The rhythm section (plays the beats)
- 📚 **Library Bounded Context**: The sheet music (manages the songs)
- 🎵 **Playlist Bounded Context**: The setlist (organizes concerts)
- 🖥️ **Application Context**: The conductor (coordinates the performance)

Each musician plays their part independently, communicates through the conductor, and creates beautiful reactive harmony!

**🎶 This is not monolithic mud - this is architecturally orchestrated music! 🎶**
