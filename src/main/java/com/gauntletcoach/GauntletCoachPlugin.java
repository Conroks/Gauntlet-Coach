package com.gauntletcoach;

import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import net.runelite.client.config.ConfigManager;
import com.google.inject.Provides;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

@PluginDescriptor(name = "Gauntlet Coach")

public class GauntletCoachPlugin extends Plugin {
    @Inject
    private Client client;
    //initializes the set of relevant npc id's
    private static final Set<Integer> HUNLLEF_IDS = Set.of(
            9021, 9022, 9023, 9024, // Crystal Hunllef ID
            9035, 9036, 9037, 9038  // Corrupted Hunllef ID
    );

    private static final Set<Integer> TORNADO_IDS = Set.of(
            9025, //Regular Gauntlet
            9039  //Corrupted Gauntlet
    );

    private final Set<NPC> activeTornadoes = new HashSet<>();

    private static final Set<Integer> MINION_IDS = Set.of(
            9026, 9027, 9028, 9029, 9030, 9031, 9032, 9033, 9034, //Regular Gauntlet
            9040, 9041, 9042, 9043, 9044, 9045, 9046, 9047, 9048  //Corrupted Gauntlet
    );

    @Inject
    private GauntletCoachPanel gauntletCoachPanel;

    //sets up the button on the toolbar
    @Inject
    private ClientToolbar gauntletCoachToolbar;

    @Inject
    private GauntletCoachConfig config;

    private boolean fightActive = false;
    private int fightStartTick = -1;
    private int hunllefDamageReceived = 0;
    private int hunllefMeleeDamageReceived = 0;
    private int hunllefDamageDealt = 0;
    private int hunllefAttackCount = 0;

    private int minionDamageDealt = 0;
    private int minionDamageReceived = 0;
    private int minionAttackCount = 0;

    private enum HunllefStyle {MAGE,RANGED,MELEE}
    private HunllefStyle currentHunllefStyle = HunllefStyle.RANGED;
    private HunllefStyle lastAttackStyle = HunllefStyle.RANGED;
    private boolean lastAttackOnPrayer = false;
    private boolean waitingForPrayerCorrection = false;
    private HunllefStyle expectedPrayerStyle = null;
    private int hunllefStyleSwapTick = -1;

    private int totalPrayerReactionTicks = 0;
    private int prayerSwitchCount = 0;
    private int fastestPrayerReaction = Integer.MAX_VALUE;
    private int slowestPrayerReaction = -1;


    private enum DamageSource {HUNLLEF, TORNADO, MINION, FLOOR, UNKNOWN}
    private DamageSource pendingDamageSource = DamageSource.UNKNOWN;
    private int pendingHunllefAttackTick = -1;

    private static final int hunllefAttackAnimation = 8419;
    private static final int hunllefMageSwitchAnimation = 8754;
    private static final int hunllefRangedSwitchAnimation = 8755;
    private static final int hunllefMeleeAttackAnimation = 8420;
    private static final int HUNLLEF_DAMAGE_WINDOW = 2;

    private int hunllefDamageReceivedOnPrayer = 0;
    private int hunllefDamageReceivedOffPrayer = 0;
    private int incorrectPrayerHits = 0;


    private NavigationButton gcButton;

    //Initialize the plugin
    @Override
    protected void startUp() {

        resetEncounter();

        final BufferedImage gcIcon = ImageUtil.loadImageResource(getClass(), "gcIcon.png");

        //Sets up the navigation button on the side to open the Gauntlet Coach panel
        gcButton = NavigationButton.builder()
                .panel(gauntletCoachPanel)
                .icon(gcIcon)
                .tooltip("Learn the Gauntlet!")
                .build();

        //Adds the button to the ClientToolbar
        gauntletCoachToolbar.addNavigation(gcButton);

        System.out.println("~~~~~~~Gauntlet Coach started~~~~~~~");
    }

