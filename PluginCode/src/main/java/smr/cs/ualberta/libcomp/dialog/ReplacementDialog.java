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
    private String LibraryName = "None";
    private String full_lib_list = "";
    private int to_library;
    private int libID;
    private int domainID;
    private int columnLength = 0;
    private int startingSort = 4;
    private int offsetBtnCols = 4;
    private int currentLibrary = 4;
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
    private Color cololrSelectColumn = Color.lightGray;
    private Color cololrCurrentLibrary = new Color(245, 245, 245);//new Color(210, 210, 210);
    private ArrayList<Library> libraryList;


    String[] columnToolTips = {"Column 1 Chart", // chart
            "Column 2 Sort Descending",
            "Column 3 Sort Ascending",
            "The header row shows your current library, click on an alternative library to replace",
            "Number of times the library is imported per 1000 repositories", //popularity
            "Average time in days between two consecutive releases of a library", //release frequency
            "Average time in days to close issues in the issue tracking system of a library", //issue closing time
            "Average time in days to get the first response on issues in the issue tracking system of a library", //issue response time
            "Average number of code breaking changes per release", //backwards compatibility
            "Approximation of the percentage of security related issues of a library",//security
            "Approximation of the percentage of performance related issues of a library", //performance
            "A rating out of 5 stars calculated for each library, based on the values of the above metrics. Briefly, each metric gets a normalized weight between 0 and 1 depending on its semantics, and then we calculate an overall weighted score across all metrics", //Overall Score
            "The time since a question tagged with this library has been asked on Stack Overflow", // Last Discussed on Stack Overflow
            "The date of the last commit in the library's repository", //Last Modification Date
            "The library's license as listed on GitHub." // License
    };


    public String getSelectionLibrary() {
        return full_lib_list;
    }

    public String getLibraryReturned() {
        return LibraryReturned;
    }

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
            case 2: { returnValue = 3; break;}
            case 3: { returnValue = 4; break;}
            case 4: { returnValue = 6; break;}
            case 5: { returnValue = 9; break;}
            case 6: { returnValue = 10; break;}
            case 7: { returnValue = -1; break;}
            case 8: { returnValue = 7; break;}
            case 9: { returnValue = 8; break;}
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
        int indexPerformance = 4;
        int indexSecurity = 5;
        int indexBackwardCompatibility = 6;
        int indexDiscussed = 7;
        int indexModification = 8;
        int indexLicense = 9;
        int indexScore = 10;

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
        intf = new DecimalFormat("# Repos");
        daysf = new DecimalFormat("# Days");
        reposf = new DecimalFormat("# Repos");
        percentf = new DecimalFormat("0.00 %");
        changef = new DecimalFormat("# Changes");
        scoref = new DecimalFormat("# ");

        // datef = new DateFormat("yyy-MM-DD");

        indexPopularity = 0;
        indexRelease = 1;
        indexIssueClosing = 2;
        indexIssueResponse = 3;
        indexBackwardCompatibility = 4;
        indexSecurity = 5;
        indexPerformance = 6;
        indexScore = 7;

        indexDiscussed = 0;
        indexModification = 1;
        indexLicense = 0;


        data[indexPopularity][offsetBtnCols - 1] = "Popularity (Repos)";
        data[indexRelease][offsetBtnCols - 1] = "Release Frequency (Days)";
        data[indexIssueClosing][offsetBtnCols - 1] = "Issue Closing Time (Days)";
        data[indexIssueResponse][offsetBtnCols - 1] = "Issue Response Time (Days)";
        data[indexPerformance][offsetBtnCols - 1] = "Performance (Percent)";
        data[indexSecurity][offsetBtnCols - 1] = "Security (Percent)";
        data[indexBackwardCompatibility][offsetBtnCols - 1] = "Backwards Compatibility";

        data[indexScore][offsetBtnCols - 1] = "Overall Score";
        data[rowLength + indexDiscussed][offsetBtnCols - 1] = "Stack Overflow";
        data[rowLength + indexModification][offsetBtnCols - 1] = "Modification Date";
        data[rowLengthTotal - 1][offsetBtnCols - 1] = "License";


        while (current < columnLength - offsetBtnCols) {

            dataDouble[indexPopularity][current + offsetBtnCols] = (libraryList.get(current).getPopularity());
            dataDouble[indexRelease][current + offsetBtnCols] = (libraryList.get(current).getRelease_frequency());
            dataDouble[indexIssueClosing][current + offsetBtnCols] = (libraryList.get(current).getIssue_closing_time());
            dataDouble[indexIssueResponse][current + offsetBtnCols] = (libraryList.get(current).getIssue_response_time());
            dataDouble[indexBackwardCompatibility][current + offsetBtnCols] = (libraryList.get(current).getBackwards_compatibility());
            dataDouble[indexSecurity][current + offsetBtnCols] = (libraryList.get(current).getSecurity());
            dataDouble[indexPerformance][current + offsetBtnCols] = (libraryList.get(current).getPerformance());
            dataDouble[indexScore][current + offsetBtnCols] = (libraryList.get(current).getOverall_score());

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

            data[8][current + offsetBtnCols] = libraryList.get(current).getLast_discussed_so();
            data[9][current + offsetBtnCols] = libraryList.get(current).getLast_modification_date();
            data[10][current + offsetBtnCols] = libraryList.get(current).getLicense();


            if (full_lib_list.length() < 1)
                full_lib_list = "" + libraryList.get(current).getLibrary_id();
            else
                full_lib_list = full_lib_list + "," + libraryList.get(current).getLibrary_id();
            current = current + 1;
        }


        // make sure that only the first 3 columns buttomsn are editable
        int frameHeight = 30;
        frameHeight = 110 + (frameHeight * (7 + 4));

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
        bConfirm.setBounds(x + widthsize + 10, y, 420, heightsize);
        bConfirm.setEnabled(false);

        bCancel.setOpaque(true);
        //   bConfirm.setBackground(colorBackGround);
        //   bConfirm.setForeground(colorForGround);
        //   bCancel.setBackground(colorBackGround);
        //   bCancel.setForeground(colorForGroundDis);

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
                    bConfirm.setEnabled(true);
                    //    bConfirm.setForeground(colorForGroundDis);

                    String message = "Replace " + libraryList.get(0).getPackage() + " Package with " + libraryList.get(columnM - offsetBtnCols).getPackage();
                    bConfirm.setText(message);
                }
                if ((columnM < 4) || (columnM == currentLibrary)) {
                    bConfirm.setText("Replace");
                    bConfirm.setEnabled(false);
                    //    bConfirm.setForeground(colorForGroundDis);
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
                    if (metricValue != -1)
                        new Chart(message,img);
                }
                if (columnM == 1) {
                    int typeofSort = 1;
                    if (rowM < 10)
                        sortTable(typeofSort, rowM);
                }
                if (columnM == 2) {

                    int typeofSort = 2;
                    if (rowM < 10)
                        sortTable(typeofSort, rowM);
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

        for (i = startingSort; i < columnLength - 1; i++) {
            largest = i;

            for (j = i + 1; j < columnLength; j++) {
                if (typeofSort == 1) {
                    if (row < 8) {
                        if (dataDouble[row][largest] < dataDouble[row][j]) {
                            largest = j;
                        }
                    } else {
                        if (dataDate[row - rowLength][largest].isBefore(dataDate[row - rowLength][j])) {
                            largest = j;
                        }
                    }
                } else {
                    if (row < 8) {
                        if (dataDouble[row][largest] > dataDouble[row][j]) {
                            largest = j;
                        }
                    } else {
                        if (dataDate[row - rowLength][largest].isAfter(dataDate[row - rowLength][j])) {
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
                    case 7: // indOverScore
                        table.getModel().setValueAt(scoref.format(dataDouble[rowIndex][i]), rowIndex, i);
                        break;
                    case 8: // Date
                        table.getModel().setValueAt(dataDate[0][i], rowIndex, i);
                        break;
                    case 9: // Date
                        table.getModel().setValueAt(dataDate[1][i], rowIndex, i);
                        break;
                    case 10: // Date
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

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {

            Icon warnIcon = null;

            switch (column) {
                case 0:  {
                    // image for charts
                    String imageStr = "iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAIAAAC0Ujn1AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAAMfSURBVEhL7ZbbK6RhHMeF/0JyLcWluHElbk1qL4glNazZFdY4JELtjXJh4mIaUpQxjrHjMBI5zTjkELkhx2Scs8i6YPcz83tfzdjNTju2dmu/F9P7/N7f83l/8xy+zxPwzT89PDxcXl5eqbq9vZ2fn3c4HHNzc/6iHx8f7+7uvqriS+vr62tra/z6i765uenq6vqsanR0tKioqKCgoLCw0F/04eFhYmLiG1WZmZkBT1JSfldOp1Oj0UAU6XS6wMDAvx59dHSUlJT0VlVubq5PaFZVb29vTExMXFwc02K325UXHqLq1NTUd6r0en1QUNCv0cfHx3V1dZIWHR3d19envPAQORT7QVVZWZlP6JOTk/r6ekmj8IGBAeWFh0Cnp6e/V1VaWuoT+vT01GAwSBpoq9WqvPAQOf6iExIS2BFNTU1tbW1ZWVlErq+vt7a2FhYWmDq2iai6utqnafwR3d7e3tPTA4II+3t3d3dpaYnZ+6iqqqrKC833v6jCYugj6LOzs4aGBkkDPTg4aDabWTNP6L29PdDPqvYaEKPRWFNT88ktHpqbm2dnZycmJjY2NniWNHbz0NBQR0cH64QVRgQz2t/fX15eBk1EBDo4OFi6uNChoaFKw62QkBBGMzk5GYrFYpGgoDs7O/v7+4uLi4nc39+DXllZeQkdHh6uNNyiyQpNS0tjtb0yOioqqry8HDcYGRlhBCT4auiKioq8vDybzfZH0IzJf7ToX0b7skIODg5As0wVsF7PZn4JHRkZiaNTCyxMQ4JiTz/1kOzsbG4HosrKSi8PeYaOiIjAw9jr7EasToLx8fF4XmtrK/szPz+fCDeQ7e1tTDUjI4PCRSUlJV7OFxYWpjTcoolPpqSkdHd3t7S0SDA2NpaSTSYT9JycHCJ45ObmJkYm1w8RMyT5LoGura1lZDlYEQ+NjY38zenp6YuLi52dHa1Wy/EB8fz8nAsNp9rk5CTfpiNixKemprjkiVZXVxlxRgZUABRuU9gFk4Z4GBsbm5mZoQNHOEUx4sPDw+Pj4/SkSRA03r24uMidkSCZ1CEiAYKLZrN9B804HE4lSFu2AAAAAElFTkSuQmCC";
                    warnIcon = new ImageIcon(getImage(imageStr));
                }
                break;
                case 1:  {
                    // image for descending sort
                    String imageStr = "iVBORw0KGgoAAAANSUhEUgAAAB4AAAAZCAYAAAAmNZ4aAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAAK7SURBVEhL1ZbNSypRFMCPn5SmlBW40GUtElomEa4EF67ClUt3/QEtA/+B/ggF8Q9wLwjpTgjapCCICwmTykj8wK/pnvPOjE4zk/PsvQfvB4e5954z59577rkfFkkATK/XA5/PB1arFRaLBVxcXEClUmGtmmazCbPZjGzNIHezt7cHBwcHoOq43W5DMBjkGsDZ2RmUSiVwuVzc8ouXlxe4urqCp6cncDgc3Po92M10OoV4PA7pdJoaFDqdDg5CkXA4LAlj1i6p1WrS4eGhytasxGIx8rE2TsKGS0swVDj6TXA6nfRVhfr5+Rn8fj/XAMSMoVwua8KJvxQKBfj4+ACbzcat34P/YN4cHR3B+fk5NSjohXoymbD2z2IuJXUYDocwGo1gPB4bCurRDrNfAw+AMDtj4VDa3d0lG7GdJIvFoiuyn+vra/5zyUYzbrVasLW1RWVcN+FHV2Tq9TqXlmzUMXa6vb1NZUw8I7Hb7SQ7Oztku8pGWY3rd3t7C+/v7+TYCHnmmMWJRIJbGexY5r/I6p+yUagREQnI5XJ06K+4IObzOZ3vGOL9/X1u/QJ2LPM7oW40GmQjHGvE4/FIp6enkrhg2FrL2lCL/cglNW9vb/R9fX3VSL/fh263S18jVB1jiFbBPWp0Gaxen3qISdEyGKFa48FgoNpz0WgUisUi17Tk83kQ16OyxvjFweIRiXsd11iEnXRfsdzf3+MRSAZijUHcl6wCEOsEmUyGwo2vk0AgAMfHx6z9OUoyrZNUKoUBIsRANHpxRbJ2PZTVyWRS42RVRNgkEVb6QaZarWrs8FWi92LRQ9lO2WxW4whFhFZ6fHxkqyUPDw+kl7eQ1+uVQqGQ6ZNOlVziLQUnJydcA4hEInB3d8c1Lbj+4nqkpMId4Xa76TFnCuz4K5eXl9LNzQ3X/g6qGf87AD4Bv/EzfaBJlCsAAAAASUVORK5CYII=";
                    warnIcon = new ImageIcon(getImage(imageStr));
                }
                break;
                case 2:   {
                    // image for ascending sort
                    String imageStr = "iVBORw0KGgoAAAANSUhEUgAAAB4AAAAZCAYAAAAmNZ4aAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAAJASURBVEhL1Za9rylBFMCPb/ERIjQUiEQkCgk9jcZf4E/QqkU0WqLV6BVqPdEIDZ0oiERCgSCRiI99M7Nn332z1n17XXkv95ec7DlzZuac+diZ0QgE+A88BL7f79DtdsHv90MwGMRSnn6/D+v1GnQ6HZao53a7gcvlAqCBJXa7nZBOp2kigt1uF9rtNnp4qP/bgn0Js9lMIKN8qFCr1bDGB/I6rwib6vF4DLFYjNjK5HI5qNfraAFUKhVoNBpgsViwRD3n8xmy2SxAtVpVzEguXq9XuFwubMTvQGM2mwWHw8E2Cs1ms9mQOCI2mw2cTidcr1c4Ho8wHA4hEomg93uwqV4sFmA0GmE6nUIymUQXQKFQgHw+zxIiCYLb7WblNDmahMFgYPZnkO5Br9dDKpXCEoQGlqAbjBZJQtYSPTw+n4/5tVqtKiGBhclkgq1FtKSD3+z3e9RETqcTajzL5ZJ96T+vRuhSrVYr1kaCC6zRaFATkdsSgUAANfUkEgnURLjAaiFLwtbuK2K1WrG1yEuB38HPCkwvD7r+n0mz2cTayrwUeD6fo/acXq+HmjIvBSb/MfvS005JKH874bj7WH5ZlMtlKBaLaH2w3W5hMBg8PbnoSZfJZNB6Ag0sMRqNuJOLBEbP+/lZu5rex3QN4/E4k3A4DK1WC70qwZEzyI3DTXWpVEIPz591JCGPAvSqgxsxeWeh9nU8Hg9q6uB2NVU7nQ4cDgcwmUxsGpU6lL8y6csxFApBNBplthoenrf/BoBfAtu19PKdgHYAAAAASUVORK5CYII=";
                    warnIcon = new ImageIcon(getImage(imageStr));
                }
                break;
            }
            lbl.setIcon(warnIcon);
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
        Color colorForGroundDis = new Color(0, 0, 0);

        Container container = getContentPane();
        container.setLayout(new FlowLayout());
        JPanel r = new JPanel(new BorderLayout());

        JLabel jLabel = new JLabel();
        jLabel.setIcon(new ImageIcon(new javax.swing.ImageIcon(img).getImage().getScaledInstance(650, 300, Image.SCALE_SMOOTH)));
        r.add(jLabel);

        JButton bConfirm=new JButton("Okay");
        r.add(bConfirm, BorderLayout.SOUTH);
        container.add(r);
        setVisible(true);
        setSize(680,380);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        bConfirm.setEnabled(true);
      //  bConfirm.setForeground(colorForGroundDis);
      //  bConfirm.setOpaque(true);

        bConfirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                dispose();
            }
        });
    }
}