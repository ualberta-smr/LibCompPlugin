package org.intellij.sdk.editor;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import static java.awt.GridBagConstraints.LINE_START;

/**
 * The DialogUserProfile class contains the details of the user profile dialog
 * This dialog is accessable via the Tools menu
 */


public class DialogUserProfile extends JFrame {

    DialogUserProfile(String title) {
        super(title);
    }

    private JTextArea profileID;
    private DataUser userRecord;
    private JPanel mainPanel;
    private GridBagConstraints gbc = new GridBagConstraints();
    private GridBagLayout gbLayout = new GridBagLayout();
    private Font mainFont = new Font("SansSerif", Font.PLAIN, 12);
    private Font titleFont = new Font("SansSerif", Font.BOLD | Font.ITALIC, 12);

    void init() {
        mainPanel = new JPanel();
        mainPanel.setLayout(gbLayout);
        this.setTitle("LibComp User Profile");

        userRecord = new DataUser();
        DatabaseAccess dataAccessObject = new DatabaseAccess();
        userRecord = dataAccessObject.ReadUserProfile();

        int xlocation = 0;
        int ylocation = 1;

        TitledBorder title;
        title = BorderFactory.createTitledBorder("Please fill this form to the best of your ability: ");
        mainPanel.setBorder(title);

        this.setContentPane(mainPanel);
        gbc.anchor = LINE_START;

        // Question 1: occupation

        xlocation = 0;
        ylocation = 1;

        gbc.gridwidth = 7;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JLabel labelOccupation = new JLabel();
        labelOccupation.setText("What is your level of expertise that is related to software engineering?");
        labelOccupation.setFont(titleFont);
        gbLayout.setConstraints(labelOccupation, gbc);
        mainPanel.add(labelOccupation, gbc);

        xlocation = 0;
        ylocation = ylocation + 3;

        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        String[] choices = { "Undergraduate","Graduate", "Researcher","Software Developer"};
        final JComboBox<String> OccupationMenu = new JComboBox<String>(choices);
        OccupationMenu.setSelectedIndex(Integer.parseInt(userRecord.getOccupation())); // will be changes later
        OccupationMenu.setFont(mainFont);
        gbLayout.setConstraints(OccupationMenu, gbc);
        mainPanel.add(OccupationMenu, gbc);

        xlocation = 0;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JPanel fillerLine1 = new JPanel();
        gbLayout.setConstraints(fillerLine1, gbc);
        mainPanel.add(fillerLine1, gbc);

        xlocation = 0;

        // Question 2: types of projects

        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JLabel typeslbl = new JLabel();
        typeslbl.setText("On which kinds of projects did you regularly work over the last year?");
        typeslbl.setFont(titleFont);
        gbLayout.setConstraints(typeslbl, gbc);
        mainPanel.add(typeslbl, gbc);

        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JCheckBox proj_Check1 = new JCheckBox("Assignments from classes or online courses");
        if (userRecord.getProject1().equals("1"))
            proj_Check1.setSelected(true);
        proj_Check1.setFont(mainFont);
        gbLayout.setConstraints(proj_Check1, gbc);
        mainPanel.add(proj_Check1);

        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JCheckBox proj_Check2 = new JCheckBox("Personal projects used for learning and exploration that might be publicly available, but exist mainly for personal use ");
        proj_Check2.setFont(mainFont);
        if (userRecord.getProject2().equals("1"))
            proj_Check2.setSelected(true);
        gbLayout.setConstraints(proj_Check2, gbc);
        mainPanel.add(proj_Check2);

        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JCheckBox proj_Check3 = new JCheckBox("Small open-source or commercial projects with <= 2000 lines of code that are used by others ");
        proj_Check3.setFont(mainFont);
        if (userRecord.getProject3().equals("1"))
            proj_Check3.setSelected(true);
        gbLayout.setConstraints(proj_Check3, gbc);
        mainPanel.add(proj_Check3);

        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JCheckBox proj_Check4 = new JCheckBox("Medium open-source or commercial projects with > 2000 and < 50,000 lines of code that are used by others ");
        proj_Check4.setFont(mainFont);
        if (userRecord.getProject4().equals("1"))
            proj_Check4.setSelected(true);
        gbLayout.setConstraints(proj_Check4, gbc);
        mainPanel.add(proj_Check4);

        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JCheckBox proj_Check5 = new JCheckBox("Large open-source or commercial projects with >= 50,000 lines of code that are used by others ");
        proj_Check5.setFont(mainFont);
        if (userRecord.getProject5().equals("1"))
            proj_Check5.setSelected(true);
        gbLayout.setConstraints(proj_Check5, gbc);
        mainPanel.add(proj_Check5);

        // Question 3: types of teams

        xlocation = 0;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JPanel fillerLine2 = new JPanel();
        gbLayout.setConstraints(fillerLine2, gbc);
        mainPanel.add(fillerLine2, gbc);

        ylocation = ylocation + 4;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JLabel teamslbl = new JLabel();
        teamslbl.setText("In which kind of teams did you regularly work on a project in the last year? ");
        teamslbl.setFont(titleFont);
        gbLayout.setConstraints(teamslbl, gbc);
        mainPanel.add(teamslbl, gbc);

        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JCheckBox team_Check1 = new JCheckBox("Projects with me as the only regular committer");
        if (userRecord.getTeam1().equals("1"))
            team_Check1.setSelected(true);
        team_Check1.setFont(mainFont);
        gbLayout.setConstraints(team_Check1, gbc);
        mainPanel.add(team_Check1);

        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JCheckBox team_Check2 = new JCheckBox("Small teams (<= 3 regular committers in the last year) ");
        if (userRecord.getTeam2().equals("1"))
            team_Check2.setSelected(true);

        team_Check2.setFont(mainFont);
        gbLayout.setConstraints(team_Check2, gbc);
        mainPanel.add(team_Check2);

        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JCheckBox team_Check3 = new JCheckBox("Medium teams (> 3 and < 10 regular committers in the last year) ");
        if (userRecord.getTeam3().equals("1"))
            team_Check3.setSelected(true);

        team_Check3.setFont(mainFont);
        gbLayout.setConstraints(team_Check3, gbc);
        mainPanel.add(team_Check3);

        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JCheckBox team_Check4 = new JCheckBox("Large teams (>= 10 regular committers in the last year)");
        if (userRecord.getTeam4().equals("1"))
            team_Check4.setSelected(true);

        team_Check4.setFont(mainFont);
        gbLayout.setConstraints(team_Check4, gbc);
        mainPanel.add(team_Check4);

        // Question 4: general programming skills

        xlocation = 0;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JPanel fillerLine3 = new JPanel();
        gbLayout.setConstraints(fillerLine3, gbc);
        mainPanel.add(fillerLine3, gbc);

        xlocation = 0;
        gbc.gridwidth = 1;

        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JLabel labelProgramming = new JLabel();
        labelProgramming.setText("How would you rate your general programming skills? ");
        labelProgramming.setFont(titleFont);
        gbLayout.setConstraints(labelProgramming, gbc);
        mainPanel.add(labelProgramming, gbc);

        xlocation = 1;

        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Programming0 = new JRadioButton("Low");
        Programming0.setFont(mainFont);
        Programming0.setHorizontalTextPosition(SwingConstants.LEFT);
        Programming0.setActionCommand("0");
        Programming0.setSelected(true);
        gbLayout.setConstraints(Programming0, gbc);
        mainPanel.add(Programming0, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Programming1 = new JRadioButton("");
        Programming1.setFont(mainFont);
        Programming1.setActionCommand("1");
        gbLayout.setConstraints(Programming1, gbc);
        mainPanel.add(Programming1, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Programming2 = new JRadioButton("");
        Programming2.setFont(mainFont);
        Programming2.setActionCommand("2");
        gbLayout.setConstraints(Programming2, gbc);
        mainPanel.add(Programming2, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Programming3 = new JRadioButton("");
        Programming3.setFont(mainFont);
        Programming3.setActionCommand("3");
        gbLayout.setConstraints(Programming3, gbc);
        mainPanel.add(Programming3, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Programming4 = new JRadioButton("High - ");
        Programming4.setActionCommand("4");
        Programming4.setFont(mainFont);
        gbLayout.setConstraints(Programming4, gbc);
        mainPanel.add(Programming4, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Programming5 = new JRadioButton("No answer ");
        Programming5.setFont(mainFont);
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

        if (userRecord.getProgramming().equals("0"))
            Programming0.setSelected(true);
        if (userRecord.getProgramming().equals("1"))
            Programming1.setSelected(true);
        if (userRecord.getProgramming().equals("2"))
            Programming2.setSelected(true);
        if (userRecord.getProgramming().equals("3"))
            Programming3.setSelected(true);
        if (userRecord.getProgramming().equals("4"))
            Programming4.setSelected(true);
        if (userRecord.getProgramming().equals("5"))
            Programming5.setSelected(true);


        // Question 5: java programming skills

        xlocation = 0;
        ylocation = ylocation + 4;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;

        JLabel labelJava = new JLabel();
        labelJava.setText("How would you rate your programming skills in Java? ");
        labelJava.setFont(titleFont);
        gbLayout.setConstraints(labelJava, gbc);
        mainPanel.add(labelJava, gbc);

        xlocation = 1;

        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        gbc.gridwidth = 1;

        JRadioButton Java0 = new JRadioButton("Low");
        Java0.setHorizontalTextPosition(SwingConstants.LEFT);
        Java0.setSelected(true);
        Java0.setFont(mainFont);
        Java0.setActionCommand("0");
        gbLayout.setConstraints(Java0, gbc);
        mainPanel.add(Java0, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Java1 = new JRadioButton("");
        Java1.setActionCommand("1");
        Java1.setFont(mainFont);
        gbLayout.setConstraints(Java1, gbc);
        mainPanel.add(Java1, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Java2 = new JRadioButton("");
        Java2.setActionCommand("2");
        Java2.setFont(mainFont);
        gbLayout.setConstraints(Java2, gbc);
        mainPanel.add(Java2, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Java3 = new JRadioButton("");
        Java3.setActionCommand("3");
        Java3.setFont(mainFont);
        gbLayout.setConstraints(Java3, gbc);
        mainPanel.add(Java3, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Java4 = new JRadioButton("High - ");
        Java4.setActionCommand("4");
        Java4.setFont(mainFont);
        gbLayout.setConstraints(Java4, gbc);
        mainPanel.add(Java4, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Java5 = new JRadioButton("No answer ");
        Java5.setActionCommand("5");
        Java5.setFont(mainFont);
        gbLayout.setConstraints(Java5, gbc);
        mainPanel.add(Java5, gbc);

        ButtonGroup groupJava = new ButtonGroup();
        groupJava.add(Java0);
        groupJava.add(Java1);
        groupJava.add(Java2);
        groupJava.add(Java3);
        groupJava.add(Java4);
        groupJava.add(Java5);


        if (userRecord.getJavaSkills().equals("0"))
            Java0.setSelected(true);
        if (userRecord.getJavaSkills().equals("1"))
            Java1.setSelected(true);
        if (userRecord.getJavaSkills().equals("2"))
            Java2.setSelected(true);
        if (userRecord.getJavaSkills().equals("3"))
            Java3.setSelected(true);
        if (userRecord.getJavaSkills().equals("4"))
            Java4.setSelected(true);
        if (userRecord.getJavaSkills().equals("5"))
            Java5.setSelected(true);

        xlocation = 0;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JPanel fillerLine4 = new JPanel();
        gbLayout.setConstraints(fillerLine4, gbc);
        mainPanel.add(fillerLine4, gbc);

        // User Profile ID

        xlocation = 0;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        gbc.gridwidth = 7;

        JLabel labelProfile1 = new JLabel();
        labelProfile1.setText("User ID ");
        labelProfile1.setFont(titleFont);
        gbLayout.setConstraints(labelProfile1, gbc);
        mainPanel.add(labelProfile1, gbc);

        xlocation = 0;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;

        JLabel labelProfile2 = new JLabel();
        labelProfile2.setText("All your activities are anonymous, the only thing that we store is the random ID below.");
        labelProfile2.setFont(mainFont);
        gbLayout.setConstraints(labelProfile2, gbc);
        mainPanel.add(labelProfile2, gbc);

        xlocation = 0;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        profileID = new JTextArea(1, 65);
        profileID.setFont(mainFont);
        profileID.setText(userRecord.getUserID());
        profileID.setEnabled(false);
        gbLayout.setConstraints(profileID, gbc);
        mainPanel.add(profileID, gbc);

        xlocation = 0;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;

        JButton bUpdate=new JButton("    Update    ");
        bUpdate.setFont(mainFont);
        gbLayout.setConstraints(bUpdate, gbc);
        mainPanel.add(bUpdate, gbc);


        bUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String groupJavatxt =  groupJava.getSelection().getActionCommand();
                String groupProgtxt =  groupProgramming.getSelection().getActionCommand();
                String profileString = profileID.getText();

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


              //  DataUser finalUserRecord = new DataUser();
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
                try {
                    DatabaseAccess dataAccessObject = new DatabaseAccess();
                    dataAccessObject.updateUserProfile(userRecord);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dispose();
            }
        });
        this.pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }
}