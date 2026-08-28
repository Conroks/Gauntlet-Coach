# \# Gauntlet Coach

# 

# \*\*Old School RuneScape RuneLite plugin for Gauntlet/Corrupted Gauntlet data tracking and coaching.\*\*

# 

# Gauntlet Coach is a RuneLite plugin designed to help players learn and improve at the \*\*Gauntlet\*\* and \*\*Corrupted Gauntlet (CG)\*\* in Old School RuneScape.

# 

# Rather than simply tracking whether a player completes the encounter, Gauntlet Coach analyzes the Hunllef fight and provides post-fight performance metrics to help identify mistakes and areas for improvement.

# 

# The goal is to make learning the Gauntlet less about guessing what went wrong and more about understanding exactly where damage, missed prayers, positioning mistakes, and other inefficiencies occurred.

# 

# \## Features

# 

# Gauntlet Coach tracks combat data throughout the Hunllef encounter and presents a post-fight breakdown of the player's performance.

# 

# Current tracking includes:

# 

# \* Fight duration

# \* Total damage dealt

# \* Average DPS

# \* Total damage received

# \* Damage received while correctly praying

# \* Damage received while incorrectly praying

# \* Incorrect prayer hits

# \* Melee damage received

# \* Avoidable damage

# \* Percentage of damage received off-prayer

# \* Hunllef attack tracking

# \* Minion damage dealt

# \* Minion damage received

# \* Minion attack tracking

# 

# The plugin supports both the \*\*regular Gauntlet\*\* and \*\*Corrupted Gauntlet\*\* encounters.

# 

# \## How It Works

# 

# Gauntlet Coach monitors the Hunllef encounter through RuneLite's event system.

# 

# During the fight, the plugin tracks combat events such as NPC attacks, hitsplats, prayer state, damage sources, and encounter timing.

# 

# Once the encounter ends, the collected data is used to provide a performance breakdown that can help the player understand what happened during the fight and where they can improve.

# 

# The plugin is intended as a \*\*learning and coaching tool\*\*, rather than an automation tool. It does not play the encounter for the user.

# 

# \## Gauntlet Coach Panel

# 

# Gauntlet Coach adds a dedicated panel to the RuneLite sidebar.

# 

# The panel is used to display encounter information and coaching data collected by the plugin.

# 

# Future development will continue expanding the panel with more detailed post-fight analysis and actionable coaching feedback.

# 

# Longer term, I would like to expand Gauntlet Coach beyond the Gauntlet to support additional bosses. The goal is to build a broader PvM coaching profile that can identify a player's weaknesses across multiple encounters, make personalized suggestions, and eventually provide a bossing roadmap or progression ladder focused on developing the player's specific skill set.

# 

# \---

# 

# \## Development Notes

# 

# The following IDs are retained as a reference for development and encounter tracking.

# 

# \### Relevant Asset IDs

# 

# \#### Gauntlet

# 

# | NPC                |                        ID |

# | ------------------ | ------------------------: |

# | Hunllef            | 9021 / 9022 / 9023 / 9024 |

# | Hunllef Tornado    |                      9025 |

# | Crystal Rat        |                      9026 |

# | Crystal Spider     |                      9027 |

# | Crystal Bat        |                      9028 |

# | Crystal Unicorn    |                      9029 |

# | Crystal Scorpion   |                      9030 |

# | Crystal Wolf       |                      9031 |

# | Crystal Bear       |                      9032 |

# | Crystal Dragon     |                      9033 |

# | Crystal Dark Beast |                      9034 |

# 

# \#### Corrupted Gauntlet

# 

# | NPC                  |                        ID |

# | -------------------- | ------------------------: |

# | Corrupted Hunllef    | 9035 / 9036 / 9037 / 9038 |

# | Hunllef Tornado      |                      9039 |

# | Corrupted Rat        |                      9040 |

# | Corrupted Spider     |                      9041 |

# | Corrupted Bat        |                      9042 |

# | Corrupted Unicorn    |                      9043 |

# | Corrupted Scorpion   |                      9044 |

# | Corrupted Wolf       |                      9045 |

# | Corrupted Bear       |                      9046 |

# | Corrupted Dragon     |                      9047 |

# | Corrupted Dark Beast |                      9048 |

# 

# \### Animation IDs

# 

# \#### Hunllef

# 

# | Animation          |   ID |

# | ------------------ | ---: |

# | Standard attack    | 8419 |

# | Melee attack       | 8420 |

# | Ranged → Mage swap | 8754 |

# | Mage → Ranged swap | 8755 |

# | Summon tornadoes   | 8418 |



