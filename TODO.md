# 🎵 Tunes4J Reactive MVC Architecture Refactoring Plan

## 📋 Executive Summary

This plan outlines the transformation of Tunes4J from a traditional MVC architecture to a **reactive MVC architecture** based on **DDD (Domain-Driven Design)** principles using **bounded contexts** and the **Observer Pattern** for communication.

**Current State**: Monolithic structure with GUI components tightly coupled, mixed concerns across packages
**Target State**: Independent, event-driven MVC components communicating via Observer Pattern within DDD bounded contexts

**🔥 CRITICAL IMPLEMENTATION REQUIREMENT**: **GUI component migration is MANDATORY for each MVC Component (Bounded Context)**. Without **copying AND enhancing** the corresponding UI components (`gui/` → bounded context `/view/`), the architectural refactoring **CANNOT** achieve proper separation and reactive event-driven architecture.

**Migration Process**: **COPY FIRST, THEN ENHANCE**
1. **Copy** GUI components from `/gui/` directory to respective bounded context `/view/` directories
2. **Enhance** them to implement event-driven architecture:
   - Add `@EventListener` annotations for reactive event handling
   - Inject `ApplicationEventPublisher` for Observer Pattern communication
   - Replace direct component coupling with event-driven updates
   - Transform direct method calls into Observer Pattern event publishing
   - Make components truly reactive and decoupled

## ⚠️ Important Implementation Notes

- **DO NOT modify existing packages directly** (`gui/`, `dto/`, `dao/`, `event/`, `player/`, `dsp/`, `service/`) - instead, **copy** components to their respective bounded context architecture layers
- **Copy strategy**: Create new files in bounded contexts first by copying from existing packages, then refactor them in-place without modifying originals
- **DO NOT update the main application to run until ALL bounded contexts are wired up** - work incrementally but keep the app non-functional during transition
- **Preserve functionality**: Copy entire components/classes first, then refactor their internals to DDD/reactive patterns
- **Leave originals untouched**: Original packages serve as reference sources during the entire migration process

---

## 🏗️ Phase 1: Foundation Setup (Bounded Context Structure)

### 1.1 Create Bounded Context Directory Structure
- [ ] Create `audio/` bounded context packages:
  - `src/main/java/org/ocelot/tunes4j/audio/model/`
  - `src/main/java/org/ocelot/tunes4j/audio/controller/`
  - `src/main/java/org/ocelot/tunes4j/audio/service/`
  - `src/main/java/org/ocelot/tunes4j/audio/adapter/`
  - `src/main/java/org/ocelot/tunes4j/audio/view/`
- [ ] Create `playlist/` bounded context packages:
  - `src/main/java/org/ocelot/tunes4j/playlist/model/`
  - `src/main/java/org/ocelot/tunes4j/playlist/controller/`
  - `src/main/java/org/ocelot/tunes4j/playlist/service/`
  - `src/main/java/org/ocelot/tunes4j/playlist/adapter/`
  - `src/main/java/org/ocelot/tunes4j/playlist/view/`
- [ ] Create `library/` bounded context packages:
  - `src/main/java/org/ocelot/tunes4j/library/model/`
  - `src/main/java/org/ocelot/tunes4j/library/adapter/`
- [ ] Create `application/` bounded context packages:
  - `src/main/java/org/ocelot/tunes4j/application/view/`
  - `src/main/java/org/ocelot/tunes4j/application/controller/`

### 1.2 Create Spring Boot Application Events Infrastructure
- [ ] Create `src/main/java/org/ocelot/tunes4j/event/` package for domain events
- [ ] Create base `AudioDomainEvent` class extending `ApplicationEvent`
- [ ] Create domain event classes:
  - `AudioSongSelectedEvent`
  - `AudioUserInteractionEvent`
  - `AudioPlaybackStateEvent`
  - `SongInfoEvent`
  - `AudioDataEvent`
- [ ] Create base `BaseController` class with `ApplicationEventPublisher` injection

### 1.3 Correct DDD Layering (CRITICAL FIX)
- [x] **FIXED**: Removed JPA DTO (`@Entity @Data`) from domain model layer
- [x] **CORRECTED**: Domain model layer contains pure business logic objects (NO JPA!)
- [x] **MAINTAINED**: DTOs (`@Entity @Data`) stay in infrastructure layer (`dto/`)
- [ ] Document proper DDD layering: Domain ≠ Infrastructure persistence objects

