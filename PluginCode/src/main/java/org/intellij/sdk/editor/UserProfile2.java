package org.intellij.sdk.editor;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.awt.GridBagConstraints.LINE_START;

public class UserProfile2 extends JFrame {
    UserProfile2(String title) {
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

// Remove From here


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
                String optionalFeedback = optionalRating.getText();

                userData userRecord = new userData();
                userRecord.setUserID("rehab001");
                userRecord.setRate(groupRatetxt);
                userRecord.setOptionalFeedback(optionalFeedback);

                userRecord.setCloudStore("1");
                SelectRecords dataAccessObject = new SelectRecords();

                int xyx = dataAccessObject.updateUserProfile2(userRecord,1);
                dispose();
            }
        });

        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}