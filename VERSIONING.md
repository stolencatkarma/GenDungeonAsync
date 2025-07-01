# Git Versioning System

## 🎯 **Current Status: WORKING!**

Your Git versioning system is now successfully implemented and working. Here's what it does:

## **Version Format Explanation**

The version string `v1.0.0-3-ga8849bb-dirty` breaks down as:
- `v1.0.0` - Last git tag (release version)
- `3` - Number of commits since that tag
- `g` - Indicates git hash follows
- `a8849bb` - Short commit hash (7 characters)
- `dirty` - Working directory has uncommitted changes

## **Version Examples**

| Scenario | Version Output | Meaning |
|----------|----------------|---------|
| Clean release tag | `v1.0.0` | Exact release version |
| 2 commits after tag | `v1.0.0-2-g1a2b3c4` | Development version |
| Uncommitted changes | `v1.0.0-2-g1a2b3c4-dirty` | Local development |
| No tags exist | `1a2b3c4` | Just commit hash |

## **How It Works**

1. **Gradle Plugin**: `com.palantir.git-version` automatically calls `git describe`
2. **Fallback Protection**: If git fails, version defaults to `1.0.0-SNAPSHOT`
3. **Build Integration**: Version is automatically used in JAR filename
4. **plugin.yml**: Version is injected into the Paper plugin metadata

## **Usage Commands**

```bash
# Normal development build
gradlew build
# -> GenDungeonAsync-v1.0.0-3-ga8849bb.dirty-dev.jar

# Deploy to server
gradlew deployToServer
# -> Builds and copies to D:/paper-1.21.7/plugins

# Clean release build (after committing changes)
git commit -m "Release changes"
gradlew clean build
# -> GenDungeonAsync-v1.0.0-4-g2b3c4d5-dev.jar
```

## **Creating Releases**

```bash
# For patch release (1.0.0 -> 1.0.1)
git tag v1.0.1
gradlew build
# -> GenDungeonAsync-v1.0.1-dev.jar

# For minor release (1.0.1 -> 1.1.0)
git tag v1.1.0
gradlew build
# -> GenDungeonAsync-v1.1.0-dev.jar

# For major release (1.1.0 -> 2.0.0)
git tag v2.0.0
gradlew build
# -> GenDungeonAsync-v2.0.0-dev.jar
```

## **Benefits**

✅ **Automatic versioning** - No manual version updates needed  
✅ **Unique versions** - Every build has a unique version identifier  
✅ **Traceability** - Can trace any JAR back to exact git commit  
✅ **Development friendly** - Clear distinction between releases and dev builds  
✅ **Crash protection** - Fallback version if git is unavailable  

## **Next Steps**

1. **Use as-is** - The system is working perfectly for development
2. **Clean builds** - Commit changes before building for cleaner versions
3. **Release tagging** - Use `git tag vX.Y.Z` when ready to release
4. **Automatic deployment** - Use `gradlew deployToServer` for testing

The versioning system is production-ready and follows industry best practices!
