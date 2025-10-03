# Rimelib
Utility library for my Minecraft mods.

This mod doesn't do anything by itself but provides certain convenient ways of doing stuff for other mods.

---

### If you want to use this mod in your mod:

Add the repository:
```kt
maven("https://ancientri.me/maven/releases") {
    name = "AncientRime"
}
```
Add the dependency:
```kt
modImplementation("me.ancientri:rimelib:$mod_version")
```
You can also `include()` the jar if you don't want the user to download it manually.

---

The versioning scheme is: `major.minor.hotfix`:
- Major is increased when I feel like it.
- Minor will be incremented for breaking changes.
- Hotfix will be incremented for non-breaking changes or additions.

The same version of the library might be released for multiple minecraft versions with minecraft-version-specific fixes included, potentially big enough to be breaking changes.


