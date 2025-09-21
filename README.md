# Tunes4J - Java Audio Player and Tag Editor

![Java v17](https://img.shields.io/badge/java-v17-red)
![Gradle](https://img.shields.io/badge/build-gradle-green)
![License](https://img.shields.io/badge/license-MIT-blue)

Tunes4J is a comprehensive Java-based audio player and tag editor application built with Swing. It supports multiple audio formats, radio streaming, and provides a rich set of features for music library management.

## Features

### Audio Playback
- **Multi-format Support**: MP3, OGG, AAC audio formats
- **Streaming Support**: Internet radio streaming with ICY protocol
- **Audio Processing**: Digital signal processing and FFT transformations
- **Custom Players**: Dedicated players for different audio formats

### User Interface
- **Swing-based GUI**: Custom-styled components with modern look and feel
- **Custom Components**: Rounded panels, sliding labels, image buttons, volume sliders
- **Source List Navigation**: iTunes-style sidebar navigation
- **Media Library**: Table-based media management with custom renderers
- **Visualizations**: Sound wave display and audio visualizations

### Music Management
- **Tag Editing**: Support for multiple ID3 tag libraries (JAudioTagger, JID3Lib, Mp3agic)
- **Database Integration**: HSQLDB database for library persistence
- **File Monitoring**: Automatic folder monitoring and file change detection
- **Playlist Support**: Create and manage playlists with drag-and-drop
- **Radio Stations**: Save and manage internet radio stations

### Advanced Features
- **Spring Framework**: Dependency injection and configuration management
- **Audio Effects**: Fade transitions, interpolators, and visual effects
- **Custom Notifications**: System notification support
- **Single Instance**: Prevents multiple application instances
- **Cross-platform**: Works on Windows, macOS, and Linux

## Requirements

- **Java 17** or higher
- **Gradle 8.5** or higher (for building from source)

## Installation

### Building from Source

1. Clone the repository:
```bash
git clone https://github.com/hugomf/tunes4j.git
cd tunes4j
```

2. Build the project using Gradle:
```bash
./gradlew build
```

3. Create a fat JAR with all dependencies:
```bash
./gradlew fatJar
```

4. Run the application:
```bash
java -jar build/libs/tunes4j-all.jar
```

### Pre-built Releases

Download the latest release from the [Releases page](https://github.com/hugomf/tunes4j/releases) and run:
```bash
java -jar tunes4j-all.jar
```

## Usage

### Basic Audio Playback
1. Launch Tunes4J
2. Use the "Add Files" button to import audio files
3. Select files from the library and click play
4. Use the player controls (play, pause, stop, next, previous)

### Radio Streaming
1. Go to the Radio section in the sidebar
2. Click "Add Station" and enter the stream URL
3. Select a station and click play

### Tag Editing
1. Right-click on any audio file in the library
2. Select "Edit Tags" from the context menu
3. Modify the metadata fields and save changes

### Playlist Management
1. Right-click in the Playlists section
2. Select "New Playlist"
3. Drag and drop songs from your library to the playlist

## Project Structure

```
src/main/java/org/ocelot/tunes4j/
├── config/           # Spring configuration classes
├── dao/             # Data access objects and repositories
├── dto/             # Data transfer objects
├── dsp/             # Digital signal processing
├── effects/         # UI effects and animations
├── event/           # Custom event classes
├── gui/             # Graphical user interface components
├── notification/    # Notification system
├── player/          # Audio player implementations
├── processing/      # Audio processing utilities
├── service/         # Background services
├── taggers/         # Audio tag editing libraries
└── utils/           # Utility classes
```

## Technical Details

### Dependencies
- **Audio Libraries**: JLayer, MP3SPI, VorbisSPI, JAAD
- **Tag Libraries**: JAudioTagger, JID3Lib, Mp3agic
- **UI Libraries**: Glazed Lists, LiquidLNF, VAqua
- **Spring**: Spring Data JPA, Spring Context
- **Database**: HSQLDB with Hibernate
- **Utilities**: Guava, Commons Lang, Lombok

### Architecture
- **MVC Pattern**: Model-View-Controller architecture
- **Spring DI**: Dependency injection for loose coupling
- **Event-driven**: Custom event system for component communication
- **Modular Design**: Separate components for different functionalities

## Java 17 Compatibility

This version of Tunes4J has been upgraded to support Java 17 with the following enhancements:

### Key Changes for Java 17:
- **Explicit Audio Service Registration**: Added explicit registration of MP3 and audio service providers to work with Java 17's module system
- **JVM Arguments**: Configured necessary `--add-opens` arguments for module access
- **Dependency Updates**: Updated all dependencies to versions compatible with Java 17
- **ARM64 Support**: Replaced native libraries with Java NIO alternatives for better ARM64 compatibility

### Building with Java 17:
The application includes proper Gradle configuration for Java 17 compatibility. When building or running, ensure you have Java 17 installed.

### Audio Format Support:
All audio formats (MP3, OGG, AAC) continue to work seamlessly with Java 17 through explicit service provider registration.

## Contributing

We welcome contributions! Please feel free to submit pull requests, open issues, or suggest new features.

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Development

### Setting up Development Environment
1. Install Java 17 JDK
2. Install Gradle 8.5 or higher
3. Clone the repository
4. Import into your favorite IDE (IntelliJ IDEA recommended)

### Building and Testing
```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Create distribution
./gradlew fatJar
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Built with Java Swing and Spring Framework
- Uses various open-source audio libraries
- Inspired by modern media players and iTunes
- Special thanks to all contributors and the Java community

## Support

If you encounter any issues or have questions:
1. Check the [Issues](https://github.com/hugomf/tunes4j/issues) page
2. Create a new issue with detailed information
3. Include your Java version and operating system


**Tunes4J** - Your Java-powered audio companion! 🎵
