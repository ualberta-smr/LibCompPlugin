package org.intellij.sdk.editor;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.awt.GridBagConstraints.LINE_START;


/**
 * The DialogSendFeedback class contains the details of the SendFeedback dialog
 * This dialog is accessable via the Tools menu
 */

public class DialogSendFeedback extends JFrame {
    DialogSendFeedback(String title) {
        super(title);
    }

    private JPanel mainPanel;
    private DataUser userRecord;
    private GridBagConstraints gbc = new GridBagConstraints();
    private GridBagLayout gbLayout = new GridBagLayout();
    private Font mainFont = new Font("SansSerif", Font.PLAIN, 12);
    private Font titleFont = new Font("SansSerif", Font.BOLD | Font.ITALIC, 12);


    void init() {
        mainPanel = new JPanel();
        mainPanel.setLayout(gbLayout);
        this.setTitle("Send Current LibComp Interaction Data ");

        int xlocation = 0;
        int ylocation = 1;

        userRecord = new DataUser();
        DatabaseAccess dataAccessObject = new DatabaseAccess();
        userRecord = dataAccessObject.ReadUserProfile();

        TitledBorder title;
        title = BorderFactory.createTitledBorder("Please fill this form to the best of your ability: ");
        mainPanel.setBorder(title);

        this.setContentPane(mainPanel);
        gbc.anchor = LINE_START;

        xlocation = 0;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JPanel fillerLineLine00 = new JPanel();
        gbLayout.setConstraints(fillerLineLine00, gbc);
        mainPanel.add(fillerLineLine00, gbc);

        xlocation = xlocation + 1;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JPanel fillerLineLine01 = new JPanel();
        gbLayout.setConstraints(fillerLineLine01, gbc);
        mainPanel.add(fillerLineLine01, gbc);

        xlocation = 0;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JLabel lineltext1 = new JLabel();
        JLabel lineltext2 = new JLabel();
        JLabel lineltext3 = new JLabel();
        JLabel lineltext4 = new JLabel();
        JLabel lineltext5 = new JLabel();
        JLabel lineltext6 = new JLabel();

        lineltext1.setText(" LibComp stores the interactions you have with the plugin. For each invocation of LibComp, this includes the project name,");
        lineltext2.setText(" class name, location of trigger, import list, time of trigger, information of the librarues being compared, and information");
        lineltext3.setText(" of any replaced libraries. This data is stored locally on your computer unless you explicitly send it to us using this dialog.");
        lineltext4.setText(" If you have filled a user profile, then this data will also be associated with this user profile. We do not collect any ");
        lineltext5.setText(" other data. While the information we collect is not sensitive, please do NOT send us this data if information such as ");
        lineltext6.setText(" class or project names in your code are confidential.  ");

        lineltext1.setFont(mainFont);
        lineltext2.setFont(mainFont);
        lineltext3.setFont(mainFont);
        lineltext4.setFont(mainFont);
        lineltext5.setFont(mainFont);
        lineltext6.setFont(mainFont);

        gbc.gridwidth = 7;
        ylocation = ylocation + 1;
        gbc.gridy = ylocation;
        gbLayout.setConstraints(lineltext1, gbc);
        mainPanel.add(lineltext1, gbc);

        ylocation = ylocation + 1;
        gbc.gridy = ylocation;
        gbLayout.setConstraints(lineltext2, gbc);
        mainPanel.add(lineltext2, gbc);

        ylocation = ylocation + 1;
        gbc.gridy = ylocation;
        gbLayout.setConstraints(lineltext3, gbc);
        mainPanel.add(lineltext3, gbc);

        ylocation = ylocation + 1;
        gbc.gridy = ylocation;
        gbLayout.setConstraints(lineltext4, gbc);
        mainPanel.add(lineltext4, gbc);

        ylocation = ylocation + 1;
        gbc.gridy = ylocation;
        gbLayout.setConstraints(lineltext5, gbc);
        mainPanel.add(lineltext5, gbc);

        ylocation = ylocation + 1;
        gbc.gridy = ylocation;
        gbLayout.setConstraints(lineltext6, gbc);
        mainPanel.add(lineltext6, gbc);


        gbc.gridwidth = 1;
        xlocation = 0;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JPanel fillerLine10 = new JPanel();
        gbLayout.setConstraints(fillerLine10, gbc);
        mainPanel.add(fillerLine10, gbc);


        // Checkbox to send data to server
        gbc.gridwidth = 7;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JCheckBox proj_Check1 = new JCheckBox("<html>I have carefully read the above information and confirm my agreement to share the collected LibComp interaction data.</html>");

        proj_Check1.setFont(mainFont);
        if (userRecord.getCloudStore().equals("1"))
            proj_Check1.setSelected(true);
        gbLayout.setConstraints(proj_Check1, gbc);
        mainPanel.add(proj_Check1);

        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JPanel fillerLineLine02 = new JPanel();
        gbLayout.setConstraints(fillerLineLine02, gbc);
        mainPanel.add(fillerLineLine02, gbc);


        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JCheckBox proj_Check2 = new JCheckBox("<html>Always send all LibComp interaction data. By checking this box, LibComp will automatically send your interaction data <br> without  you having to come back to this dialog. You can always come back and change this setting.</html>");

        proj_Check2.setFont(mainFont);
        if (userRecord.getSendAllCloud().equals("1"))
            proj_Check2.setSelected(true);
        gbLayout.setConstraints(proj_Check2, gbc);
        mainPanel.add(proj_Check2);

        gbc.gridwidth = 1;
        xlocation = 0;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JPanel fillerLine11 = new JPanel();
        gbLayout.setConstraints(fillerLine11, gbc);
        mainPanel.add(fillerLine11, gbc);

        // Enter optional feedback

        xlocation = 0;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JLabel optionaltext2 = new JLabel();
        optionaltext2.setText("Optional Feedback ");
        optionaltext2.setFont(titleFont);
        gbLayout.setConstraints(optionaltext2, gbc);
        mainPanel.add(optionaltext2, gbc);


        xlocation = 0;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JPanel fillerLine2 = new JPanel();
        gbLayout.setConstraints(fillerLine2, gbc);
        mainPanel.add(fillerLine2, gbc);


        xlocation = 0;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JLabel optionaltext01 = new JLabel();
        optionaltext01.setText("This information will help us understand your experience with LibComp. ");
        optionaltext01.setFont(mainFont);
        gbLayout.setConstraints(optionaltext01, gbc);
        mainPanel.add(optionaltext01, gbc);


        xlocation = 0;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JPanel fillerLine3 = new JPanel();
        gbLayout.setConstraints(fillerLine3, gbc);
        mainPanel.add(fillerLine3, gbc);


        ylocation = ylocation +1;
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

        if (userRecord.getRate().equals("0"))
            Rate0.setSelected(true);
        if (userRecord.getRate().equals("1"))
            Rate1.setSelected(true);
        if (userRecord.getRate().equals("2"))
            Rate2.setSelected(true);
        if (userRecord.getRate().equals("3"))
            Rate3.setSelected(true);
        if (userRecord.getRate().equals("4"))
            Rate4.setSelected(true);
        if (userRecord.getRate().equals("5"))
            Rate5.setSelected(true);

        xlocation = 0;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JPanel fillerLine4 = new JPanel();
        gbLayout.setConstraints(fillerLine4, gbc);
        mainPanel.add(fillerLine4, gbc);

        xlocation = 0;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JLabel optionaltext02 = new JLabel();
        optionaltext02.setText("Enter any feedback for our team (optional): ");
        optionaltext02.setFont(titleFont);
        gbLayout.setConstraints(optionaltext02, gbc);
        mainPanel.add(optionaltext02, gbc);

        xlocation = 0;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        gbc.gridwidth = 7;
        JTextArea optionalRating = new JTextArea(2, 65);
        optionalRating.setFont(mainFont);
        optionalRating.setText(userRecord.getOptionalFeedback());
        gbLayout.setConstraints(optionalRating, gbc);
        mainPanel.add(optionalRating, gbc);

        gbc.gridwidth = 1;
        ylocation = ylocation + 1;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        JPanel fillerLineLine04 = new JPanel();
        gbLayout.setConstraints(fillerLineLine04, gbc);
        mainPanel.add(fillerLineLine04, gbc);

        // Rate the Plugin
        xlocation = 0;
        ylocation = ylocation + 6;
        gbc.gridx = xlocation;
        gbc.gridy = ylocation;
        gbc.gridwidth = 5;
        JButton bUpdate=new JButton("   Send current LibComp interaction data     ");
        gbLayout.setConstraints(bUpdate, gbc);
        mainPanel.add(bUpdate, gbc);


        proj_Check1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if  (proj_Check1.isSelected())
                {
                    bUpdate.setEnabled(true);
                } else
                {
                    bUpdate.setEnabled(false);
                }
            }
        });


        bUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String groupRatetxt = Rategroup.getSelection().getActionCommand();
                String optionalFeedback = optionalRating.getText();
                String proj_Check1txt = "0";
                String proj_Check2txt = "0";

                if (proj_Check1.isSelected()) {proj_Check1txt = "1";}
                if (proj_Check2.isSelected()) {proj_Check2txt = "1";}

                DataUser userRecordFinal = new DataUser();
                userRecordFinal.setUserID(userRecord.getUserID());
                userRecordFinal.setRate(groupRatetxt);
                userRecordFinal.setOptionalFeedback(optionalFeedback);

                userRecordFinal.setCloudStore(proj_Check1txt);
                userRecordFinal.setSendAllCloud(proj_Check2txt);
                DatabaseAccess dataAccessObject = new DatabaseAccess();

                int xyx = dataAccessObject.updateUserProfile2(userRecordFinal);
                dispose();
            }
        });

        this.pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }
}