# Changelog

All notable changes to the Tunes4J project will be documented in this file.

## [Unreleased]

### Added
- Java 17 compatibility and support
- Explicit audio service provider registration for Java 17 module system
- Dependency conflict resolution for test compilation
- Comprehensive test coverage for audio service providers

### Changed
- **Java Version**: Upgraded from Java 11 to Java 17 minimum requirement
- **Gradle Wrapper**: Updated from Gradle 5.1.1 to Gradle 8.5
- **Dependencies**: Updated multiple dependencies for Java 17 compatibility:
  - Spring Data JPA: Updated to 2.7.0
  - Hibernate: Updated to 5.6.15.Final
  - Spring Framework: Updated to 5.3.23
  - Lombok: Updated to 1.18.30
  - Various test dependencies updated

### Fixed
- Resolved "UnsupportedAudioFileException" for MP3 files in Java 17
- Fixed dependency conflicts in test compilation
- Addressed Java module system access issues with JVM arguments
- Replaced ARM64-incompatible native libraries with Java NIO alternatives

### Technical
- Added `--add-opens` JVM arguments for Java 17 module system compatibility
- Implemented explicit audio service provider registration in `Tunes4JApp.java`
- Created `JavaNioFolderMonitorService.java` to replace native watch service
- Fixed macOS dock icon handling for Java 17

## [Previous Versions]

For earlier changes, please refer to the git history or previous release notes.

---

*Note: This project follows [Semantic Versioning](https://semver.org/).*