---

## 🏗️ Phase 2: Audio Bounded Context Refactoring

### 2.1 Model Layer - Domain Objects (`audio/model/`)
- [ ] Move and refactor `Song` from `dto/Song.java` to `audio/model/Song.java` (DDD Entity)
- [ ] Create `Album` entity in `audio/model/Album.java`
- [ ] Create `AudioPlayback` aggregate in `audio/model/AudioPlayback.java`
- [ ] Move spectrum domain objects to `audio/model/`
- [ ] Define domain business rules and validation

### 2.2 Service Layer - Business Logic (`audio/service/`)
- [ ] Create `PlaybackService` for audio playback orchestration
- [ ] Create `SpectrumService` for spectrum processing and visualization
- [ ] Move FFT and DSP logic from `dsp/` to `SpectrumService`
- [ ] Migrate `Tunes4JAudioPlayer` logic to `PlaybackService`

### 2.3 Adapter Layer - External Interfaces (`audio/adapter/`)
- [ ] Create `SongRepositoryImpl` implementing domain repository interface
- [ ] Move and refactor `SongRepository` from `dao/` to `audio/adapter/`
- [ ] Create `AudioPlayerAdapter` wrapping audio playback libraries
- [ ] Create `SpectrumAdapter` for FFT processing
- [ ] Move `KJFFT` and DSP components to adapters

### 2.4 Controller Layer - Reactive Coordinators (`audio/controller/`)
- [ ] Create `AudioController` extending `BaseController`
- [ ] Implement `@EventListener` methods for reactive event handling
- [ ] Add event publishing logic using Observer Pattern
- [ ] Create reactive methods for UI coordination

### 2.5 View Layer - GUI Components Migration (`audio/view/`)
**CRITICAL**: Migrate all audio GUI components from `gui/` to `audio/view/` - this is a key part of achieving MVC separation per bounded context

**Migration Sources**:
- `gui/PlayerPanel.java` → `AudioPlayerView.java`
- `gui/SongDisplayPanel.java` → `SongDisplayView.java`
- `gui/MediaTable.java` → `SongListView.java` (reactive song table)
- `gui/JPanelSpectrum.java` → `SpectrumView.java`
- `gui/JPanelSoundWave.java` → `SpectrumView.java` (combined spectrum view)
- `gui/SpectrumProcessor.java` → `AudioSpectrumView.java`

**Migration Process**: **COPY FIRST → THEN ENHANCE**

For each component:
1. **Copy** original GUI component from `/gui/` directory to `/audio/view/` directory
2. **Add** `@EventListener` for reactive capabilities
3. **Inject** `ApplicationEventPublisher` for Observer Pattern communication
4. **Replace** direct component calls with event publishing/listening
5. **Remove** tight GUI coupling with other components

**Migration Tasks**:
- [ ] **AudioPlayerView Migration**: **Copy** `gui/PlayerPanel.java` to `AudioPlayerView.java`, then **enhance** with playback controls (play/pause/stop/volume) as reactive event publisher using Observer Pattern
- [ ] **SongDisplayView Migration**: **Copy** `gui/SongDisplayPanel.java` to `SongDisplayView.java`, then **enhance** current song metadata display to reactive event listener (@EventListener)
- [ ] **SongListView Migration**: **Copy** `gui/MediaTable.java` to `SongListView.java`, then **enhance** song selection table to reactive component with double-click event publishing via ApplicationEventPublisher
- [ ] **SpectrumView Migration**: **Copy** spectrum visualization (`gui/JPanelSpectrum.java`, `gui/JPanelSoundWave.java`) to `SpectrumView.java`, then **enhance** with reactive event listener capabilities
- [ ] **AudioSpectrumView Migration**: **Copy** `gui/SpectrumProcessor.java` to `AudioSpectrumView.java`, then **enhance** spectrum processing coordinator with lifecycle management using Observer Pattern
- [ ] **@EventListener Integration**: Implement reactive event handling in all migrated view components using Spring event system
- [ ] **Observer Pattern Publishing**: Ensure view components publish appropriate domain events (Observer Pattern) instead of direct method calls
- [ ] **Decoupling Enhancement**: Eliminate direct references to other GUI components, replacing them with event-driven communication

