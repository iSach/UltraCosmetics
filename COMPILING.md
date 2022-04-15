# Compiling

This project depends on NMS, which shouldn't be redistributed. This means you'll have to build some of the dependencies yourself. (TODO: a script/tool for this would be handy)

How to compile:
1. Use BuildTools normally and create a (remapped when supported) jar for every NMS version UC depends on. At the time of writing, the versions are: 1.8.8, 1.12.2, 1.16.5, 1.17.1\*, and 1.18.2\*.
2. Open a shell in the UC folder, and run `./gradlew clean obfuscate`. Using tasks `build` or `jar` will not produce a jar that works with 1.17 and up because of the new mappings. `clean` is sometimes required for some reason.
3. The built UC jar is at `build/libs/UltraCosmetics-<version>-<buildtype>.jar`. Do NOT use the other jar that says `obfuscated-donotuse`, it won't work.

\* make sure you use the --remapped flag when building this version

And you're done!
