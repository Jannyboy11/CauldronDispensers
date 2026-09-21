# DispenserCauldrons

Allows dispensers to fill cauldrons with water, lava or powder snow.

The behaviour mimics bucket behaviour when interacted by a player; already filled cauldrons contents can be overwritten.

### Compiling

Requirements: [JDK 25](https://jdk.java.net/archive/), [Apache Maven](https://maven.apache.org/), [BuildTools](https://www.spigotmc.org/wiki/buildtools/).

1. Install required dependencies into local maven repo:
- CraftBukkit: `java -jar BuildTools.jar --rev 26.3 --compile craftbukkit`
- Paper: `mvn ca.bkaw:paper-nms-maven-plugin:init --pl :PaperCompat`

2. Then compile CauldronDispensers:
- `mvn clean package`

3. Find the CauldronDispensers jar in `./BukkitPlugin/target`.
