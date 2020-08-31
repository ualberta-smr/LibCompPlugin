package smr.cs.ualberta.libcomp.dialog;

import com.intellij.ui.components.JBScrollPane;
import smr.cs.ualberta.libcomp.DatabaseAccess;
import smr.cs.ualberta.libcomp.data.Library;
import javax.swing.*;
import java.awt.Component;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Vector;

/**
 * This file contains the details of the main plugin dialog, its formatting, and displaying the information related to it
 */

public class ReplacementDialog extends JFrame {

    private String LibraryReturned = "None";
    private String mavenReturned = "None";
    private String LibraryName = "None";
    private String full_lib_list = "";
    private int to_library;
    private int libID;
    private int domainID;
    private int columnLength = 0;
    private int startingSort = 4;
    private int offsetBtnCols = 4;
    private int currentLibrary = 4;
    private int selectedLibrary = -1;
    private int rowLength;
    private int rowLengthTotal;

    private double[][] dataDouble;
    private LocalDate[][] dataDate;
    private String[][] dataString;
    private JTable table;
    private String[] columnHeaders;
    private DecimalFormat df;
    private DecimalFormat intf;
    private DecimalFormat daysf;
    private DecimalFormat reposf;
    private DecimalFormat percentf;
    private DecimalFormat changef;
    private DecimalFormat scoref;
    private Color colorBackGround = new Color(210, 210, 210);
    private Color colorForGround = new Color(255, 255, 255);
    private Color colorForGroundDis = new Color(0, 0, 0);
    private Color colorAlternateLine = new Color(230, 230, 230);
    private Color cololrSelectColumn = new Color(204, 229, 255); //Color.lightGray;
    private Color cololrCurrentLibrary = new Color(245, 245, 245);//new Color(210, 210, 210);
    private ArrayList<Library> libraryList;


    String[] columnToolTips = {"Column 1 Chart", // chart
            "Column 2 Sort Descending",
            "Column 3 Sort Ascending",
            "The header row shows your current library, click on an alternative library to replace",
            "Number of times the library is imported in the 1000 top repositories on GitHub", //popularity
            "Average time in days between two consecutive releases of a library", //release frequency
            "Average time in days to close issues in the issue tracking system of a library", //issue closing time
            "Average time in days to get the first response on issues in the issue tracking system of a library", //issue response time
            "Average number of code breaking changes per release", //backwards compatibility
            "Approximation of the percentage of security related issues of a library",//security
            "Approximation of the percentage of performance related issues of a library", //performance
            "The time since a question tagged with this library has been asked on Stack Overflow", // Last Discussed on Stack Overflow
            "The date of the last commit in the library's repository", //Last Modification Date
            "The library's license as listed on GitHub.", // License
            "A rating out of 5 stars calculated for each library, based on the values of the above metrics. Briefly, each metric gets a normalized weight between 0 and 1 depending on its semantics, and then we calculate an overall weighted score across all metrics" //Overall Score
    };


    public String getSelectionLibrary() {
        return full_lib_list;
    }
    public String getLibraryReturned() { return LibraryReturned; }
    public String getMavenReturned(){ return mavenReturned; }
    public String getLibraryname() {
        return LibraryName;
    }
    public int getto_library() {
        return to_library;
    }

    public int getMapping(int original){
        int returnValue = 0;
        switch (original) {
            case 0: { returnValue = 1; break;}
            case 1: { returnValue = 2; break;}
            case 2: { returnValue = 7; break;}
            case 3: { returnValue = 6; break;}
            case 4: { returnValue = 5; break;}
            case 5: { returnValue = 8; break;}
            case 6: { returnValue = 8; break;}
            case 7: { returnValue = 3; break;}
            case 8: { returnValue = 4; break;}
            case 9: { returnValue = -1; break;}
            case 10: { returnValue = -1; break;}
        }
        return returnValue;
    }

