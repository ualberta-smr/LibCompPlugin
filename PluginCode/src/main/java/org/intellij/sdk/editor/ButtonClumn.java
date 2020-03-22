package org.intellij.sdk.editor;

import com.intellij.ui.components.JBScrollPane;

import javax.imageio.ImageIO;
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
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;


//OUR MAIN CLASS
public class ButtonClumn extends JFrame {
    private String termSelected;

    private String LibraryReturned = "None";
    private int month;
    private int year;
    private int libID;
    private int domainID;
    private int columnLength = 0;
    private int startingSort = 4; // after current Library
    private int offsetBtnCols = 4;
    private int currentLibrary = 4;
    private int rowLength;
    private String selectionLibrary = " ";
    private double[][] dataDouble;
    private JTable table;
    private String columnHeaders[];
    private DecimalFormat df;
    private DecimalFormat intf;
    private DecimalFormat daysf;
    private DecimalFormat reposf;
    private DecimalFormat percentf;
    private DecimalFormat changef;
    private Color colorBackGround = new Color(128, 128, 128);
    private Color colorForGround = new Color(255, 255, 255);
    private Color colorAlternateLine = new Color(230, 230, 230);
    private Color cololrSelectColumn = Color.lightGray;
    private Color cololrCurrentLibrary = new Color(210, 210, 210);
    private ArrayList<LibData> libraryList;

    String[] columnToolTips = {"Column 1 Chart", // chart
            "Column 2 Sort Descending", // sort asc
            "Column 3 Sort Ascending", // sort desc
            "The header row shows your current library, click on an alternative library to replace", //alternative libraries
            "Number of times the library is imported per 1000 repositories", //popularity
            "Average time in days between two consecutive releases of a library", //release frequency
            "Average time in days to close issues in the issue tracking system of a library", //issue closing time
            "Average time in days to get the first response on issues in the issue tracking system of a library", //issue response time
            "Approximation of the percentage of performance related issues of a library", //performance
            "Approximation of the percentage of security related issues of a library", //security
            "Average number of code breaking changes per release"}; //backwards compatibility

    public String getSelectionLibrary() { return selectionLibrary; }

    public String getLibraryReturned() {
        return LibraryReturned;
    }

