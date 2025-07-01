# Development Setup

## Quick Start

1. **Clone and build:**
   ```bash
   cd d:\dev\GenDungeonAsync
   gradlew build
   ```

2. **Find the built plugin:**
   ```
   build/libs/GenDungeonAsync-1.0.0-dev.jar
   ```

3. **Install on server:**
   - Copy the JAR to your Paper server's `plugins/` folder
   - Restart the server
   - Plugin will create default config in `plugins/GenDungeonAsync/`

## Development Commands

### Building
```bash
gradlew build          # Build the plugin
gradlew clean build    # Clean and build
gradlew jar            # Build only the JAR
```

### Testing with Paper
```bash
gradlew runServer      # Start a test Paper server (if configured)
```

### IDE Setup
The project is configured for:
- IntelliJ IDEA
- Eclipse
- VS Code with Java extensions

## Project Structure
```
src/main/java/com/example/gendungeonasync/
├── GenDungeonAsync.java           # Main plugin class
├── commands/
│   ├── DungeonCommand.java        # /dungeon command
│   └── DungeonTeleportCommand.java # /dungeontp command
├── generator/
│   └── DungeonGenerator.java      # Async dungeon generation
├── listener/
│   └── DungeonEventListener.java  # Event handling
├── manager/
│   └── DungeonManager.java        # Dungeon management
└── model/
    └── Dungeon.java               # Dungeon data model
```

## Configuration
Edit `src/main/resources/config.yml` to customize:
- Generation settings
- Loot tables
- World restrictions
- Messages

## Testing
1. Build the plugin
2. Copy to Paper 1.21.1 server
3. Start server with Java 21
4. Test commands:
   - `/dungeon small easy`
   - `/dungeontp <player>`
