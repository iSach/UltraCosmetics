# Compiling

This project depends on NMS, which shouldn't be redistributed. This means you'll have to build some of the dependencies yourself. (TODO: a script/tool for this would be handy)

How to compile:
1. Use BuildTools normally and create a (remapped when supported) jar for every NMS version UC depends on. At the time of writing, the versions are: 1.8, 1.8.3, 1.8.8, 1.9.2, 1.9.4, 1.10.2, 1.11.2, 1.12.2, 1.13, 1.13.2, 1.14.4, 1.15.2, 1.16.1, 1.16.3, 1.16.4, 1.17.1\*, and 1.18\*. Dropping some of these versions is being considered.
2. Download [SpecialSource](https://repo.maven.apache.org/maven2/net/md-5/SpecialSource/1.11.0/SpecialSource-1.11.0-shaded.jar) and put it at `tooling/specialsource/SpecialSource.jar` in the UC folder.
3. Open a shell in the UC folder, and run `./gradlew clean obfuscate`. Using tasks `build` or `jar` will not produce a jar that works with 1.17 and up because of the new mappings. `clean` is sometimes required for some reason.
4. The built UC jar is at `build/libs/UltraCosmetics-<version>-RELEASE.jar`. Do NOT use the other jar that ends in `-obf.jar`, it won't work.

\* make sure you use the --remapped flag when building this version

And you're done!
