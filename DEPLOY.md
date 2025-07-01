# Quick Deployment Guide

## Development Workflow

### 1. Build and Deploy (Recommended)
```bash
gradlew deployToServer
```
This command will:
- **Clean** the build directory (removes old JARs)
- **Build** the plugin with current Git version
- **Remove** old GenDungeonAsync JAR files from plugins folder
- **Copy** only the new JAR to `D:\dev-server-1.21.4-towny\plugins`
- **Display** success message

### 2. Quick Copy (if already built)
```bash
gradlew copyToServer
```
- Removes old plugin JARs from server
- Copies the existing JAR to the server

### 3. Manual Method
```bash
gradlew build
# Then manually copy: build/libs/GenDungeonAsync-1.0.0-dev.jar
#                to: D:\dev-server-1.21.4-towny\plugins\
```

## After Deployment
1. **Restart your Paper server** or use `/reload confirm` (not recommended for production)
2. **Check server logs** for successful plugin loading
3. **Test the plugin** with `/dungeon small easy`
4. **Configure** in `plugins/GenDungeonAsync/config.yml` if needed

## Development Tips
- Use `gradlew deployToServer` for every code change
- **Only one JAR** is maintained - old versions are automatically cleaned
- The clean build ensures no version conflicts or accumulation
- Check server console for any errors after reload
- Use `/pl` command to verify plugin is loaded

## Benefits of Clean Deployment
- **No JAR accumulation** - Prevents having 5+ old JAR files
- **Single current version** - Always just one plugin JAR in both build and server
- **Clean development** - Fresh build every deployment
- **Version clarity** - Easy to identify current deployed version

## Server Path
Your Paper server plugins folder: `D:\dev-server-1.21.4-towny\plugins`