    @Override
    protected void shutDown() {

        if(gcButton != null) {
            gauntletCoachToolbar.removeNavigation(gcButton);
            gcButton = null;
        }

        resetEncounter();
    }

    //When NPCs are rendered in, reports the NPC Id number to the terminal
    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        NPC npc = event.getNpc();

        if (TORNADO_IDS.contains(npc.getId())) {
            activeTornadoes.add(npc);

            System.out.println("Tornado spawned. ActiveTornadoes: " + activeTornadoes.size());
        }

    }

    //Connects config file
    @Provides
    GauntletCoachConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(GauntletCoachConfig.class);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        int currentTick = client.getTickCount();
        int prayerReactionTicks = -1;

        if (pendingHunllefAttackTick !=-1) {

            int attackAge = currentTick - pendingHunllefAttackTick;

            if (attackAge > HUNLLEF_DAMAGE_WINDOW) {
                pendingHunllefAttackTick = -1;
            }
        }


        //Checks each tick after Hunllef switches his damage type for the player's Prayer Reaction
        if (waitingForPrayerCorrection){
            if (isCorrectPrayerActive(expectedPrayerStyle)){
                prayerReactionTicks = currentTick - hunllefStyleSwapTick ;
                waitingForPrayerCorrection = false;
                System.out.println("Prayer reation time: "  + prayerReactionTicks + " ticks.");
                totalPrayerReactionTicks += prayerReactionTicks;

                if(prayerReactionTicks < fastestPrayerReaction) {
                    fastestPrayerReaction = prayerReactionTicks;
                }

                if(prayerReactionTicks > slowestPrayerReaction) {
                    slowestPrayerReaction = prayerReactionTicks;
                }
                prayerSwitchCount++;
                prayerReactionTicks = -1;
            }
        }
    }

    private DamageSource resolveDamageSource(int currentTick){
        if (pendingHunllefAttackTick !=1) {
            int attackAge = currentTick - pendingHunllefAttackTick;

            if(attackAge <= HUNLLEF_DAMAGE_WINDOW){
                return DamageSource.HUNLLEF;
            }
        }
        return DamageSource.UNKNOWN;
    }

    private boolean isCorrectPrayerActive(HunllefStyle style){
        if(style == HunllefStyle.RANGED) {
            return client.isPrayerActive(Prayer.PROTECT_FROM_MISSILES);
        } else if (style == HunllefStyle.MAGE) {
            return client.isPrayerActive(Prayer.PROTECT_FROM_MAGIC);
        } else if (style == HunllefStyle.MELEE){
             return client.isPrayerActive(Prayer.PROTECT_FROM_MELEE);
        }
        return false;
    }

    private int getTornadoesOnPlayer(){
        Player player = client.getLocalPlayer();
        if(player == null){
            return 0;
        }
        int tornadoesOnPlayer = 0;

        for(NPC tornado : activeTornadoes) {
            if (tornado.getWorldLocation().equals(player.getWorldLocation())){
                tornadoesOnPlayer++;
            }
        }
        return tornadoesOnPlayer;
    }


    //Checks players Prayer against hunllef's current attack and outputs
    private void checkHunllefAttack(HunllefStyle style) {
        boolean onPrayer = false;

        if(!fightActive){
            fightActive = true;
            fightStartTick = client.getTickCount();
        }

        if(style == HunllefStyle.RANGED) {
            onPrayer = client.isPrayerActive(Prayer.PROTECT_FROM_MISSILES);
        } else if (style == HunllefStyle.MAGE) {
            onPrayer = client.isPrayerActive(Prayer.PROTECT_FROM_MAGIC);
        } else if (style == HunllefStyle.MELEE){
            onPrayer = client.isPrayerActive(Prayer.PROTECT_FROM_MELEE);
        }
        lastAttackStyle = style;
        lastAttackOnPrayer = onPrayer;
        hunllefAttackCount++;
        pendingDamageSource = DamageSource.HUNLLEF;
        pendingHunllefAttackTick = client.getTickCount();
        System.out.println("Hunllef attacked with " + style + " ~ Player on prayer: " + onPrayer);

    }

    //Tracks Hitsplats and sums damage, outputting to terminal
    @Subscribe
    public void onHitsplatApplied(HitsplatApplied event) {
        Actor actor = event.getActor();
        Hitsplat hitsplat = event.getHitsplat();

        //Checks damage hunllef has done to player
        if(actor == client.getLocalPlayer()) {

            DamageSource damageSource = resolveDamageSource(client.getTickCount());

            if(damageSource == DamageSource.HUNLLEF) {

                hunllefDamageReceived += hitsplat.getAmount();

                if (lastAttackStyle == HunllefStyle.MELEE) {
                    hunllefMeleeDamageReceived += hitsplat.getAmount();
                }

                if (lastAttackOnPrayer) {
                    hunllefDamageReceivedOnPrayer += hitsplat.getAmount();
                    System.out.println("Damage received ON correct prayer: " + hitsplat.getAmount());
                } else {
                    hunllefDamageReceivedOffPrayer += hitsplat.getAmount();
                    System.out.println("Change Prayer, Damage received during incorrect Prayer: " + hitsplat.getAmount());
                    incorrectPrayerHits++;
                }
                pendingHunllefAttackTick = -1;
            }
        }

        //Determines player damage output
        if ((actor instanceof NPC)) {
            NPC npc = (NPC) actor;

            if (hitsplat.isMine()) {
                if (HUNLLEF_IDS.contains(npc.getId())) {
                    hunllefDamageDealt += hitsplat.getAmount();
                    gauntletCoachPanel.updateDamage(hunllefDamageDealt);
                    System.out.println("Damage to Hunllef: " + hitsplat.getAmount() + " ~ Total: " + hunllefDamageDealt);

                }
            }
        }
    }

    //Tracks NPC animation changes
    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        Actor actor = event.getActor();
        if (!(actor instanceof NPC)) return;

        NPC npc = (NPC) actor;
        if (!HUNLLEF_IDS.contains(npc.getId())) return;

        int animID = npc.getAnimation();

        if (animID == hunllefRangedSwitchAnimation) {
            currentHunllefStyle = HunllefStyle.RANGED;
            expectedPrayerStyle = HunllefStyle.RANGED;
            hunllefStyleSwapTick = client.getTickCount();
            System.out.println("Hunllef swapped to RANGED");
            waitingForPrayerCorrection = true;
        } else if (animID == hunllefMageSwitchAnimation) {
            currentHunllefStyle = HunllefStyle.MAGE;
            expectedPrayerStyle = HunllefStyle.MAGE;
            hunllefStyleSwapTick = client.getTickCount();
            System.out.println("Hunllef swapped to MAGE");
            waitingForPrayerCorrection = true;
        } else if (animID == hunllefMeleeAttackAnimation) {
            checkHunllefAttack(HunllefStyle.MELEE);
            System.out.println("Hunllef hit with MELEE");
        } else if (animID == hunllefAttackAnimation) {
            checkHunllefAttack(currentHunllefStyle);
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event)
    {
        NPC npc = event.getNpc();

        if(TORNADO_IDS.contains(npc.getId())){
            activeTornadoes.remove(npc);

            System.out.println("Tornado despawned. Active tornadoes: " + activeTornadoes.size());
        }

        if (HUNLLEF_IDS.contains(npc.getId()) && fightActive)
        {
            int fightEndTick = client.getTickCount();
            int fightDurationTicks = (fightEndTick - fightStartTick);

            outputResults(fightDurationTicks);
            resetEncounter();

        }
    }

    private void outputResults(int fightDurationTicks)
    {

        //Calculates how long the fight lasted based on the tick count
        int fightDurationSeconds;
        int fightDurationMinutes;
        fightDurationSeconds = (int) (fightDurationTicks *.6);
        fightDurationMinutes = fightDurationSeconds / 60;
        int fightDisplaySeconds = fightDurationSeconds % 60;
        double playerDPS = 0.0;
        double percentageOffPrayer = 0.0;
        double averagePrayerReaction = 0.0;

        //Calculates the Players DPS
        if(fightDurationSeconds > 0) {
            playerDPS = (double) hunllefDamageDealt / fightDurationSeconds;
        }
        int avoidableDamageTotal = hunllefMeleeDamageReceived + hunllefDamageReceivedOffPrayer;

        if(hunllefDamageReceived > 0) {
            percentageOffPrayer = (double) hunllefDamageReceivedOffPrayer / hunllefDamageReceived * 100;
        }

        if (prayerSwitchCount > 0){
        averagePrayerReaction = (double) totalPrayerReactionTicks/prayerSwitchCount;
        }

        System.out.println("~~~~~~~~~~~~~~GAUNTLET COMPLETED~~~~~~~~~~~~~~");
        System.out.println("The fight took " + fightDurationTicks + " total ticks");
        System.out.println("The fight was " + fightDurationMinutes + ":" + String.format("%02d", fightDisplaySeconds) +" long");
        System.out.println("Total Damage Received From Hunllef: " + hunllefDamageReceived);
        System.out.println("Total Melee Damage Received From Hunllef: " + hunllefMeleeDamageReceived);
        System.out.println("Total Attacks Received From Hunllef: " + hunllefAttackCount);
        System.out.println("Total Damage Received OFF Prayer: " + hunllefDamageReceivedOffPrayer);
        System.out.println("Total Damage Received ON Prayer: " + hunllefDamageReceivedOnPrayer);
        System.out.println("Avoidable Damage " + avoidableDamageTotal);
        System.out.println("Percentage of Damage Received OFF Prayer " + String.format("%.2f", percentageOffPrayer) + "%");

        System.out.println("Total Damage Dealt to Hunllef: " + hunllefDamageDealt);
        System.out.println("Average DPS over the fight " + String.format("%.2f", playerDPS));

        System.out.println("Average Prayer Reaction time: " + String.format("%.2f", averagePrayerReaction) + " ticks");
        System.out.println("Fastest Prayer Reaction time: " + fastestPrayerReaction + " ticks");
        System.out.println("Slowest Prayer Reaction time: " + slowestPrayerReaction + " ticks");

        System.out.println("Total Damage Dealt To Minions: " + minionDamageDealt);
        System.out.println("Total Damage Received From Minions: " + minionDamageReceived);

    }

    private void resetEncounter()
    {
        hunllefDamageReceived = 0;
        hunllefMeleeDamageReceived = 0;
        hunllefDamageDealt = 0;
        hunllefAttackCount = 0;

        minionDamageDealt = 0;
        minionAttackCount = 0;
        minionDamageReceived = 0;

        waitingForPrayerCorrection = false;
        expectedPrayerStyle = null;
        hunllefStyleSwapTick = -1;
        hunllefDamageReceivedOnPrayer = 0;
        hunllefDamageReceivedOffPrayer = 0;

        currentHunllefStyle = HunllefStyle.RANGED;
        lastAttackStyle = HunllefStyle.RANGED;
        lastAttackOnPrayer = false;
        pendingDamageSource = DamageSource.UNKNOWN;
        pendingHunllefAttackTick = -1;
        fightActive = false;
        fightStartTick = -1;
        incorrectPrayerHits = 0;
        totalPrayerReactionTicks = 0;
        prayerSwitchCount = 0;
        fastestPrayerReaction = Integer.MAX_VALUE;
        slowestPrayerReaction = 0;
        activeTornadoes.clear();

        System.out.println("Gauntlet plugin reset");

    }
}