    public ReplacementDialog(String domainName, int domainId, int libID) throws HeadlessException, ParseException {

        this.libID = libID;
        this.domainID = domainId;
        Border border = BorderFactory.createLineBorder(Color.black, 1);

        int indexPopularity = 0;
        int indexRelease = 1;
        int indexIssueClosing = 2;
        int indexIssueResponse = 3;
        int indexBackwardCompatibility = 4;
        int indexSecurity = 5;
        int indexPerformance = 6;
        int indexScore = 10;

        int indexDiscussed = 0;
        int indexModification = 1;
        int indexLicense = 0;

        //DATA FOR OUR TABLE
        this.setTitle(domainName);
        DatabaseAccess dataAccessObject = new DatabaseAccess();

        libraryList = dataAccessObject.getMetricsData(domainId, libID);

        columnLength = libraryList.size() + offsetBtnCols;
        columnHeaders = new String[columnLength];
        int current = offsetBtnCols;
        columnHeaders[0] = "";
        columnHeaders[1] = "";
        columnHeaders[2] = "";
        columnHeaders[3] = " \nMetrics \n";
        String replacedString;


        while (current < columnLength) {
            if (current > offsetBtnCols)
                replacedString = " \n" + libraryList.get(current - offsetBtnCols).getName();
            else
                replacedString = libraryList.get(current - offsetBtnCols).getName() + "\n";

            columnHeaders[current] = replacedString;
            current = current + 1;
        }

        rowLength = 8;
        rowLengthTotal = 11;
        Object[][] data = new Object[rowLengthTotal][columnLength];
        dataDouble = new double[rowLength][columnLength];
        dataDate = new LocalDate[2][columnLength]; // only two date Format
        dataString = new String[1][columnLength]; // only one String format


        current = 0;
        df = new DecimalFormat("#");
        intf = new DecimalFormat("# Repos"); // "#/1000 Repos"
        daysf = new DecimalFormat("# Days");
        reposf = new DecimalFormat("# Repos");
        percentf = new DecimalFormat("0.00 %");
        changef = new DecimalFormat("# Breaking Changes");
        scoref = new DecimalFormat("0.00/5 ");

        // datef = new DateFormat("yyy-MM-DD");


        data[indexPopularity][offsetBtnCols - 1] = "Popularity (Repos)";
        data[indexRelease][offsetBtnCols - 1] = "Release Frequency (Days)";
        data[indexIssueClosing][offsetBtnCols - 1] = "Issue Closing Time (Days)";
        data[indexIssueResponse][offsetBtnCols - 1] = "Issue Response Time (Days)";
        data[indexPerformance][offsetBtnCols - 1] = "Performance (Percent)";
        data[indexSecurity][offsetBtnCols - 1] = "Security (Percent)";
        data[indexBackwardCompatibility][offsetBtnCols - 1] = "Backwards Compatibility";

        data[indexScore][offsetBtnCols - 1] = "Overall Score";
        data[rowLength - 1][offsetBtnCols - 1] = "Last Stack Overflow Post";
        data[rowLength][offsetBtnCols - 1] = "Last Modification Date";
        data[rowLength + 1][offsetBtnCols - 1] = "License";


        while (current < columnLength - offsetBtnCols) {

            dataDouble[indexPopularity][current + offsetBtnCols] = (libraryList.get(current).getPopularity());
            dataDouble[indexRelease][current + offsetBtnCols] = (libraryList.get(current).getRelease_frequency());
            dataDouble[indexIssueClosing][current + offsetBtnCols] = (libraryList.get(current).getIssue_closing_time());
            dataDouble[indexIssueResponse][current + offsetBtnCols] = (libraryList.get(current).getIssue_response_time());
            dataDouble[indexBackwardCompatibility][current + offsetBtnCols] = (libraryList.get(current).getBackwards_compatibility());
            dataDouble[indexSecurity][current + offsetBtnCols] = (libraryList.get(current).getSecurity());
            dataDouble[indexPerformance][current + offsetBtnCols] = (libraryList.get(current).getPerformance());
            dataDouble[indexScore-3][current + offsetBtnCols] = (libraryList.get(current).getOverall_score());

            dataDate[indexDiscussed][current + offsetBtnCols] = (libraryList.get(current).getLast_discussed_so());
            dataDate[indexModification][current + offsetBtnCols] = (libraryList.get(current).getLast_modification_date());
            dataString[indexLicense][current + offsetBtnCols] = (libraryList.get(current).getLicense());

            data[indexPopularity][current + offsetBtnCols] = intf.format(libraryList.get(current).getPopularity());
            data[indexRelease][current + offsetBtnCols] = daysf.format(libraryList.get(current).getRelease_frequency());
            data[indexIssueClosing][current + offsetBtnCols] = daysf.format(libraryList.get(current).getIssue_closing_time());
            data[indexIssueResponse][current + offsetBtnCols] = daysf.format(libraryList.get(current).getIssue_response_time());
            data[indexBackwardCompatibility][current + offsetBtnCols] = changef.format(libraryList.get(current).getBackwards_compatibility());
            data[indexSecurity][current + offsetBtnCols] = percentf.format(libraryList.get(current).getSecurity());
            data[indexPerformance][current + offsetBtnCols] = percentf.format(libraryList.get(current).getPerformance());
            data[indexScore][current + offsetBtnCols] = scoref.format(libraryList.get(current).getOverall_score());

            if (libraryList.get(current).getLast_discussed_so().getYear() == 1900)
                data[7][current + offsetBtnCols] = "Never";
            else
                data[7][current + offsetBtnCols] = libraryList.get(current).getLast_discussed_so();

            data[8][current + offsetBtnCols] = libraryList.get(current).getLast_modification_date();



            data[9][current + offsetBtnCols] = libraryList.get(current).getLicense();

            if (full_lib_list.length() < 1)
                full_lib_list = "" + libraryList.get(current).getLibrary_id();
            else
                full_lib_list = full_lib_list + "," + libraryList.get(current).getLibrary_id();
            current = current + 1;
        }

        int frameHeight = 30;
        frameHeight = 110 + (frameHeight * (7 + 4));

        // make sure that columns buttomsn are not editable
        table = new JTable(data, columnHeaders) {
            public boolean isCellEditable(int row, int column) {
                return column < 0;
            }


            //Implement table cell tool tips.
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                int realColumnIndex = convertColumnIndexToModel(colIndex);
                int realRowIndex = convertRowIndexToModel(rowIndex);

                try {
                    tip = columnToolTips[realRowIndex + offsetBtnCols];
                    if (realColumnIndex < offsetBtnCols - 1)
                        tip = columnToolTips[realColumnIndex] + " : " + columnToolTips[realRowIndex + offsetBtnCols];
                } catch (RuntimeException e1) {
                    //catch error
                }
                return tip;
            }

            protected JTableHeader createDefaultTableHeader() {
                return new JTableHeader(columnModel) {
                    public String getToolTipText(MouseEvent e) {
                        java.awt.Point p = e.getPoint();
                        int index = columnModel.getColumnIndexAtX(p.x);
                        int realIndex = columnModel.getColumn(index).getModelIndex();
                        if (realIndex > 3) realIndex = 3;
                        return columnToolTips[realIndex];
                    }
                };
            }
        };

