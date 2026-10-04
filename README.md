# CauldronDispensers

<img src="https://github.com/Jannyboy11/CauldronDispensers/blob/master/img/CauldronDispensers4.webp?raw=true" alt="drawing" width="150"/>

Allows dispensers to fill cauldrons with water, lava or powder snow.

The behaviour mimics bucket behaviour when interacted by a player; already filled cauldrons contents can be overwritten.

Do you like this plugin? Then please leave a review on [MC-Foundry](https://mc-foundry.com/p/cauldrondispensers)!

## Credits

Special thanks to Icodak for creating the plugin's logo!
You can find them on [SpigotMC](https://www.spigotmc.org/members/icodak.473813/) and on [Discord](https://discordapp.com/users/345308025331908619).

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
