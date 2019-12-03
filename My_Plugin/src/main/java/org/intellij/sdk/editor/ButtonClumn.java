package org.intellij.sdk.editor;

import com.intellij.ui.components.JBScrollPane;

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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;


//OUR MAIN CLASS
public class ButtonClumn extends JFrame { //entire java frame
    private String termSelected;
    private String LibraryReturned = "None";
    private int month;
    private int year;
    private int libID;
    private int domainID;
    private String selectionLibrary = " ";
    String[] columnToolTips = {"Column 0 shows your current library, click on an alternative library to replace", //alternative libraries
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

    public ButtonClumn(String domainName, int domainId,int libID, int year, int month) throws HeadlessException {
        this.termSelected = termSelected;
        this.libID = libID;
        this.year = year;
        this.month = month;
        this.domainID = domainId;


        //DATA FOR OUR TABLE
        int rowLength = 2;
        int columnLength = 8;

        this.setTitle(domainName);

        SelectRecords dataAccessObject = new SelectRecords();
        ArrayList<String> libraryChoicesArray = dataAccessObject.GetMetrics();

        //COLUMN HEADERS
        columnLength = libraryChoicesArray.size() + 1;

        String columnHeaders[] = new String[columnLength]; //?
        int current = 1;
        columnHeaders[0] = " \nAlternative Libraries";
        String replacedString;
        while (current < columnLength){
            replacedString =  libraryChoicesArray.get(current-1).replace("NLINE","\n");
            columnHeaders[current] =  replacedString;
            current = current + 1;
        }

        columnLength = current;
        libraryChoicesArray.clear();
        ArrayList <LibData> libraryList;
        libraryList = dataAccessObject.GetPerformanceValues(domainId, libID, year, month, columnLength-1);

        //libraryChoicesArray =
        rowLength = libraryList.size();
        Object[][] data = new Object[rowLength][columnLength];

        current = 0;
        DecimalFormat df = new DecimalFormat("#.00");

        while (current < rowLength){

            data[current][0] = "  " + libraryList.get(current).getName();
            data[current][1] = df.format(libraryList.get(current).getPopularity());
            data[current][2] = df.format(libraryList.get(current).getRelease_frequency());
            data[current][3] = df.format(libraryList.get(current).getIssue_closing_time());
            data[current][4] = df.format(libraryList.get(current).getIssue_response_time());
            data[current][5] = df.format(libraryList.get(current).getPerformance());
            data[current][6] = df.format(libraryList.get(current).getSecurity());
            data[current][7] = df.format(libraryList.get(current).getBackwards_compatibility());
            selectionLibrary = selectionLibrary + ";" + libraryList.get(current).getPackage();
            current = current + 1;
 }

        int frameHeight = 30;
        frameHeight= 100 + (frameHeight * current);

        //CREATE OUR TABLE AND SET HEADER
        JTable table=new JTable(data,columnHeaders){
            public boolean isCellEditable(int row, int column) {
                if (column >= 0) {
                    return false;
                }
                else
                    return true; //column of buttons
            };

            //Implement table cell tool tips.
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                //      int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                int realColumnIndex = convertColumnIndexToModel(colIndex);
                try {
                    tip = columnToolTips[realColumnIndex];

                } catch (RuntimeException e1) {
                    //catch null pointer exception if mouse is over an empty line
                }
                return tip;
            }

            protected JTableHeader createDefaultTableHeader() {
                return new JTableHeader(columnModel) {
                    public String getToolTipText(MouseEvent e) {
                        String tip = null;
                        java.awt.Point p = e.getPoint();
                        int index = columnModel.getColumnIndexAtX(p.x);
                        int realIndex = columnModel.getColumn(index).getModelIndex();
                        return columnToolTips[realIndex];
                    }
                };
            }

        };

        // Prepare the headers to be multilines
        MultiLineHeaderRenderer renderer = new MultiLineHeaderRenderer();
        Enumeration Enum = table.getColumnModel().getColumns();
        while (Enum.hasMoreElements()) {
            ((TableColumn)Enum.nextElement()).setHeaderRenderer(renderer);
        }

        //JTable jTable = new JTable()
        table.setRowHeight(30);
        //SET CUSTOM RENDERER TO TEAMS COLUMN
        table.getColumnModel().getColumn(0).setMaxWidth(250);
        table.getColumnModel().getColumn(0).setPreferredWidth(250);

        table.getTableHeader().setReorderingAllowed(false);
        table.setGridColor(Color.black);
        table.setShowGrid(true);

        // alternate each row color, first row color is fixed, all cells centre alligned

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value, boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table,
                        value, isSelected, hasFocus, row, column);
                c.setBackground(row%2==0 ? new Color(245,255,250) : new Color(253,245,230));

               if (isSelected) {
                   c.setForeground(Color.black);
                   c.setFont(new Font("SansSerif", Font.BOLD, 16));
               }
               else
               {
                   c.setFont(ButtonClumn.this.getFont());
                   c.setForeground(Color.black);
               }

                if (column > 0) {
                    setHorizontalAlignment(JLabel.CENTER);
                } else
                {
                    setHorizontalAlignment(JLabel.LEFT);
                }
                return c;
            };
        });


        int ColumnWidth = 800;
        int x = ColumnWidth - 250 ;
        int y = frameHeight - 35;
        int widthsize = 100;
        int heightsize = 40;

        JButton bConfirm=new JButton("Replace");
        JButton bCancel=new JButton("Cancel");

        bCancel.setBounds(x,y,widthsize, heightsize);
        bConfirm.setBounds(x+widthsize+10,y,widthsize, heightsize);

        bConfirm.setEnabled(false);

        getContentPane().add(bConfirm);
        getContentPane().add(bCancel);

        bConfirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                int row = table.getSelectedRow();
              //  int column = table.getSelectedColumn();
                if (row > 0) {
                    LibraryReturned = libraryList.get(row).getPackage();
                    // LibraryReturned = (data[row][0]).toString();
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
            public void mouseClicked (MouseEvent e) {
                int row = table.getSelectedRow();
               // int column = table.getSelectedColumn();
                if (row > 0) {
                    bConfirm.setEnabled(true);

                                    }
                if (row == 0) {
                    bConfirm.setEnabled(false);
                                    }


            }
        });

        //SCROLLPANE,SET SZE,SET CLOSE OPERATION
        JBScrollPane pane = new JBScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        table.setSize(ColumnWidth,frameHeight);
        pane.setBounds(0, 0, ColumnWidth, frameHeight);
        getContentPane().add(pane);
        setSize(ColumnWidth,frameHeight + 50);

        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

    }
 }

class MultiLineHeaderRenderer extends JList implements TableCellRenderer {
    public MultiLineHeaderRenderer() {
        setOpaque(true);
        setForeground(UIManager.getColor("TableHeader.foreground"));
       setBackground(UIManager.getColor("TableHeader.background"));
// just for header
        Border border = BorderFactory.createLineBorder(Color.black,1);
        setBorder(border);

        ListCellRenderer renderer = getCellRenderer();
        ((JLabel)renderer).setHorizontalAlignment(JLabel.CENTER);
        setCellRenderer(renderer);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {

       // setFont(table.getFont());
        setFont(new Font("SansSerif", Font.ITALIC, 12));
        String str = (value == null) ? "" : value.toString();
        BufferedReader br = new BufferedReader(new StringReader(str));
        String line;
        Vector v = new Vector();
        try {
            while ((line = br.readLine()) != null) {
                v.addElement(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        setListData(v);
        return this;
    }
}