---

## 🏗️ Phase 3: Playlist Bounded Context Refactoring

### 3.1 Model Layer - Domain Objects (`playlist/model/`)
- [ ] Move and refactor `PlayList` from `dto/PlayList.java` to `playlist/model/PlayList.java`
- [ ] Create `PlaylistSong` entity for many-to-many relationship
- [ ] Create `PlaylistCategory` entity for folder organization
- [ ] Define domain business rules for playlists

### 3.2 Service Layer - Business Logic (`playlist/service/`)
- [ ] Create `PlaylistService` for playlist management
- [ ] Create `PlaylistManagement` for complex playlist operations
- [ ] Implement playlist validation and business rules

### 3.3 Adapter Layer - External Interfaces (`playlist/adapter/`)
- [ ] Create `PlaylistRepositoryImpl` in `playlist/adapter/`
- [ ] Move `PlayListRepository` from `dao/` to `playlist/adapter/`
- [ ] Create `PlaylistDragDropAdapter` for drag-and-drop functionality
- [ ] Create `PlaylistViewAdapter` for UI integration

### 3.4 Controller Layer - Reactive Coordinators (`playlist/controller/`)
- [ ] Create `PlaylistController` extending `BaseController`
- [ ] Implement `@EventListener` methods for playlist events
- [ ] Add reactive playlist management coordination

### 3.5 View Layer - GUI Components Migration (`playlist/view/`)
**CRITICAL**: Migrate all playlist GUI components from `gui/` to `playlist/view/` - essential for playlist bounded context independence

**Migration Sources**:
- `gui/sourcelist/SourceList.java` → `PlaylistSourceListView.java` (hierarchical playlist tree)
- `gui/sourcelist/SourceListModel.java` → playlist data model backing
- `gui/LeftPanel.java` → `PlaylistView.java` (main playlist navigation panel)
- `gui/MediaTable.java` → playlist contents table (shared component adaptation)

**Migration Process**: **COPY FIRST → THEN ENHANCE**

For each component:
1. **Copy** original GUI component from `/gui/` directory to `/playlist/view/` directory
2. **Add** `@EventListener` for reactive capabilities
3. **Inject** `ApplicationEventPublisher` for Observer Pattern communication
4. **Replace** direct playlist UI interactions with event-driven updates
5. **Remove** tight coupling with audio bounded context GUI components

**Migration Tasks**:
- [ ] **PlaylistView Migration**: **Copy** `gui/LeftPanel.java` to `PlaylistView.java`, then **enhance** main playlist navigation panel with reactive event handling using Observer Pattern
- [ ] **PlaylistSourceListView Migration**: **Copy** `gui/sourcelist/SourceList.java` and related components to `PlaylistSourceListView.java`, then **enhance** hierarchical playlist/category tree display with event-driven reactive capabilities
- [ ] **PlaylistEditorView Migration**: **Create or copy** existing playlist editing components, then **enhance** with metadata management and event publishing capabilities
- [ ] **PlaylistContentView Migration**: **Copy and adapt** `gui/MediaTable.java` for playlist-specific song listing, then **enhance** with reactive updates using Observer Pattern
- [ ] **PlaylistTreeView Migration**: **Implement from** source list components, then **enhance** tree-based navigation with drag-and-drop support via event system
- [ ] **@EventListener Integration**: Implement reactive playlist event handling using Spring event system (selection, creation, deletion events)
- [ ] **Observer Pattern Publishing**: Enable playlist operations to publish appropriate domain events instead of direct component coupling
- [ ] **Decoupling Enhancement**: Break direct dependencies on audio GUI components, replacing them with cross-context event communication

---

## 🏗️ Phase 4: Library Bounded Context Refactoring

### 4.1 Model Layer - Domain Objects (`library/model/`)
- [ ] Create `Library` aggregate for song collection management
- [ ] Create `Metadata` aggregate for ID3 tag handling
- [ ] Define search and filter business rules

### 4.2 Adapter Layer - External Interfaces (`library/adapter/`)
- [ ] Create `LibraryRepositoryImpl` for song database persistence
- [ ] Create `MetadataExtractor` adapter for external libraries
- [ ] Create `FileWatcher` adapter for filesystem monitoring
- [ ] Migrate file watching logic from `service/` to adapters

