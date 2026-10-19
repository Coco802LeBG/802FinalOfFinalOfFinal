# 802 Client (Fabric 1.21.11)

Modules : Crystal Reflex (base Marlow, MIT), PvP Counters, HUD, CPS, PotionHud, Ping, Speed, Direction,
Memory, Server, Durability, Food, World Time, World Info, Keystrokes, ArmorHud, AutoSprint,
Toggle Sneak, Fullbright, Zoom. Menu en jeu (Right Shift), éditeur de HUD, profils, GUI scale par écran,
installation de mods depuis Modrinth.

## Compiler
- GitHub : onglet Actions > Build > télécharger l'artefact `802-client`.
- En local : JDK 21+, puis `./gradlew build`. Le jar est dans build/libs/.

## Config
`.minecraft/config/802client.json` : modules on/off, réglages, GUI scale par écran (0 = défaut Minecraft, 1 à 6).

## Non testé
Ce code n'a jamais été compilé ni lancé. Si la compilation échoue, envoie les lignes contenant `error:`.
