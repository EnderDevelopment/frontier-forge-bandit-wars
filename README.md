# Minecraft Survival Mod

Frontier Reckoning is a Fabric Minecraft mod that adds a harsher survival system with persistent thirst, increased hunger exhaustion, exertion tracking, dehydration damage, status commands, and persistent player gang membership for Minecraft 1.20.1.

## Install

1. Install Java 17.
2. Install Minecraft 1.20.1 with Fabric Loader 0.15.11 or newer.
3. Install Fabric API for Minecraft 1.20.1.
4. Build the project with:

   `./gradlew build`

5. Copy `build/libs/frontier-reckoning-1.0.0.jar` into the instance's `mods` folder.
6. Launch the Fabric installation.

## Use

- Thirst starts full and decreases every second.
- Exertion increases thirst loss and decreases gradually over time.
- Hunger exhaustion increases continuously, including while standing still.
- Mining blocks and attacking entities increase exertion.
- Dehydration causes warning messages and periodic damage when thirst reaches zero.
- Use `/frontier status` to view current thirst and gang membership.
- Use `/frontier drink` to restore some thirst.
- Use `/frontier gang <name>` to join or create a recorded gang membership.
- Player thirst, exertion, and gang membership are stored in the world and copied when player data is transferred.

Build output is created in the `build/libs` directory.

---

More Minecraft mod projects by EnderDevelopment: https://github.com/EnderDevelopment

Project page: https://dash.enderdevelopment.com/community/317c33ec-7b2e-40fd-adc0-2010f50bfb40?utm_source=github&utm_medium=readme&utm_campaign=auto_publish&utm_content=317c33ec-7b2e-40fd-adc0-2010f50bfb40
