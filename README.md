# Gauntlet Coach

A RuneLite plugin that provides **post-fight analytics and coaching for the Gauntlet and Corrupted Gauntlet (CG)** in Old School RuneScape.

Gauntlet Coach analyzes your Hunllef encounter to show what went wrong, where damage could have been avoided, and where you can improve — turning each attempt into actionable feedback.

## Features

* Fight duration and average DPS
* Damage dealt and received
* On-prayer vs. off-prayer damage
* Incorrect prayer hits
* Melee and avoidable damage
* Hunllef attack tracking
* Minion damage and attack tracking

Supports both **regular and Corrupted Gauntlet**.

## How It Works

Gauntlet Coach monitors combat events during the Hunllef encounter using RuneLite's event system. Once the fight ends, the collected data is used to generate a performance breakdown in the Gauntlet Coach sidebar panel.

The plugin is designed as a **learning tool** — it analyzes your performance rather than playing or solving the encounter for you.

## Future Plans

The long-term goal is to expand Gauntlet Coach beyond the Gauntlet into a broader **PvM coaching system** used to create a profile on a player's broader PvM performance, analyzing a players strengths and weak points across multiple bosses.
This profile can be used to identify recurring weaknesses and make personalized coaching suggestions and provide a roadmap or bossing ladder to develop the player's specific skill set.


## Development Reference

<details>
<summary><b>NPC IDs</b></summary>

### Gauntlet

```text id="rd4gve"
Hunllef:            9021 / 9022 / 9023 / 9024
Tornado:            9025
Crystal Rat:        9026
Crystal Spider:     9027
Crystal Bat:        9028
Crystal Unicorn:    9029
Crystal Scorpion:   9030
Crystal Wolf:       9031
Crystal Bear:       9032
Crystal Dragon:     9033
Crystal Dark Beast: 9034
```

### Corrupted Gauntlet

```text id="gv74vc"
Corrupted Hunllef:  9035 / 9036 / 9037 / 9038
Tornado:            9039
Corrupted Rat:      9040
Corrupted Spider:   9041
Corrupted Bat:      9042
Corrupted Unicorn:  9043
Corrupted Scorpion: 9044
Corrupted Wolf:     9045
Corrupted Bear:     9046
Corrupted Dragon:   9047
Corrupted Dark Beast: 9048
```

</details>

<details>
<summary><b>Animation IDs</b></summary>

```text id="xssb3p"
Hunllef Attack:                  8419
Hunllef Melee Attack:            8420
Ranged → Mage Swap:              8754
Mage → Ranged Swap:              8755
Summon Tornadoes:                8418
```

</details>