    public ButtonClumn(String domainName, int domainId, int libID, int year, int month) throws HeadlessException {
        this.termSelected = termSelected;
        this.libID = libID;
        this.year = year;
        this.month = month;
        this.domainID = domainId;
        Border border = BorderFactory.createLineBorder(Color.black, 1);

        //DATA FOR OUR TABLE
        this.setTitle(domainName);

        SelectRecords dataAccessObject = new SelectRecords();

        libraryList = dataAccessObject.GetPerformanceValues(domainId, libID, year, month);
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

        rowLength = 7;
        Object[][] data = new Object[rowLength][columnLength];
        dataDouble = new double[rowLength][columnLength];

        current = 0;
        df = new DecimalFormat("#");
        intf = new DecimalFormat("# Repos");
        daysf = new DecimalFormat("# Days");
        reposf = new DecimalFormat("# Repos");
        percentf = new DecimalFormat("# %");
        changef = new DecimalFormat("# Changes");


        data[0][offsetBtnCols - 1] = "Popularity (Repos)";
        data[1][offsetBtnCols - 1] = "Release Frequency (Days)";
        data[2][offsetBtnCols - 1] = "Issue Closing Time (Days)";
        data[3][offsetBtnCols - 1] = "Issue Response Time (Repos)";
        data[4][offsetBtnCols - 1] = "Performance (Percent)";
        data[5][offsetBtnCols - 1] = "Security (Percent)";
        data[6][offsetBtnCols - 1] = "Backwards Compatibility";

        while (current < columnLength - offsetBtnCols) {

            dataDouble[0][current + offsetBtnCols] = (libraryList.get(current).getPopularity());
            dataDouble[1][current + offsetBtnCols] = (libraryList.get(current).getRelease_frequency());
            dataDouble[2][current + offsetBtnCols] = (libraryList.get(current).getIssue_closing_time());
            dataDouble[3][current + offsetBtnCols] = (libraryList.get(current).getIssue_response_time());
            dataDouble[4][current + offsetBtnCols] = (libraryList.get(current).getPerformance());
            dataDouble[5][current + offsetBtnCols] = (libraryList.get(current).getSecurity());
            dataDouble[6][current + offsetBtnCols] = (libraryList.get(current).getBackwards_compatibility());


            data[0][current + offsetBtnCols] = intf.format(libraryList.get(current).getPopularity());
            data[1][current + offsetBtnCols] = daysf.format(libraryList.get(current).getRelease_frequency());
            data[2][current + offsetBtnCols] = daysf.format(libraryList.get(current).getIssue_closing_time());
            data[3][current + offsetBtnCols] = reposf.format(libraryList.get(current).getIssue_response_time());
            data[4][current + offsetBtnCols] = percentf.format(libraryList.get(current).getPerformance());
            data[5][current + offsetBtnCols] = percentf.format(libraryList.get(current).getSecurity());
            data[6][current + offsetBtnCols] = changef.format(libraryList.get(current).getBackwards_compatibility());

            selectionLibrary = selectionLibrary + ";" + libraryList.get(current).getPackage(); // for feedback
            current = current + 1;
        }

        int frameHeight = 30;
        frameHeight = 110 + (frameHeight * 7);

        // make sure that only the first 3 columns buttomsn are editable
        table = new JTable(data, columnHeaders) {
            public boolean isCellEditable(int row, int column) {
                if (column > 2) {
                    return false;
                } else
                    return true; //column of buttons
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
                    //catch null pointer exception if mouse is over an empty line
                }
                return tip;
            }

            protected JTableHeader createDefaultTableHeader() {
                return new JTableHeader(columnModel) {
                    public String getToolTipText(MouseEvent e) {
                        //  String tip = null;
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
        MultiLineHeaderRenderer renderer = new MultiLineHeaderRenderer();
        Enumeration Enum = table.getColumnModel().getColumns();
        while (Enum.hasMoreElements()) {
            ((TableColumn) Enum.nextElement()).setHeaderRenderer(renderer);
        }

        for (int i = 0; i < 3; i++) {

            table.getColumnModel().getColumn(i).setCellRenderer(new ImageRenderer()); // first 3 columsn make them image columns
            table.getColumnModel().getColumn(i).setMaxWidth(30);
            table.getColumnModel().getColumn(i).setPreferredWidth(30);
            table.getColumnModel().getColumn(i).setHeaderRenderer(new MergeHeaderRenderer()); // change the cell of the header, remove borders

        }

        table.getColumnModel().getColumn(3).setMaxWidth(225);
        table.getColumnModel().getColumn(3).setPreferredWidth(225);

        table.getTableHeader().setReorderingAllowed(false); // users cannot change the order of columns by dragging them
        table.setGridColor(Color.black);
        table.setColumnSelectionAllowed(true); // to allow selection based on columns
        table.setRowSelectionAllowed(false); // to disable section based on rows
        table.setRowHeight(30);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value, boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table,
                        value, isSelected, hasFocus, row, column);

                c.setBackground(row % 2 == 0 ? Color.white : colorAlternateLine);

                if (currentLibrary == column)
                    c.setBackground(cololrCurrentLibrary);

                if (isSelected) {
                    if ((column > offsetBtnCols - 1) && (column != currentLibrary))
                        c.setBackground(cololrSelectColumn);
                } else {
                    c.setForeground(Color.black);
                }
                if (column == 3) {
                    setHorizontalAlignment(JLabel.LEFT);
                    c.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
                } else
                    setHorizontalAlignment(JLabel.CENTER);
                return c;
            }

            ;
        });

        int ColumnWidth = 1000; // I made the column width bigger so that I can increase font size
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
        bConfirm.setBackground(colorBackGround);
        bConfirm.setForeground(colorForGround);
        bCancel.setBackground(colorBackGround);
        bCancel.setForeground(colorForGround);

        getContentPane().add(bConfirm);
        getContentPane().add(bCancel);

        bConfirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                int row = table.getSelectedRow();
                int column = table.getSelectedColumn();
                if ((column >= 4)) {
                    LibraryReturned = libraryList.get(column - offsetBtnCols).getPackage();
                    //these 2 lines for pop-up are just for testing - will be removed
                   // String message = "Selected Package is for " + LibraryReturned;
                  //  JOptionPane.showMessageDialog(null, message);
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

                    String message = "Replace " + libraryList.get(0).getPackage() + " Package with " + libraryList.get(columnM - offsetBtnCols).getPackage();
                    bConfirm.setText(message);
                }
                if ((columnM < 4) || (columnM == currentLibrary)) {
                    bConfirm.setEnabled(false);
                }

