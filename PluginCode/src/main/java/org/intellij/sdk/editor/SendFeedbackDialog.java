package org.intellij.sdk.editor;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.awt.GridBagConstraints.LINE_START;

public class SendFeedbackDialog extends JFrame {
    SendFeedbackDialog(String title) {
        super(title);
    }

    private JPanel mainPanel;
    private GridBagConstraints gbc = new GridBagConstraints();
    private GridBagLayout gbLayout = new GridBagLayout();
    private Font mainFont = new Font("SansSerif", Font.PLAIN, 12);
    private Font titleFont = new Font("SansSerif", Font.BOLD | Font.ITALIC, 12);


    void init() {
        mainPanel = new JPanel();
        mainPanel.setLayout(gbLayout);
        this.setTitle("LibComp ");

        int xlocation = 0;
        int ylocation = 1;

        TitledBorder title;
        title = BorderFactory.createTitledBorder("Please fill this form to the best of your ability: ");
        mainPanel.setBorder(title);


        this.setContentPane(mainPanel);
        gbc.anchor = LINE_START;

        // Rate the Plugin
        xlocation = 0;
        ylocation = 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JLabel labelText = new JLabel();
        labelText.setText("How would you rate your experience with LibComp? ");
        labelText.setFont(titleFont);
        gbLayout.setConstraints(labelText, gbc);
        mainPanel.add(labelText, gbc);

        xlocation = 1;

        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Rate0 = new JRadioButton("Low");
        Rate0.setHorizontalTextPosition(SwingConstants.LEFT);
        Rate0.setSelected(true);
        Rate0.setFont(mainFont);
        Rate0.setActionCommand("0");
        gbLayout.setConstraints(Rate0, gbc);
        mainPanel.add(Rate0, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Rate1 = new JRadioButton("");
        Rate1.setActionCommand("1");
        Rate1.setFont(mainFont);
        gbLayout.setConstraints(Rate1, gbc);
        mainPanel.add(Rate1, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Rate2 = new JRadioButton("");
        Rate2.setActionCommand("2");
        Rate2.setFont(mainFont);
        gbLayout.setConstraints(Rate2, gbc);
        mainPanel.add(Rate2, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Rate3 = new JRadioButton("");
        Rate3.setActionCommand("3");
        Rate3.setFont(mainFont);
        gbLayout.setConstraints(Rate3, gbc);
        mainPanel.add(Rate3, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Rate4 = new JRadioButton("High - ");
        Rate4.setActionCommand("4");
        Rate4.setFont(mainFont);
        gbLayout.setConstraints(Rate4, gbc);
        mainPanel.add(Rate4, gbc);

        xlocation = xlocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JRadioButton Rate5 = new JRadioButton("No answer ");
        Rate5.setActionCommand("5");
        Rate5.setFont(mainFont);
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
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JPanel fillerLine1 = new JPanel();
        gbLayout.setConstraints(fillerLine1, gbc);
        mainPanel.add(fillerLine1, gbc);

        // Enter optional feedback
        xlocation = 0;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JLabel optionaltext = new JLabel();
        optionaltext.setText("Enter and feedback to our team (optional): ");
        optionaltext.setFont(titleFont);
        gbLayout.setConstraints(optionaltext, gbc);
        mainPanel.add(optionaltext, gbc);

        xlocation = 0;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        gbc.gridwidth = 7;
        JTextArea optionalRating = new JTextArea(2, 35);
        optionalRating.setFont(mainFont);
        gbLayout.setConstraints(optionalRating, gbc);
        mainPanel.add(optionalRating, gbc);

        xlocation = 0;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JPanel fillerLine2 = new JPanel();
        gbLayout.setConstraints(fillerLine2, gbc);
        mainPanel.add(fillerLine2, gbc);

        // Checkbox to send data to server
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JCheckBox proj_Check1 = new JCheckBox("Save on the cloud");
        proj_Check1.setFont(mainFont);
        gbLayout.setConstraints(proj_Check1, gbc);
        mainPanel.add(proj_Check1);

        xlocation = 1;
        ylocation = ylocation + 6;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        gbc.gridwidth = 5;
        JButton bUpdate=new JButton("        Update        ");
        gbLayout.setConstraints(bUpdate, gbc);
        mainPanel.add(bUpdate, gbc);

        bUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String groupRatetxt = Rategroup.getSelection().getActionCommand();
                String optionalFeedback = optionalRating.getText();
                String proj_Check1txt = "0";
                if (proj_Check1.isSelected()) {proj_Check1txt = "1";}

                userData userRecord = new userData();
                userRecord.setUserID("rehab001"); // hardcoded ID for now, will change to randomly generated user ID's
                userRecord.setRate(groupRatetxt);
                userRecord.setOptionalFeedback(optionalFeedback);

                userRecord.setCloudStore(proj_Check1txt);
                SelectRecords dataAccessObject = new SelectRecords();

                int xyx = dataAccessObject.updateUserProfile2(userRecord,1);
                dispose();
            }
        });
        this.pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }
}