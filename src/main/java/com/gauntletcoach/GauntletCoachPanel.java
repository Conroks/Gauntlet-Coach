package com.gauntletcoach;


import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.Border;


public class GauntletCoachPanel extends PluginPanel {

    //Creates the JLabel section to display the title
    JLabel gauntletCoach = new JLabel("Gauntlet Coach");

    //Creates the JLabel section to display the damage dealt
    int totalDamage = 0;
    JLabel damageDealt = new JLabel ("Damage Dealt: " + totalDamage);

    JPanel combatPanel = new JPanel ();

    //Constructor, adding each JLabel to the panel
    public GauntletCoachPanel(){
        this.add(gauntletCoach);
        this.add(combatPanel);
        combatPanel.add(damageDealt);


        //Using the Box layout to display the information
        this.setLayout(
                new BoxLayout(
                    this, BoxLayout.Y_AXIS
                )
        );

        combatPanel.setLayout(
                new BoxLayout(
                        combatPanel,BoxLayout.Y_AXIS
                )
        );


//        combatPanel.setBorder(
//            BorderFactory.createBevelBorder();
//
//        )
    }

    public void updateDamage(int damage){

        totalDamage = damage;
        damageDealt.setText("Damage Dealt: " + totalDamage);
    }



}

