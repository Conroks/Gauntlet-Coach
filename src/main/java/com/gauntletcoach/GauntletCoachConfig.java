package com.gauntletcoach;


import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("gauntletcoach")
public interface GauntletCoachConfig extends Config {

        @ConfigItem(
            keyName = "logAnimations",
            name = "Log NPC Animations",
            description = "Print NPC animations to the console"
    )
    default boolean logAnimations()
    {
        return false;
    }

    @ConfigItem(
            keyName = "trackDamage",
            name = "Track Damage",
            description = "Track damage dealt to Hunllef"
    )
    default boolean trackDamage()
    {
        return true;
    }
}


