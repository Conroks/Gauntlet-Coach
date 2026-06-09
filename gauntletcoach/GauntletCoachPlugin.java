package net.runelite.client.plugins.gauntletcoach;

import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
        name = "Gauntlet Coach")

public class GauntletCoachPlugin extends Plugin
{
    @Inject
    private Client client;

    @Override
    protected void startUp()
    {
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

        //Hunllef Id: 9021
        //Corrupted Hunllef Id: 9035
    }
}