package net.runelite.client.plugins.gauntletcoach;

import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.Actor;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.Hitsplat;
import java.util.Set;
import net.runelite.client.config.ConfigManager;
import com.google.inject.Provides;

@PluginDescriptor(
        name = "Gauntlet Coach")

public class GauntletCoachPlugin extends Plugin
{
    @Inject
    private Client client;

    private static final Set<Integer> GAUNTLET_NPC_IDS = Set.of(
            9021, 9026, 9027, 9028, 9029, 9030, 9031, 9032, 9033, 9034, // Gauntlet
            9035, 9040, 9041, 9042, 9043, 9044, 9045, 9046, 9047, 9048  // Corrupted
    );

    @Inject
    private GauntletCoachConfig config;
    /*
    ~~~~~~~~~~~~~~Gauntlet~~~~~~~~~~~~~~~

    Hunllef Id: 9021
    Crystal Rat: 9026
    Crystal Bat: 9028
    Crystal Spider: 9027
    Crystal Scorpion: 9030
    Crystal Wolf: 9031
    Crystal Unicorn: 9029
    Crystal Dragon: 9033
    Crystal Dark Beast: 9034
    Crystal Bear: 9032

    ~~~~~~~~~~~Corrupted Gauntlet~~~~~~~~~~~~~

    Corrupted Hunllef Id: 9035
    Corrupted Rat: 9040
    Corrupted Bat: 9042
    Corrupted Spider: 9041
    Corrupted Scorpion: 9044
    Corrupted Wolf: 9045
    Corrupted Unicorn: 9043
    Corrupted Dark Beast: 9048
    Corrupted Dragon: 9047
    Corrupted Bear: 9046


     */
    private int totalDamage = 0;
    private int attackCount = 0;

    @Override
    protected void startUp()
    {
        totalDamage = 0;
        attackCount = 0;
        System.out.println("Gauntlet Coach started");
    }

    @Override
    protected void shutDown()
    {
        System.out.println("Gauntlet Coach stopped");
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event)
    {
        NPC npc = event.getNpc();

        System.out.println(
                "NPC Spawned: " +
                        npc.getName() +
                        " ID: " +
                        npc.getId()
        );
    }

    @Provides
    GauntletCoachConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(GauntletCoachConfig.class);
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied event)
    {
        Actor actor = event.getActor();
        if (!(actor instanceof NPC)) return;

        NPC npc = (NPC) actor;
        if (!GAUNTLET_NPC_IDS.contains(npc.getId())) return;

        Hitsplat hitsplat = event.getHitsplat();
        if (hitsplat.isMine())
        {
            totalDamage += hitsplat.getAmount();
            System.out.println("Damage dealt: " + hitsplat.getAmount() + " | Total: " + totalDamage);
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event)
    {
        Actor actor = event.getActor();
        if (!(actor instanceof NPC)) return;

        NPC npc = (NPC) actor;
        if (!GAUNTLET_NPC_IDS.contains(npc.getId())) return;

        System.out.println(
                "NPC Animation: " + npc.getName() +
                        " AnimID: " + npc.getAnimation()
        );
    }

}