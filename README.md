# GenDungeonAsync

A Paper plugin for Minecraft 1.21.3+ that generates random dungeons asynchronously for players to explore and adventure through. **Compatible with Minecraft 1.21.4**.

## Features

- **Async Dungeon Generation**: Dungeons are generated asynchronously to prevent server lag
- **Multiple Sizes**: Choose from Small, Medium, Large, or Huge dungeons
- **Difficulty Levels**: Easy, Normal, Hard, and Expert difficulty settings
- **Random Names**: Each dungeon gets a procedurally generated name
- **Mob Spawning**: Dungeons spawn mobs based on difficulty level
- **Treasure Chests**: Find loot scattered throughout the dungeon
- **Lighting**: Automatic torch placement for visibility
- **Player Management**: Track which players are in which dungeons

## Commands

- `/dungeon [size] [difficulty]` - Generate a new dungeon
  - Aliases: `/dg`, `/gen`
  - Permission: `gendungeon.create`
  - Examples:
    - `/dungeon` - Creates a medium difficulty normal dungeon
    - `/dungeon large` - Creates a large normal difficulty dungeon
    - `/dungeon small hard` - Creates a small hard difficulty dungeon
    - `/dungeon huge expert` - Creates a huge expert difficulty dungeon
  
- `/dungeontp <player> [dungeon_id]` - Teleport to a dungeon
  - Aliases: `/dtp`
  - Permission: `gendungeon.teleport`
  - Examples:
    - `/dungeontp PlayerName` - Teleport to PlayerName's active dungeon
    - `/dungeontp PlayerName <uuid>` - Teleport to specific dungeon by ID

## Usage Examples

### Basic Usage
```
1. Player runs: /dungeon medium normal
2. Plugin generates dungeon asynchronously (no server lag)
3. Player is teleported to dungeon entrance
4. Player explores, fights mobs, and collects loot
5. When player leaves/dies, they're removed from the dungeon
```

### Admin Usage
```
1. Admin runs: /dungeontp Steve
2. Admin is teleported to Steve's active dungeon
3. Admin can help or observe the player
```

## Permissions

- `gendungeon.*` - All permissions
- `gendungeon.create` - Create dungeons (default: true)
- `gendungeon.teleport` - Teleport to dungeons (default: op)
- `gendungeon.admin` - Admin permissions (default: op)

## Installation

1. Download the plugin JAR file
2. Place it in your server's `plugins` folder
3. Restart your server
4. Configure the plugin in `plugins/GenDungeonAsync/config.yml`

## Building and Deployment

### Build Commands
```bash
gradlew build          # Build the plugin
gradlew clean build    # Clean and build
gradlew jar            # Build only the JAR
```

### Deployment Commands
```bash
gradlew copyToServer   # Copy built JAR to Paper server
gradlew deployToServer # Build and deploy in one command
```

### Manual Installation
```bash
gradlew build
# Copy build/libs/GenDungeonAsync-1.0.0-dev.jar to your server's plugins folder
```

## Configuration

The plugin can be configured via `config.yml`:

- Adjust generation settings
- Configure loot tables
- Set world restrictions
- Customize messages

## Requirements

- Paper 1.21.3+ or compatible (including 1.21.4)
- Java 21
- Minecraft server with sufficient RAM for async operations

## License

This project is licensed under the MIT License.