        // Prepare the headers to be multilines
        // Prepare the headers to be multi-lined
        MultiLineHeaderRenderer renderer = new MultiLineHeaderRenderer();
        Enumeration Enum = table.getColumnModel().getColumns();
        while (Enum.hasMoreElements()) {
            ((TableColumn) Enum.nextElement()).setHeaderRenderer(renderer);
        }

        for (int i = 0; i < 3; i++) {

            table.getColumnModel().getColumn(i).setCellRenderer(new ImageRenderer());
            table.getColumnModel().getColumn(i).setMaxWidth(30);
            table.getColumnModel().getColumn(i).setPreferredWidth(30);
            table.getColumnModel().getColumn(i).setHeaderRenderer(new MergeHeaderRenderer());
        }


        table.getColumnModel().getColumn(3).setMaxWidth(225);
        table.getColumnModel().getColumn(3).setPreferredWidth(225);

        table.getTableHeader().setReorderingAllowed(false); //users cannot change the order of columns by dragging them
        table.setGridColor(Color.black);
        table.setColumnSelectionAllowed(true); //to allow selection based on columns
        table.setRowSelectionAllowed(false); //to disable section based on rows
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);


        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value, boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table,
                        value, isSelected, hasFocus, row, column);

                c.setBackground(row % 2 == 0 ? Color.white : colorAlternateLine);


                if (currentLibrary == column) {
                    c.setBackground(cololrCurrentLibrary);
                    c.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                }

                if (isSelected) {
                    if ((column > offsetBtnCols - 1) && (column != currentLibrary)) {
                        c.setBackground(cololrSelectColumn);
                        c.setForeground(colorForGroundDis);
                        c.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                    }
                } else {
                    c.setForeground(Color.black);
                }

                if (column == 3) {
                    setHorizontalAlignment(JLabel.LEFT);
                    c.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
                    c.setForeground(Color.black);
                } else
                    setHorizontalAlignment(JLabel.CENTER);

                c.setForeground(colorForGroundDis);

                return c;
            }
        });

        int ColumnWidth = 1000;
        int x = ColumnWidth - 545;
        int y = frameHeight - 35;
        int widthsize = 100;
        int heightsize = 40;

        JButton bConfirm = new JButton("Replace");
        JButton bCancel = new JButton("Cancel");

        bCancel.setBounds(x, y, widthsize, heightsize);
        bConfirm.setBounds(x + widthsize + 10, y, 410, heightsize);
        bConfirm.setEnabled(false);

        bCancel.setOpaque(false);
        bConfirm.setOpaque(false);

        getContentPane().add(bConfirm);
        getContentPane().add(bCancel);

        bConfirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                int row = table.getSelectedRow();
                int column = table.getSelectedColumn();
                if ((column >= 4)) {
                    LibraryReturned = libraryList.get(column - offsetBtnCols).getPackage();
                    to_library = libraryList.get(column - offsetBtnCols).getLibrary_id();
                    LibraryName = libraryList.get(column - offsetBtnCols).getName();
                    mavenReturned = libraryList.get(column - offsetBtnCols).getMavenlink();
                    dispose(); //force close
                }
            }
        });

        bCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                dispose();
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int rowM = table.getSelectedRow();
                int columnM = table.getSelectedColumn();

                if ((columnM > offsetBtnCols - 1) && (columnM != currentLibrary)) {
                    // Sorting Color locations
                    selectedLibrary = columnM;
                    table.addColumnSelectionInterval(selectedLibrary, selectedLibrary);
                    bConfirm.setEnabled(true);
                    bConfirm.setBackground(cololrSelectColumn);
                    String message = "Replace " + libraryList.get(0).getPackage() + " Package with " + libraryList.get(columnM - offsetBtnCols).getPackage();
                    bConfirm.setText(message);
                }

                if (columnM == currentLibrary) {
                    bConfirm.setText("Replace");
                    bConfirm.setEnabled(false);
                    selectedLibrary = -1;
                }

                if (columnM == 3) {
                    if (selectedLibrary != -1)
                        table.addColumnSelectionInterval(selectedLibrary, selectedLibrary);
                }

                //How to add image:
                if ((columnM == 0)) {
                    String message = " "+ data[rowM][3];
                    int metricValue = -1;
                    Image img = null;
                    try {
                        metricValue = getMapping(rowM);
                        img = dataAccessObject.readCharts(domainID, metricValue);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }

                    if (selectedLibrary != -1)
                        table.addColumnSelectionInterval(selectedLibrary, selectedLibrary);

                    if (metricValue != -1)
                        new Chart(message,img);
                }

                if ((columnM == 1) || (columnM == 2))  {
                    if (rowM != 9) {
                        sortTable(columnM, rowM);
                        if (selectedLibrary != -1)
                            table.addColumnSelectionInterval(selectedLibrary, selectedLibrary);
                    }
                }

            }
        });

        //SCROLLPANE,SET SZE,SET CLOSE OPERATION
        JBScrollPane pane = new JBScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

