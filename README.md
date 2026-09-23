# CauldronDispensers

<img src="https://github.com/Jannyboy11/CauldronDispensers/blob/master/img/icon.png?raw=true" alt="drawing" width="150"/>

Allows dispensers to fill cauldrons with water, lava or powder snow.

The behaviour mimics bucket behaviour when interacted by a player; already filled cauldrons contents can be overwritten.

Do you like this plugin? Then please leave a review on [MC-Foundry](https://mc-foundry.com/p/cauldrondispensers)!

### Compiling

Requirements: [JDK 25](https://jdk.java.net/archive/), [Apache Maven](https://maven.apache.org/), [BuildTools](https://www.spigotmc.org/wiki/buildtools/).

1. Install required dependencies into local maven repo:
- CraftBukkit: `java -jar BuildTools.jar --rev 26.3 --compile craftbukkit`
- Paper: `mvn ca.bkaw:paper-nms-maven-plugin:init --pl :PaperCompat`

2. Then compile CauldronDispensers:
- `mvn clean package`

3. Find the CauldronDispensers jar in `./BukkitPlugin/target`.

### Releasing

Run the following commands:

- `mvn release:prepare -DignoreSnapshots=true`
- `git push origin master --follow-tags`
- `mvn release:clean`
