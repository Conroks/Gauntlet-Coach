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
import net.runelite.api.Prayer;

@PluginDescriptor(
        name = "Gauntlet Coach")

public class GauntletCoachPlugin extends Plugin {
    @Inject
    private Client client;
    //initializes the set of relevant npc id's
    private static final Set<Integer> HUNLLEF_IDS = Set.of(
            9021, 9022, // Crystal Hunllef ID
            9035, 9036, 9037  // Corrupted Hunllef ID
    );

    private static final Set<Integer> TORNADO_IDS = Set.of(
            9025, //Regular Gauntlet
            9039  //Corrupted Gauntlet
    );

    private static final Set<Integer> MINION_IDS = Set.of(
            9026, 9027, 9028, 9029, 9030, 9031, 9032, 9033, 9034, //Regular Gauntlet
            9040, 9041, 9042, 9043, 9044, 9045, 9046, 9047, 9048  //Corrupted Gauntlet
    );


    @Inject
    private GauntletCoachConfig config;

    /* Relevant asset ID tracking
    ~~~~~~~~~~~~~~Gauntlet~~~~~~~~~~~~~~~

    Hunllef Id: 9021/9022
    Hunllef Tornados: 9025
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

    Corrupted Hunllef Id: 9035/9036/9037
    Hunllef Tornados : 9039
    Corrupted Rat: 9040
    Corrupted Bat: 9042
    Corrupted Spider: 9041
    Corrupted Scorpion: 9044
    Corrupted Wolf: 9045
    Corrupted Unicorn: 9043
    Corrupted Dark Beast: 9048
    Corrupted Dragon: 9047
    Corrupted Bear: 9046

    Animation ID tracking
        Hunllef
             Hunllef attack:8419
             Hunllef Melee attack:8420
             Hunllef Ranged to Mage swap animation: 8754
             Hunllef Mage to Ranged swap animation: 8755
        */

    private int hunllefDamageDealt = 0;
    private int hunllefDamageReceived = 0;
    private int hunllefAttackCount = 0;

    private int minionDamageDealt = 0;
    private int minionDamageReceived = 0;
    private int minionAttackCount = 0;

    private enum HunllefStyle {MAGE,RANGED,MELEE}
    private HunllefStyle currentHunllefStyle = HunllefStyle.RANGED;
    private boolean lastAttackOnPrayer = false;

    private static final int hunllefAttackAnimation = 8419;
    private static final int hunllefMageSwitchAnimation = 8754;
    private static final int hunllefRangedSwitchAnimation = 8755;
    private static final int hunllefMeleeAttackAnimation = 8420;

    private int hunllefDamageReceivedOnPrayer = 0;
    private int hunllefDamageReceivedOffPrayer = 0;

    //Initialize the plugin
    @Override
    protected void startUp() {
        hunllefDamageDealt = 0;
        hunllefDamageReceived = 0;
        hunllefAttackCount = 0;
        minionDamageDealt = 0;
        minionDamageReceived = 0;
        minionAttackCount = 0;
        System.out.println("Gauntlet Coach started");
    }

    @Override
    protected void shutDown() {
        System.out.println("Gauntlet Coach stopped");
    }

    //When NPCs are rendered in, reports the NPC Id number to the terminal
    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        NPC npc = event.getNpc();

        System.out.println(
                "NPC Spawned: " +
                        npc.getName() +
                        " ID: " +
                        npc.getId()
        );
    }

    //Connects config file
    @Provides
    GauntletCoachConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(GauntletCoachConfig.class);
    }

    //Tracks Hitsplats and sums damage, outputting to terminal
    @Subscribe
    public void onHitsplatApplied(HitsplatApplied event) {
        Actor actor = event.getActor();
        Hitsplat hitsplat = event.getHitsplat();

        if(actor == client.getLocalPlayer()) {
            System.out.println("Tick=" + client.getTickCount() + " HITSPLAT received, amount=" + hitsplat.getAmount() + " lastAttackOnPrayer=" + lastAttackOnPrayer);
            if(lastAttackOnPrayer) {
                hunllefDamageReceivedOnPrayer += hitsplat.getAmount();
                System.out.println("Damage received ON correct prayer: " + hitsplat.getAmount());
            }else {
                hunllefDamageReceivedOffPrayer += hitsplat.getAmount();
                System.out.println("Change Prayer, Damage received during incorrect Prayer: " + hitsplat.getAmount());
            }
        }

        if ((actor instanceof NPC)) {
            NPC npc = (NPC) actor;

            //Test to see NPC ID when hitting creature ~~~~DEBUGGING + INFO GATHERING
            //System.out.println("Hit NPC ID: " + npc.getId() + " Name: " + npc.getName() + " Amount: " + hitsplat.getAmount() + " isMine: " + hitsplat.isMine());

            if (hitsplat.isMine()) {
                if (HUNLLEF_IDS.contains(npc.getId())) {
                    hunllefDamageDealt += hitsplat.getAmount();
                    System.out.println("Damage to Hunllef: " + hitsplat.getAmount() + " ~ Total: " + hunllefDamageDealt);
                }
            }
        }
    }



    //Checks players Prayer against hunllef's current attack and outputs
    private void checkHunllefAttack(HunllefStyle style) {
        int tick = client.getTickCount();
        boolean onPrayer = false;

        if(style == HunllefStyle.RANGED) {
            onPrayer = client.isPrayerActive(Prayer.PROTECT_FROM_MISSILES);
        } else if (style == HunllefStyle.MAGE) {
            onPrayer = client.isPrayerActive(Prayer.PROTECT_FROM_MAGIC);
        } else if (style == HunllefStyle.MELEE){
            onPrayer = client.isPrayerActive(Prayer.PROTECT_FROM_MELEE);
        }

        lastAttackOnPrayer = onPrayer;
        hunllefAttackCount++;

        System.out.println("Tick=" + tick + " RESULT: Hunllef attacked with " + style + " ~ onPrayer=" + onPrayer + " ~ lastAttackOnPrayer now=" + lastAttackOnPrayer);
    }

    //Tracks NPC animation changes
    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        Actor actor = event.getActor();

        System.out.println("RAW animation event fired, actor=" + actor.getClass().getName());

        if (!(actor instanceof NPC)) return;

        NPC npc = (NPC) actor;
        if (!HUNLLEF_IDS.contains(npc.getId())) {

            System.out.println(
                    "NPC Animation Event: "
                            + npc.getName()
                            + " ID="
                            + npc.getId()
                            + " Anim="
                            + npc.getAnimation()
            );
        }

        int animID = npc.getAnimation();
        int tick = client.getTickCount();

        System.out.println("Tick=" + tick + " AnimID=" + animID);

        if (animID == hunllefRangedSwitchAnimation) {
            currentHunllefStyle = HunllefStyle.RANGED;
            System.out.println("Tick=" + tick + " >>> SWAP to RANGED");
        } else if (animID == hunllefMageSwitchAnimation) {
            currentHunllefStyle = HunllefStyle.MAGE;
            System.out.println("Tick=" + tick + " >>> SWAP to MAGE");
        } else if (animID == hunllefMeleeAttackAnimation) {
            System.out.println("Tick=" + tick + " >>> MELEE branch triggered, calling checkHunllefAttack(MELEE)");
            checkHunllefAttack(HunllefStyle.MELEE);
        } else if (animID == hunllefAttackAnimation) {
            System.out.println("Tick=" + tick + " >>> REGULAR branch triggered, calling checkHunllefAttack(" + currentHunllefStyle + ")");
            checkHunllefAttack(currentHunllefStyle);
        }
    }
}