                //How to add image - eventually needs to read from data base????
                if ((columnM == 0)) {
                    String message = " "+ data[rowM][3];
                    String filePath = System.getenv("APPDATA")+"\\LibComp";
                    String imagepath = filePath + "\\Image-" + year + "-" + month + "-" + domainID + "-" + (rowM + 1) + ".png";
                    //   imagepath =  "c:\\temp\\Image-2019-6-72-1.jpg";
                        new SwingDemo(message,imagepath);

                }
                if (columnM == 1) {
                    int typeofSort = 1;
                    sortingTable(typeofSort, rowM);
                }
                if (columnM == 2) {
                    int typeofSort = 2;
                    sortingTable(typeofSort, rowM);
                }
            }
        });

        //SCROLLPANE,SET SZE,SET CLOSE OPERATION
        JBScrollPane pane = new JBScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //JScrollPane pane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        table.setSize(ColumnWidth, frameHeight);


        pane.setBorder(border);
        pane.setBounds(0, 0, ColumnWidth, frameHeight);
        getContentPane().add(pane);
        setSize(ColumnWidth, frameHeight + 50);

        // disable (comment out) next two lines if you need to show title of the dialog and buttons of max and min
        setSize(ColumnWidth, frameHeight + 10);
        setUndecorated(true);

       // setBackground(Color.black);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void sortingTable(int typeofSort, int row) {
        int i, j, largest, rowIndex;
        double tempValue;
        String tempHeader;
        LibData tempData;
        for (i = startingSort; i < columnLength - 1; i++) {
            largest = i;

            for (j = i + 1; j < columnLength; j++) {
                if (typeofSort == 1) {
                    if (dataDouble[row][largest] < dataDouble[row][j]) {
                        largest = j;
                    }
                } else {
                    if (dataDouble[row][largest] > dataDouble[row][j]) {
                        largest = j;
                    }
                }// end if
                // Swap largest
            } // for j

            // Swap Headers
            tempHeader = columnHeaders[i];
            columnHeaders[i] = columnHeaders[largest];
            columnHeaders[largest] = tempHeader;

            // Swap the datalist
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

        } // for i

        for (i = startingSort; i < columnLength; i++) {
            table.getColumnModel().getColumn(i).setHeaderValue(columnHeaders[i]);
            table.getTableHeader().repaint();

            for (rowIndex = 0; rowIndex < rowLength; rowIndex++) {

                switch (rowIndex) {
                    case 0:
                        table.getModel().setValueAt(intf.format(dataDouble[rowIndex][i]), rowIndex, i);
                        break;
                    case 1:
                        table.getModel().setValueAt(daysf.format(dataDouble[rowIndex][i]), rowIndex, i);
                        break;
                    case 2:
                        table.getModel().setValueAt(daysf.format(dataDouble[rowIndex][i]), rowIndex, i);
                        break;
                    case 3:
                        table.getModel().setValueAt(reposf.format(dataDouble[rowIndex][i]), rowIndex, i);
                        break;
                    case 4:
                        table.getModel().setValueAt(percentf.format(dataDouble[rowIndex][i]), rowIndex, i);
                        break;
                    case 5:
                        table.getModel().setValueAt(percentf.format(dataDouble[rowIndex][i]), rowIndex, i);
                        break;
                    case 6:
                        table.getModel().setValueAt(changef.format(dataDouble[rowIndex][i]), rowIndex, i);
                        break;
                    default:
                        table.getModel().setValueAt(df.format(dataDouble[rowIndex][i]), rowIndex, i);
                        break;
                }
            }
        }
    }
}


    class ImageRenderer extends DefaultTableCellRenderer {
        JLabel lbl = new JLabel();

        public ImageRenderer() {
            setOpaque(true);
            Border border = BorderFactory.createLineBorder(Color.black,0);
            setBorder(border);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {

            Icon warnIcon = null;
            String filePath = System.getenv("APPDATA")+"\\LibComp";

            switch (column) {
                case 0:  { warnIcon = new ImageIcon(filePath+"\\chart4.png"); }
                break;
                case 1:  { warnIcon = new ImageIcon(filePath+"\\SortDown.png"); }
                break;
                case 2:   { warnIcon = new ImageIcon(filePath+"\\SortUp.png"); }
                break;
            }

            lbl.setIcon(warnIcon);
            return lbl;
        }
    }


    class MergeHeaderRenderer extends JLabel implements TableCellRenderer {

        public MergeHeaderRenderer() {
            setOpaque(true);
            setBackground(new Color(128,128,128)); //background color for table header
            setForeground(new Color(255,255,255)); //background color for table header
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
            setBackground(new Color(128,128,128)); //background color for table header
            setForeground(new Color(255,255,255)); //background color for table header
            // just for header
            Border border = BorderFactory.createLineBorder(Color.black,1);
            setBorder(border);
            ListCellRenderer renderer = getCellRenderer();
            ((JLabel)renderer).setHorizontalAlignment(JLabel.CENTER);
            setCellRenderer(renderer);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

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

class SwingDemo extends JFrame {
    private  String imagePath;

    public SwingDemo(String title, String imagePath) throws HeadlessException {
        super(title);
        this.imagePath = imagePath;
        pack();

        Container container = getContentPane();
        container.setLayout(new FlowLayout());
        JPanel r = new JPanel(new BorderLayout());
        try {
            r.add(new JLabel(new ImageIcon(ImageIO.read(new File(imagePath)))));
        }catch (IOException e) {
            e.getMessage().toString();
        }
        JButton bConfirm=new JButton("Okay");
        r.add(bConfirm, BorderLayout.SOUTH);
        container.add(r);
        setVisible(true);
        setSize(800,400);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        bConfirm.setEnabled(true);
        bConfirm.setOpaque(true);

        bConfirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                dispose(); //force close
            }
        });
    }
}
