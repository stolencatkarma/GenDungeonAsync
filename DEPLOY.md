# Quick Deployment Guide

## Development Workflow

### 1. Build and Deploy (Recommended)
```bash
gradlew deployToServer
```
This command will:
- Clean and build the plugin
- Remove old GenDungeonAsync JAR files from plugins folder
- Copy the new JAR to `D:\paper-1.21.7\plugins`
- Display success message

### 2. Quick Copy (if already built)
```bash
gradlew copyToServer
```
- Removes old plugin JARs
- Copies the existing JAR to the server

### 3. Manual Method
```bash
gradlew build
# Then manually copy: build/libs/GenDungeonAsync-1.0.0-dev.jar
#                to: D:\paper-1.21.7\plugins\
```

## After Deployment
1. **Restart your Paper server** or use `/reload confirm` (not recommended for production)
2. **Check server logs** for successful plugin loading
3. **Test the plugin** with `/dungeon small easy`
4. **Configure** in `plugins/GenDungeonAsync/config.yml` if needed

## Development Tips
- Use `gradlew deployToServer` for every code change
- Old plugin JARs are automatically removed to prevent conflicts
- The new JAR will replace any previous versions automatically
- Check server console for any errors after reload
- Use `/pl` command to verify plugin is loaded

## Server Path
Your Paper server plugins folder: `D:\paper-1.21.7\plugins`