---

## 🏗️ Phase 5: Application Bounded Context Refactoring

### 5.1 View Layer - GUI Components Migration (`application/view/`)
**CRITICAL**: Migrate main window and navigation GUI components from `gui/` to `application/view/` - essential for unified application composition

**Migration Sources**:
- `gui/ApplicationWindow.java` → `ApplicationMainWindow.java` (main application frame)
- `gui/ApplicationMenuBar.java` → `ApplicationMenuView.java` (menu bar with actions)
- `gui/LeftSplitPane.java` → `ApplicationSplitPane.java` (panel divider)
- `gui/SplitPane.java` → additional split pane components

**Migration Process**: **COPY FIRST → THEN ENHANCE**

For each component:
1. **Copy** original GUI component from `/gui/` directory to `/application/view/` directory
2. **Add** `@EventListener` for reactive capabilities and cross-context event handling
3. **Inject** `ApplicationEventPublisher` for global event orchestration
4. **Replace** direct bounded context coupling with event routing
5. **Remove** direct business logic from views, delegate to controllers

**Migration Tasks**:
- [ ] **ApplicationMainWindow Migration**: **Copy** `gui/ApplicationWindow.java` to `ApplicationMainWindow.java`, then **enhance** to become composition root for bounded context views with comprehensive @EventListener capabilities
- [ ] **ApplicationMenuView Migration**: **Copy** `gui/ApplicationMenuBar.java` to `ApplicationMenuView.java`, then **enhance** menu bar with global actions and event publishing using ApplicationEventPublisher
- [ ] **ApplicationLayoutView Migration**: **Copy** split pane components (`gui/LeftSplitPane.java`, `gui/SplitPane.java`) to application layout views, then **enhance** for main layout management
- [ ] **ApplicationStatusBar Migration**: **Create or copy** existing status components, then **enhance** for global notifications with reactive updates via Observer Pattern
- [ ] **@EventListener Integration**: Implement comprehensive reactive event handling for global events (theme changes, application state, cross-context communication)
- [ ] **Observer Pattern Publishing**: Enable menu actions and other application components to publish application-level events through the Observer Pattern
- [ ] **Decoupling Enhancement**: Strip direct business logic from view layer, replacing with event-driven delegation to ApplicationController

### 5.2 Controller Layer - Cross-Domain Coordination (`application/controller/`)
**CRITICAL**: Create application controller for bounded context orchestration and ACL patterns

- [ ] **ApplicationController Creation**: Build `ApplicationController` extending `BaseController` for global state management
- [ ] **Context Mapping Implementation**: Define relationships and communication patterns between bounded contexts
- [ ] **Anti-Corruption Layer Implementation**: Create translation interfaces for cross-context domain interactions
- [ ] **Reactive Coordination**: Implement event routing and cross-context event handling
- [ ] **Global State Management**: Handle application-wide state (themes, preferences, global shortcuts)

---

## 🏗️ Phase 6: Event System Integration & Observer Pattern

### 6.1 Event System Implementation
- [ ] Implement `ApplicationEventPublisher` injection in all controllers
- [ ] Convert existing event system to Spring events
- [ ] Create event listener registration patterns

### 6.2 Reactive Flow Implementation
- [ ] Implement double-click → `AudioSongSelectedEvent` flow
- [ ] Implement single-click → preview flow
- [ ] Create playback state event broadcasting
- [ ] Implement spectrum data event flow

### 6.3 Observer Relationship Mapping
- [ ] Establish `SongListView` → `AudioController` relationship
- [ ] Establish `AudioController` → `SongDisplayView` relationship
- [ ] Establish `AudioController` → `VisualizationView` relationship
- [ ] Test reactive cross-component communication

---

## 🏗️ Phase 7: Configuration & Spring Integration

### 7.1 Spring Configuration Updates
- [ ] Convert `AppConfiguration.java` to Spring Boot configuration
- [ ] Add `@EnableJpaRepositories` for each bounded context
- [ ] Add component scanning for new packages
- [ ] Configure event publishing

### 7.2 Dependency Injection Cleanup
- [ ] Remove tight coupling between GUI components
- [ ] Implement proper dependency injection
- [ ] Add interface segregation between bounded contexts

