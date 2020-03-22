package org.intellij.sdk.editor;


import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.awt.GridBagConstraints.LINE_START;

public class UserProfile extends JFrame {
    UserProfile(String title) {
        super(title);
    }

    private JPanel mainPanel;
    private GridBagConstraints gbc = new GridBagConstraints();
    private GridBagLayout gbLayout = new GridBagLayout();


    void init() {
        mainPanel = new JPanel();
        mainPanel.setLayout(gbLayout);
        this.setTitle("LibComp user profile");

        int xlocation = 0;
        int ylocation = 1;

        TitledBorder title;
        title = BorderFactory.createTitledBorder("title");
        mainPanel.setBorder(title);

        this.setContentPane(mainPanel);
        gbc.anchor = LINE_START;

        // Rate the Plugin

        xlocation = 0;
        ylocation = 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JLabel labelText = new JLabel();
        labelText.setText("Rate the Plugin ?");
        gbLayout.setConstraints(labelText, gbc);
        mainPanel.add(labelText, gbc);


        xlocation = 1;

        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Rate0 = new JRadioButton("Low");
        Rate0.setHorizontalTextPosition(SwingConstants.LEFT);
        Rate0.setSelected(true);
        Rate0.setActionCommand("0");
        gbLayout.setConstraints(Rate0, gbc);
        mainPanel.add(Rate0, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Rate1 = new JRadioButton("");
        Rate1.setActionCommand("1");
        gbLayout.setConstraints(Rate1, gbc);
        mainPanel.add(Rate1, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Rate2 = new JRadioButton("");
        Rate2.setActionCommand("2");
        gbLayout.setConstraints(Rate2, gbc);
        mainPanel.add(Rate2, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Rate3 = new JRadioButton(" ");
        Rate3.setActionCommand("3");
        gbLayout.setConstraints(Rate3, gbc);
        mainPanel.add(Rate3, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Rate4 = new JRadioButton("high -");
        Rate4.setActionCommand("4");
        gbLayout.setConstraints(Rate4, gbc);
        mainPanel.add(Rate4, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Rate5 = new JRadioButton("No Answer ");
        Rate5.setActionCommand("5");
        gbLayout.setConstraints(Rate5, gbc);
        mainPanel.add(Rate5, gbc);


        ButtonGroup Rategroup = new ButtonGroup();
        Rategroup.add(Rate0);
        Rategroup.add(Rate1);
        Rategroup.add(Rate2);
        Rategroup.add(Rate3);
        Rategroup.add(Rate4);
        Rategroup.add(Rate5);

        xlocation = 0;
        ylocation = ylocation + 8;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JLabel optionaltext = new JLabel();
        optionaltext.setText("Enter Optional Feedback:");
        gbLayout.setConstraints(optionaltext, gbc);
        mainPanel.add(optionaltext, gbc);

        xlocation = 0;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JTextArea optionalRating = new JTextArea(2, 20);
        gbLayout.setConstraints(optionalRating, gbc);
        mainPanel.add(optionalRating, gbc);

// enter Occupation

        // Rate the Plugin

        xlocation = 0;
        ylocation =  ylocation + 8;

        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JLabel labelOccupation = new JLabel();
        labelOccupation.setText("Occupation");
        gbLayout.setConstraints(labelOccupation, gbc);
        mainPanel.add(labelOccupation, gbc);


        xlocation = 0;
        ylocation = ylocation + 3;

        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        String[] choices = { "ugrad","grad", "researcher","Software Developer"};
        final JComboBox<String> OccupationMenu = new JComboBox<String>(choices);
        gbLayout.setConstraints(OccupationMenu, gbc);
        mainPanel.add(OccupationMenu, gbc);

        xlocation = 0;
        // types of projects
        ylocation = ylocation + 4;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JLabel typeslbl = new JLabel();
        typeslbl.setText("On which kind of projects did you regulary work over the last year ?");
        gbLayout.setConstraints(typeslbl, gbc);
        mainPanel.add(typeslbl, gbc);

        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JCheckBox proj_Check1 = new JCheckBox("Assignmens from classes or online courses");
        gbLayout.setConstraints(proj_Check1, gbc);
        mainPanel.add(proj_Check1);

        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JCheckBox proj_Check2 = new JCheckBox("Personal projects ...");
        gbLayout.setConstraints(proj_Check2, gbc);
        mainPanel.add(proj_Check2);

        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JCheckBox proj_Check3 = new JCheckBox("Small Open ...");
        gbLayout.setConstraints(proj_Check3, gbc);
        mainPanel.add(proj_Check3);

        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JCheckBox proj_Check4 = new JCheckBox("Meduim Open ...");
        gbLayout.setConstraints(proj_Check4, gbc);
        mainPanel.add(proj_Check4);

        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JCheckBox proj_Check5 = new JCheckBox("Large Open ...");
        gbLayout.setConstraints(proj_Check5, gbc);
        mainPanel.add(proj_Check5);



        // types of projects
        ylocation = ylocation + 4;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JLabel teamslbl = new JLabel();
        teamslbl.setText("In which kind of teams did you regulary work on a project in the last year:");
        gbLayout.setConstraints(teamslbl, gbc);
        mainPanel.add(teamslbl, gbc);

        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JCheckBox team_Check1 = new JCheckBox("Projects with me and the only regular committer");
        gbLayout.setConstraints(team_Check1, gbc);
        mainPanel.add(team_Check1);

        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JCheckBox team_Check2 = new JCheckBox("Small teams (<= 3 regular committers in the last year)");
        gbLayout.setConstraints(team_Check2, gbc);
        mainPanel.add(team_Check2);

        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JCheckBox team_Check3 = new JCheckBox("Meduim teams (> 3 and < 10 regular committers in the last year)");
        gbLayout.setConstraints(team_Check3, gbc);
        mainPanel.add(team_Check3);

        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JCheckBox team_Check4 = new JCheckBox("Large teams (>= 10 regular committers in the last year)");
        gbLayout.setConstraints(team_Check4, gbc);
        mainPanel.add(team_Check4);

        // Rate the Plugin

        xlocation = 0;
        ylocation = ylocation + 4;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JLabel labelProgramming = new JLabel();
        labelProgramming.setText("Programming skills ?");
        gbLayout.setConstraints(labelProgramming, gbc);
        mainPanel.add(labelProgramming, gbc);


        xlocation = 1;

        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Programming0 = new JRadioButton("Low");
        Programming0.setHorizontalTextPosition(SwingConstants.LEFT);
        Programming0.setActionCommand("0");
        Programming0.setSelected(true);
        gbLayout.setConstraints(Programming0, gbc);
        mainPanel.add(Programming0, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Programming1 = new JRadioButton("");
        Programming1.setActionCommand("1");
        gbLayout.setConstraints(Programming1, gbc);
        mainPanel.add(Programming1, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Programming2 = new JRadioButton("");
        Programming2.setActionCommand("2");
        gbLayout.setConstraints(Programming2, gbc);
        mainPanel.add(Programming2, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Programming3 = new JRadioButton(" ");
        Programming3.setActionCommand("3");
        gbLayout.setConstraints(Programming3, gbc);
        mainPanel.add(Programming3, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Programming4 = new JRadioButton("high -");
        Programming4.setActionCommand("4");
        gbLayout.setConstraints(Programming4, gbc);
        mainPanel.add(Programming4, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Programming5 = new JRadioButton("No Answer ");
        Programming5.setActionCommand("5");
        gbLayout.setConstraints(Programming5, gbc);
        mainPanel.add(Programming5, gbc);


        ButtonGroup groupProgramming = new ButtonGroup();
        groupProgramming.add(Programming0);
        groupProgramming.add(Programming1);
        groupProgramming.add(Programming2);
        groupProgramming.add(Programming3);
        groupProgramming.add(Programming4);
        groupProgramming.add(Programming5);


        xlocation = 0;
        ylocation = ylocation + 4;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JLabel labelJava = new JLabel();
        labelJava.setText("Java skills ?");
        gbLayout.setConstraints(labelJava, gbc);
        mainPanel.add(labelJava, gbc);


        xlocation = 1;

        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Java0 = new JRadioButton("Low");
        Java0.setHorizontalTextPosition(SwingConstants.LEFT);
        Java0.setSelected(true);
        Java0.setActionCommand("0");
        gbLayout.setConstraints(Java0, gbc);
        mainPanel.add(Java0, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Java1 = new JRadioButton("");
        Java1.setActionCommand("1");
        gbLayout.setConstraints(Java1, gbc);
        mainPanel.add(Java1, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Java2 = new JRadioButton("");
        Java2.setActionCommand("2");
        gbLayout.setConstraints(Java2, gbc);
        mainPanel.add(Java2, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Java3 = new JRadioButton(" ");
        Java3.setActionCommand("3");
        gbLayout.setConstraints(Java3, gbc);
        mainPanel.add(Java3, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Java4 = new JRadioButton("high -");
        Java4.setActionCommand("4");
        gbLayout.setConstraints(Java4, gbc);
        mainPanel.add(Java4, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Java5 = new JRadioButton("No Answer ");
        Java5.setActionCommand("5");
        gbLayout.setConstraints(Java5, gbc);
        mainPanel.add(Java5, gbc);


        ButtonGroup groupJava = new ButtonGroup();
        groupJava.add(Java0);
        groupJava.add(Java1);
        groupJava.add(Java2);
        groupJava.add(Java3);
        groupJava.add(Java4);
        groupJava.add(Java5);

        xlocation = 0;
        ylocation = ylocation + 6;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JButton bUpdate=new JButton("Update");
        gbLayout.setConstraints(bUpdate, gbc);
        mainPanel.add(bUpdate, gbc);

        bUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {

                String groupRatetxt = Rategroup.getSelection().getActionCommand();
                String groupJavatxt =  groupJava.getSelection().getActionCommand();
                String groupProgtxt =  groupProgramming.getSelection().getActionCommand();
                String optionalFeedback = optionalRating.getText();
                String Occupationtxt = String.valueOf(OccupationMenu.getSelectedIndex());
                String proj_Check1txt = "0";
                String proj_Check2txt = "0";
                String proj_Check3txt = "0";
                String proj_Check4txt = "0";
                String proj_Check5txt = "0";

                String team_Check1txt = "0";
                String team_Check2txt = "0";
                String team_Check3txt = "0";
                String team_Check4txt = "0";

                if (proj_Check1.isSelected()) {proj_Check1txt = "1";}
                if (proj_Check2.isSelected()) {proj_Check2txt = "1";}
                if (proj_Check3.isSelected()) {proj_Check3txt = "1";}
                if (proj_Check4.isSelected()) {proj_Check4txt = "1";}
                if (proj_Check5.isSelected()) {proj_Check5txt = "1";}

                if (team_Check1.isSelected()) {team_Check1txt = "1";}
                if (team_Check2.isSelected()) {team_Check2txt = "1";}
                if (team_Check3.isSelected()) {team_Check3txt = "1";}
                if (team_Check4.isSelected()) {team_Check4txt = "1";}

                userData userRecord = new userData();

                userRecord.setUserID("rehab001");
                userRecord.setRate(groupRatetxt);
                userRecord.setOptionalFeedback(optionalFeedback);
                userRecord.setProject1(proj_Check1txt);
                userRecord.setProject2(proj_Check2txt);
                userRecord.setProject3(proj_Check3txt);
                userRecord.setProject4(proj_Check4txt);
                userRecord.setProject5(proj_Check5txt);
                userRecord.setTeam1(team_Check1txt);
                userRecord.setTeam2(team_Check2txt);
                userRecord.setTeam3(team_Check3txt);
                userRecord.setTeam4(team_Check4txt);

                userRecord.setProgramming(groupProgtxt);
                userRecord.setJavaSkills(groupJavatxt);
                userRecord.setOccupation(Occupationtxt);
                userRecord.setCloudStore("1");
                SelectRecords dataAccessObject = new SelectRecords();

                int xyx = dataAccessObject.updateUserProfile(userRecord,1);
                dispose();

             //   String message = groupRatetxt + groupJavatxt + groupProgtxt + " Occupation  "+ Occupationtxt + " Proj" + proj_Check1txt + proj_Check2txt + proj_Check3txt+ proj_Check4txt + proj_Check5txt + "Teams :" + team_Check1txt + team_Check2txt + team_Check3txt + team_Check4txt;
             //   JOptionPane.showMessageDialog(null,message);
            }
        });

        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}