//        JScrollPane pane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        table.setSize(ColumnWidth, frameHeight);
        pane.setBorder(border);
        pane.setBounds(0, 0, ColumnWidth, frameHeight);
        pane.setOpaque(true);
        getContentPane().add(pane);
        setSize(ColumnWidth, frameHeight + 50);
        //setSize(ColumnWidth, frameHeight + 10);
        // setUndecorated(true);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

    }


    private void sortTable(int typeofSort, int row) {
        int i, j, largest, rowIndex;
        double tempValue;
        LocalDate TempDate;
        String tempHeader;
        Library tempData;
        int offset = 0;
        if (row == 10) {offset = 3; }

        for (i = startingSort; i < columnLength - 1; i++) {
            largest = i;

            for (j = i + 1; j < columnLength; j++) {
                if (typeofSort == 1) {
                    if ((row < 7 ) || (row ==10)){
                        if (dataDouble[row-offset][largest] < dataDouble[row-offset][j]) {
                            largest = j;
                        }
                    } else {
                        if (dataDate[row - 7][largest].isBefore(dataDate[row - 7][j])) {
                            largest = j;
                        }
                    }
                } else {
                    if ((row < 7 ) || (row ==10)) {
                        if (dataDouble[row-offset][largest] > dataDouble[row-offset][j]) {
                            largest = j;
                        }
                    } else {
                        if (dataDate[row - 7][largest].isAfter(dataDate[row - 7][j])) {
                            largest = j;
                        }
                    }
                }
            }

            //Swap headers
            tempHeader = columnHeaders[i];
            columnHeaders[i] = columnHeaders[largest];
            columnHeaders[largest] = tempHeader;

            //Swap data
            tempData = libraryList.get(i - offsetBtnCols);
            libraryList.set(i - offsetBtnCols, libraryList.get(largest - offsetBtnCols));
            libraryList.set(largest - offsetBtnCols, tempData);

            if (largest == currentLibrary)
                currentLibrary = i;
            else if (i == currentLibrary)
                currentLibrary = largest;

            if (largest == selectedLibrary)
                selectedLibrary = i;
            else if (i == selectedLibrary)
                selectedLibrary = largest;



            for (rowIndex = 0; rowIndex < rowLength; rowIndex++) {
                    tempValue = dataDouble[rowIndex][i];
                    dataDouble[rowIndex][i] = dataDouble[rowIndex][largest];
                    dataDouble[rowIndex][largest] = tempValue;
            }

            for (rowIndex = 0; rowIndex < 2; rowIndex++) {
                TempDate = dataDate[rowIndex][i];
                dataDate[rowIndex][i] = dataDate[rowIndex][largest];
                dataDate[rowIndex][largest] = TempDate;
            }


        }

        for (i = startingSort; i < columnLength; i++) {
            table.getColumnModel().getColumn(i).setHeaderValue(columnHeaders[i]);
            table.getTableHeader().repaint();

            for (rowIndex = 0; rowIndex < rowLength + 3; rowIndex++) {

                switch (rowIndex) {
                    case 0: // indPopularity
                        table.getModel().setValueAt(intf.format(dataDouble[rowIndex][i]), rowIndex, i);
                        break;
                    case 1: // indRelease
                        table.getModel().setValueAt(daysf.format(dataDouble[rowIndex][i]), rowIndex, i);
                        break;
                    case 2: //indIssueClosing
                        table.getModel().setValueAt(daysf.format(dataDouble[rowIndex][i]), rowIndex, i);
                        break;
                    case 3: // indIssueResponse
                        table.getModel().setValueAt(daysf.format(dataDouble[rowIndex][i]), rowIndex, i);
                        break;
                    case 4: //indBackward
                        table.getModel().setValueAt(changef.format(dataDouble[rowIndex][i]), rowIndex, i);
                        break;
                    case 5: // indSecurity
                        table.getModel().setValueAt(percentf.format(dataDouble[rowIndex][i]), rowIndex, i);
                        break;
                    case 6: // indPerformance
                        table.getModel().setValueAt(percentf.format(dataDouble[rowIndex][i]), rowIndex, i);
                        break;
                    case 10: // indOverScore
                        table.getModel().setValueAt(scoref.format(dataDouble[rowIndex-3][i]), rowIndex, i);
                        break;
                    case 7: // Date
                        String setValue;
                        if (dataDate[0][i].getYear() == 1900)
                            setValue  = "Never";
                        else
                            setValue = dataDate[0][i].toString();

                        table.getModel().setValueAt(setValue, rowIndex, i);
                        break;
                    case 8: // Date
                        table.getModel().setValueAt(dataDate[1][i], rowIndex, i);
                        break;
                    case 9: // String
                        table.getModel().setValueAt(dataString[0][i], rowIndex, i);
                        break;
                }
            }
        }
    }
}

    class ImageRenderer extends DefaultTableCellRenderer {
        JLabel lbl = new JLabel();

        private Image getImage(String image64){
            byte[] imgArr = Base64.getDecoder().decode(image64);
            Image img= Toolkit.getDefaultToolkit().createImage(imgArr);
            return img;
        }
        public ImageRenderer() {
            setOpaque(true);
            Border border = BorderFactory.createLineBorder(Color.black,0);
            setBorder(border);
        }

        public String getImageText(int row, int column)
        {
            String imageStr = null;
            switch (column) {
                case 0: {
                    // image for charts
                    if ((row == 9)||(row == 10)) {
                        // disable chart
                        if (row % 2 == 0 ) {
                            // white background
                            imageStr = "iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAAKCSURBVEhL7ZbNTxNBGMYfdrv9ghakbYQKBzCSiEajiXoXxJveTPzXPBu8aDB8aEzRWDEVaE8SE6mkSGLwgNBSaPjY7qzvtK+RtrvLrhq88EvezMwzk3k6785H20wC/wGFyxPn1NiRtYUFvBsfh769zYoF5TLE1hY37PFk/KVUQrW/H98LBVaaEAKv02nMZjIQlQqL1ngyNr6twzQM5POrrDRRLEJEIlA6OlAulli0xrXx55WvSF6/gZHzg7g0NoKJmRT31NHpBz2amsU5WmlkZwddvT3cY42rcyyEiafTKTy4N8YKsFEqY+75CwwM9kKrAks7+3h4/y73ukAaH8eTiZdca6SwtGTSNzXfTE6y4h7HVO+urmJu+hVu3bzGSiNBvYoDXUfE52PFPbap3ltbQ3p5GX6a+PboKBAIcE8j2/k8OgcGAE1jxR22K96jTaL6/dDJUGxustpK59CQZ1OJrXH38DD6qByOdkJJJusiDBiGoFJGK0X9APPZReRyOaxXyqxa4+l1ept6jH2RxOULBvoG77D6m43KLj5+mKdZ23Dx6hUk4wnuacXDBVKFUHsQDGioiihrdCq4lJAfFEWpRZtsOODBWE7MNjxn7tMKnk2lMD7zvi54wJNxMwHFwNmohqjf+ps7YW98eIgSPQplhx0tcytz4HqTHMHWeJ0uj8VsFhmKvR/S/K+S04LtbCa9MhrdSH46yyIcZvXfYb+MI6fMeX/+GfYrphBkbtDjXj/qVKfLo9amkMhXS9Z/teUwIds07rjrwdY4pPoQpxQnKNTamVSQiAeQiGkIt4dqY4LhdgSjMUS6YrW2T1UR7j6DUKwLvmOu0dP/1SfGqfEJAfwEUtFclpgn6iUAAAAASUVORK5CYII=";
                        }
                        else
                        {
                            // alternate line
                            imageStr = "iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAAJuSURBVEhL7Zbda9NQGMafNEnbNEuWmpW2ai82LyaKitJtIIjChn+kf4MXguKNG6WoRZ0ONgviqqvOtUPYaj+s61cST9IjpR8nS3tRb/qDw+F9TsKTvHnfk8MVi0UL/wEfnafOzNiV1uEhcqkUFkIhqgwTl2VEAwEasRnLOFcuo5NI4PjggCrDbKXT2MxkEFVVqoxmLGPjqAjLMLC/n6dKP3G/H6aiwDc3h+qvMlVH49m4Um/i4p0VrF9ZwvWH63izk6UrPR493cSleh1KrQYtHqPqaDz1cTQaw+NnL3Bv5QZVAEGS8fLJcywuxSF2gI+1Bh6s3qSr5+PJOP12F/fXbtGoR+P0FPlSCVylgqvJJFW94ZpqpdHAlw97WFu9TZV+gu0Omu02FEGgineYxlqrhVe5HNpcB4u6RtUBYlHcJVWe3NiggneYxmekSHhSpW3SkyZJKYs/pIqPT05o5B2mcTMcxmUyX1Pn8ZPjuqIHvh/9QKFQgKXKVBmN6zfWlpchJGz7Lp+zW9jd+0SjYQRVQb1SRYUUG86aVB2Nq/EgJh9DMCDSaJhOtQafz+cM7pwsjWXMcf2dVyjV8fp9Fqmdr04szivO7IWxjAcJ+AyyJ4tQ/SZVvMM0jus6JEmCTCqbCUmnnYNJjjBM42I+j3fb28iQoYku5hPCNLZIf4pkR/LbfxyX/++ksL+x1Uug9y72DvuNyTCJuWGa5Bm6D2EYphM3fn9zYtO0YNnrZNjYl5l2TK77dw8LprHEC84RJ0IGT3syshBARBcRkiUnDoZkBFUdiqY7scDzCF0IQyJ7uyCy+91mdq6eGjPjKQH8BagdxyckPP6zAAAAAElFTkSuQmCC";
                        }
                    } else
                    {
                        // enable chart
                        if (row % 2 == 0 ) {
                            imageStr = "iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAAKCSURBVEhL7ZZLaBNRFIb/mWmStqmQIbH4QrBWBHFjSX2QiIq6E9SFD6S+cKGo2I0rs3Ah6KKhCCKCiq6y0Y26qagFsUGjLgq1hSqCdZNYacyDNm0mM3O9nZ6iJrkhQzVu8sFlzvmHmf+e+5g7EuPgPyDTtebUjSsSv3kL7zZvQSGZJKUMuWmY6TQlYmwZj925DdPpRDISIaWU18GtVjOncqSUx5axvrgVciaDgeQUKUV8HweT+EvdbkwOfyBRwOx2qoa+lzH2LPqesW8J9jExwc6GeujOHIbJ2KlQmH05c5oN7t1PqpiqjS9euUHRHPFkml29HGafDh9io3fvs5OXwnSnOqoyPldU3TyJ3l72JhBksY2bSKmeinM8HYuh714Ex47sI+VP5OUroKVSaF7dRooNqAMl5EZH2at169nbDR2klCc7EKXIHsKK8/EE5KZGaAUdRkq8LxcFAxTZQ2js2bEdS3bvQtuF81BUD6l8hIxpikrJ6hqO9z/Aif6HGJvMkFoeW6fTjxd8k5pAc3s3Gtuuk/qL4cwEup5GIEsSeoJ7sHOZeO5tfUAkhT/gauHrgnegDIoswykrvDVAkSq/2pYxeCW/X689+Yy13c/RGYpauR3sGRdR0A14WxyQTD7+NhEb86nPDg1hcmSEhFIkXvls8fMDYQeh8fijxxjqOorBAwcxM/aV1L+HuGJFgUNV4fCo+Bc/ZQua44UgNGaGAVPTeMvPflZJ44eKOcP3sm7lOs/zBROaPre4+MmIPH9OM3QrroTQ2OXzQe3ogOr3Q2l0WZrDF4DDuw2Ku93KV7W64W/3onON18rdDQ50Ll0JP2+qq8nSRNT/q2tG3bhGAD8BeJFlrYc47JcAAAAASUVORK5CYII=";
                        }
                        else
                        {
                            imageStr = "iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAAKSSURBVEhL7ZZNSBRhHMafmXF3dGfNHXZdd7UWMqNugaxeKupg0EGizDrYx6UIIvHQqejSISEvHaQufYCHBC/aBkUR1MXoYlCaCQWKGuxStoqLyzqzszO9O/4jtt3ZZrXssj94mfd55uPh/34MLxeNRg38B3i6bjrl4KIYIxF8OdYBn9NJTj7BGg/qqqpIWVNS8Oy9u9BZaHxwkJx83uzbb7a6LTXkFKakYK3WD355GaPxJDm5BAUeBsc+KklYmfxAbmFsB49/nodyvRetL55jz8ULuHEnt+q6QBDn+x+ioTUMV7AeyVCI7hTG9j6+9WAYl88dJ8UQXRjov4/OT2PQ2w6jbyaO3u4uuvlnbAX3suquXTpF6hfc0BBmh0fApdMIRR6Ra4+iQ+2Zm8P7Z69wtusoObnwDVuhLi3BtaORHPtYVuxJJPC2oxOi6MS2p0/Izcc9PYOVdQRbVqxEY+CrKqGmNfgrrfflekKzWAandu9C4FAbGnu68W01RS4Q8Huol49U68WVqVFcnXoNpdpFbmGKzrGrpwdcezsptpA/1mPppQtyqo+cXOaTCYx/j2EiHsN0Ik5uYWzv4yycwF4Q3TCyf4kCCDwPJy+wVgGBK/7pkoLBUSBdB8aSOHhzEkduT5u6FEoL/o20loHX7QCn6+TYxzI4GAhAWlhA9eIiOflwrPJs8T8HohQsg79GHmPi9Bm8O3ESsqKS+/ewHmpBgEOW4fDI+BeHsg3N8UawDDYyGeiqyprCts9azUbGgKGvArpmao1pJa1D1dYWl86eU9h7akYz+8WwDBZ9PsjNzZDDYQiVouk5fHvh8B6AIDWZertfQrjJi5adXlNLFQ60BEMIsyaLxY8/5XP1plEO3iSAH/c+xXNLB2G5AAAAAElFTkSuQmCC";
                        }
                    }
                }
                break;
                case 1: {
                    // image for descending sort

                    if (row == 9) {
                        // disabled
                        imageStr = "iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAAETSURBVEhL7ZbRCoIwFIZP3vgAITToJiToAYIQ9r6+SD2GV4GjNKLbAovFHzXM7UyGXeR343+E+e3oNpyUZXmnHxDhOjijeDBGcXCmYopk0ineiR0VRUEHccAdP/JZ/hx/UifcMbEeIHrgJ2maIn3nKq603+5RER3lkTKVoTKxii/iQvW2RvVmLucUqxhVe4IvbBN1HpldD3URyYgWaoGqjXNxLeUSyQ+bVOMUN6pBCgtrO63lGomHaxFqWOKzOiO5WckVkh2WWMPpQnNTNyQ7bDGHjdwgufESu7quVXvPdxGsY+6neOEt9hV0EaTjPpOxipMkQQqPVVxVFZLJZ4d9X33vV62Flfw+MQ7jD/1g/JuY6AErDE9fBfvANQAAAABJRU5ErkJggg==";
                    } else {
                        // enabled
                        if (row % 2 == 0 ) {
                            imageStr = "iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAAIcSURBVEhL7ZU/SBtRHMe/l6vaOysOsVOJQ6EiVurSwUEKVdGxRJCCQx0cHESkgtpSKBpUKGmnahEHRZBuiuLiImIKGcRBCf5Z200SazzP+Cea5++FZ2qkl9wLEh3yOcjd7/feL99883vvncII3AEOcc86OeGscf+E/e3vxZM8ax4vIptbIvo/lsIXuo5uRcFcnRvGRkBkU2Ca8LV34zPVrHpHoD+vEAMW8H1shQcO1g+NfaRpo4UutjM2KUb+sbu0wn6+eMU+0Jx+PGS9dDc3t8WoNSkPkPWvI5jt6UIhnDhHFKcwoCKG2o4eOMvLsNjZiz3sQ0MRHqCA5pziaWUVWgK/xDdYk/bkGlY0KNCpJ2o8jtEVRYQ+L5BPP0klSQ6jK4wQPL//IL/UFc+lIu2qfvNjlGT2RcQLHOTtEbksTohyojjBy+oGW6IcW2f1F6WY/KlJQtfhbg/I7WAwBLXEKbKpsbWP3VPjOL7m+iZnNFpd32RblGP77fRNeUxLh/tOds3dGuR2OHIEaLrIpsf2ydU0M5nU6yvOcIQa9zspUY7U+/i75oJxYpLnvHjM3R6S2yEWo0iJ5+xi2zGneX6K/IVFBPrrTbxu7aAnOVGOlGPOmPMZ9v4Gqdd5tKRCGJArTyDlmPN2YZp8HtCuNdDY2SeyGcAdyzLxpJJ9yqw0QUbVQZ+fLbd1iSgzpHt8W0j3+LbICWeNnHCWAC4BfDjfXXRQ+44AAAAASUVORK5CYII=";
                        }
                        else
                        {
                            imageStr = "iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAAIeSURBVEhL7ZZfSFNRHMe/utS1eZlOIVpTUEKSFB/sQSECQ4sEsQnSi9SDb8oYPmiEKOKL0F77AyvQ4VIQFMqXCHrxTR8F8WEPoeCWorkNs7W//c76mYTb7rk+rMB9zsN2fufefc73nnMuK/D5fEn8Awr5M+fkxTnj/xN/mXjO37Sz88YD07dD7qUnozhuMMBpsWC1bwDGvX2uZuaqosA74cRrumfN+QJBczmPpCfrOX5rsSKBEvzED1iNVbB5V3nkFN2mF5+HxrC+vgI9tWOEMb6xiVC5ia9IT1bx7rtFLA07YEQFYojSBELQ0VTuDg6j4kYdPtpHcIBDXIaCSzTBGF1R29CEtk8L/AuZUX1zTVtqUQADrYku1U9Qi1KuBOIopinpSClIUgtgH5Nb2zgo+l3Lhuqu7n71kjSnG6WQWglKKaXpj1QQpUd8q+WelFSgKjY/fEAahfLFuHIWkfYYR7Atz3FFHalzbHO7aHtlPh4RGm1p78FeNMIVdaTESscdlKEybWqRNozv6P4wyxU5pMSCnsXpv9b6hAhJb9sewx8IckUOabG+tRlX9NbUsTpBpBVnvHNphivySIsFve/dlC/APZD0CG1PBuH3f+WKPJrEycZ6VJuvU+pIKm2CVvfm1CiPakOTWPBo2UM5g6QM4b79KVe1o1kcrqlG3bWGVOqaZw6uakezWNA170J7//mlgvy/zJyRF+eMiyYGfgHhaqbMagVasAAAAABJRU5ErkJggg==";
                        }
                    }
                }
                break;
                case 2: {
                    // image for ascending sort
                    if (row == 9) {
                        // Disable button
                        imageStr = "iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAAEPSURBVEhL7ZbLCsIwEEVHQVduxEYY0FU/w35+xAd+gCDFXV2ULvwBlZSrqORtWxd6oOROCTmZJov2iqK40hfoY+ycv7gzosVrXiPFES0WUlCe56jCiRI/C01yIQSSntbOuCxLJD3B4k8+7zONdByzmSBxU90qvMUJJ0h6QjflLd7IDVIzeIkHPECyE9K1l3gv90huxjxGsuMUh57dTu6Q7HifcQh9di9rnXHkI1IYB3lAMmMVX+QF6ZU0TR/PLJvh7StnPiPpMf5zrXhFUzlFRTTP5jQ8DVHpeb8PamMmjB0r6XaxfXTmkiruc0fZqK6XvKxHHdqOJzyh6lShagdtx21LFe573xJ/cWf8mpjoBs/LTt5t2PUaAAAAAElFTkSuQmCC";
                    } else {

                        if (row % 2 == 0 )
                        {   // white background
                            imageStr = "iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAAFySURBVEhL7da/SwJhGAfwx7IwJyFcGl1s6U9oVYS7JsHJv8AtwjEaA3G41qBFWooI7yUImnOpEBpzSNyCBpd+UMf79GhPlPTe3ftedC5+vsh9H0G+4HQJJDAFc/yM3Ww4NpGHTyl/EXl4yz2AFrb4MhdpeJcysiOOxk8Vz/O4qUUa3ncvuQHsUVSSySQ3NePhTcpPjnvBzYzxcNvtcfu2TTFlNFzFKrdJh26Xmz7t4XdKRwz5+q2GNW56tIdttLmpnYsBNz1aw4+Unljgy18FK9zCaQ0Xscgt2LV4hleKjtDhO8pQrPAVzkKLW7DQ4QIWuOm5F0vwQAkTONyhzIs1viZZdg48+5avSSUscfMX+OqTwQwsi3W+AJ7sK2gkGlClfOlT6liHG/HC33w622jAKsXXaFjlhJJrW+NPWZaxTwnjSAdTMjX+TVZm+Vs13+G0TGNTNvky06XkZR6PKX6Uf/UbZZHyn2avt7GZDcdmSsMAHxxYO8ZcyWYDAAAAAElFTkSuQmCC";
                        }
                        else
                        {
                            imageStr = "iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAAFZSURBVEhL7ZatT8NAGIffQRBozHtIMjNBphDLQCyZW/sHTEzMoCfINAl1U6glkJBMYKiiZ0ioBLYEEhLsxBJENzmFIuHj9iOk2dreVRRBn4r3uUuTJ6fuCkEQfNAfsIaZOXk4M1KHR2IES0fq8JF3QT77WJmTKjwQAzWP5ZWaaUgVPvfuYUSucGFmGId7ogdbcOrdwswwDl97Y9gvfdGH6WMU7nIXFubSe4bpYxR+kHPYMg47MD20w21uw1ZzI19hemiHx3IDFk2HO7BktMIWW7B4nuQbLBmt8Fxuw5JpcQsWT2K4whWYHhO5CYsnNjwRE1qXu1iFsewderdfsArT4AYsmtinT4lLtCUPlN/tnanpC5/q07ryVew/Hqr5838UkSceiuFS9Ju4qMsuBfbixipzWc0oIk9c5CKdfH3NWRM7ZtS4Rk7Boeq0ip0w+SszM/JwZvy3MNEnoeFUYD9610kAAAAASUVORK5CYII=";
                        }
                    }
                }
                break;
            }
        return imageStr;
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {

                        lbl.setIcon(new ImageIcon(getImage(getImageText(row,column))));
            return lbl;
        }
    }

    class MergeHeaderRenderer extends JLabel implements TableCellRenderer {

        public MergeHeaderRenderer() {
            setOpaque(true);
            setBackground(new Color(128,128,128));
            setForeground(new Color(255,255,255));
            Border border = BorderFactory.createLineBorder(Color.black,0);
            setBorder(border);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            return this;
        }
    }

    class MultiLineHeaderRenderer extends JList implements TableCellRenderer {
        public MultiLineHeaderRenderer() {
            setOpaque(true);
            setBackground(new Color(128,128,128));
            setForeground(new Color(255,255,255));
            Border border = BorderFactory.createLineBorder(Color.black,1);
            setBorder(border);
            ListCellRenderer renderer = getCellRenderer();
            ((JLabel)renderer).setHorizontalAlignment(JLabel.CENTER);
            setCellRenderer(renderer);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
            String str = (value == null) ? "" : value.toString();
            BufferedReader br = new BufferedReader(new StringReader(str));
            String line;
            Vector v = new Vector();
            try {
                while ((line = br.readLine()) != null){
                    v.addElement(line);
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            setListData(v);
            return this;
        }
    }

class Chart extends JFrame {

    public Chart(String title, Image img) throws HeadlessException {
        super(title);

        pack();
        Container container = getContentPane();
        container.setLayout(new FlowLayout());
        JPanel r = new JPanel(new BorderLayout());

        JLabel jLabel = new JLabel();
      //  jLabel.setIcon(new ImageIcon(new javax.swing.ImageIcon(img).getImage().getScaledInstance(650, 300, Image.SCALE_SMOOTH)));

        jLabel.setIcon(new ImageIcon(img));
        r.add(jLabel);

        JButton bConfirm=new JButton("Okay");
        r.add(bConfirm, BorderLayout.SOUTH);
        container.add(r);
        setVisible(true);
        int width          = img.getWidth(null);
        int height         = img.getHeight(null);
        setSize(width+50, height+75);

        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        bConfirm.setEnabled(true);


        bConfirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                dispose();
            }
        });
    }
}