---

## 🏗️ Phase 8: Testing & Validation

### 8.1 Unit Testing Setup
- [ ] Create unit tests for domain objects in each bounded context
- [ ] Add service layer tests
- [ ] Create event system tests

### 8.2 Integration Testing
- [ ] Test reactive event flows between components
- [ ] Validate Observer Pattern implementation
- [ ] Test bounded context separation

### 8.3 End-to-End Validation
- [ ] Test complete audio playback flow
- [ ] Validate playlist functionality
- [ ] Test theme system integration

---

## 🏗️ Phase 9: Legacy Cleanup & Migration

### 9.1 Deprecated Package Removal
- [ ] Remove legacy `gui/` components after migration
- [ ] Remove `dto/` package after domain objects are moved
- [ ] Remove `dao/` package after repositories are migrated
- [ ] Clean up unused utility classes

### 9.2 Configuration Updates
- [ ] Update build.gradle dependencies
- [ ] Clean up resource files and configurations
- [ ] Update documentation and README

---

## 📊 Success Criteria

- [ ] ✅ **Zero Coupling**: Components communicate only through events
- [ ] ✅ **Reactive UI**: All UI updates triggered by events
- [ ] ✅ **DDD Compliance**: Each bounded context has model/controller/service/adapter/view
- [ ] ✅ **Observer Pattern**: Proper subject-observer relationships established
- [ ] ✅ **Event-Driven**: All component interactions through Spring events
- [ ] ✅ **Testable**: Independent bounded contexts can be tested in isolation
- [ ] ✅ **Maintainable**: Clear separation of concerns and single responsibility principle

## 🔄 Implementation Order Strategy

**Start with Audio Bounded Context** (Phase 2) as it represents the core domain and will establish patterns for other contexts.

**Parallel Implementation**: Audio and Playlist contexts can be developed in parallel after foundation setup.

**Integration Last**: Application context integration happens after individual bounded contexts are complete.

## 🚧 Risk Mitigation

- **Incremental Migration**: Move components gradually, testing at each step
- **Single Responsibility**: Each commit addresses one bounded context layer
- **Event System First**: Implement events before moving GUI components
- **Test-Driven**: Add tests before refactoring critical components
- **Boundary Enforcement**: Check BOUNDARY_GUARDRAILS.md and run verification commands before committing

---

## 🛡️ Architecture Integrity Safeguards

### **Phase Integrity Checks - RUN BEFORE EACH PHASE**

**Use this checklist for every component migration:**

#### 🔍 **Bounded Context Integration Check**
- [ ] **Package Location**: Component is in correct bounded context (/audio/, /library/, /playlist/, /application/)
- [ ] **Single Responsibility**: Does ONE thing within ONE bounded context
- [ ] **Import Hygiene**: NO forbidden imports from other bounded contexts
- [ ] **Communication Rules**: Only Observer Pattern events across contexts

#### 📋 **GUI Migration Verification**
- [ ] **COPY FIRST**: Original component copied to bounded context /view/
- [ ] **ENHANCE SECOND**: @EventListener and ApplicationEventPublisher added
- [ ] **DIRECT COUPLING REMOVED**: No direct component method calls replaced with events
- [ ] **Reactive Communication**: User interactions publish events, not orchestrate business logic

#### 🧪 **Architecture Enforcement Commands**
```bash
# Run these before every commit:
echo "🔍 Architecture integrity check..."
find src -name "*.java" -exec grep -l "import org.ocelot.tunes4j.library." {} \; | grep -v library/
find src -name "*.java" -exec grep -l "import org.ocelot.tunes4j.audio." {} \; | grep -v audio/

echo "🔧 Compilation check..."
./gradlew :compileJava --continue

echo "📊 Boundary guardrails..."
cat src/main/resources/BOUNDARY_GUARDRAILS.md | head -20
```

### 🚨 **IMMEDIATE STOP CONDITIONS**

**STOP immediately and refactor if any of these occur:**
- Cross-bounded-context imports detected
- Compilation failures with new boundary violations
- Views orchestrating business logic (not publishing events)
- Controllers managing UI directly (not coordinating through events)
- Mixed responsibilities within single components

**🎭 Remember:** Tunes4J is a **Reactive MVC Symphony**, not monolithic mud! 🎶
