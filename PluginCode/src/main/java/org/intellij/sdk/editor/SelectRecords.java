package org.intellij.sdk.editor;

import java.sql.*;
import java.util.ArrayList;

public class SelectRecords {

    /**
     * The connect method establishes a connection to the database
     * For now, a local database is being used (db.sqlite3), this database should be placed in the appdata folder in windows
     * LOCATION: C:\Users\[userid]\AppData\Roaming
     * @return The connection to the database is returned
     */
    private Connection connect() {
        String filePath = System.getenv("APPDATA");
        String url = "jdbc:sqlite:" + filePath + "\\db.sqlite3";

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

    private Connection connectFeedback() {
        String filePath = System.getenv("APPDATA");
        String url = "jdbc:sqlite:" + filePath + "\\library_feedback.sqlite3";

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }


    /**
     * The selectAllLibraries method will query the database with a given library to get all the other Libraries within the same domain
     * @param librarySelected is the library selected by the user to be replaced with other similar libraries
     * @return A list of all the possibly library replacements is returned
     */
    public ArrayList<String> selectAllLibraries(String librarySelected){

    //   String sql1 = "select library_id, year, month, domain_id from librarycomparison_data where instr(Package,'"+librarySelected+"') > 0 order by year, month desc";

        String sql = "select library_id, year, month, domain_id, librarycomparison_domain.name from librarycomparison_data, librarycomparison_domain where librarycomparison_data.domain_id = librarycomparison_domain.id and instr(Package,'" + librarySelected +"') > 0  order by year, month desc";
//        String sql = "select Replaceterm from Librarycomparison_suggest where keyterm = \"" + librarySelected +"\"";
        ArrayList<String> Terms;
        Terms = new ArrayList<String>();

        try {
            Connection connection = this.connect();
            Statement statement  = connection.createStatement();
            ResultSet resultSet    = statement.executeQuery(sql);

            while (resultSet.next()) {
                Terms.add(resultSet.getString("library_id") );
                Terms.add(resultSet.getString("domain_id") );
                Terms.add(resultSet.getString("year") );
                Terms.add(resultSet.getString("month") );
                Terms.add(resultSet.getString("name") );
                           }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return (Terms);
    }

    public int updateFeedback(feedback feedbackData){

        String insetStatement = "INSERT INTO feedback(fromLibrary,toLibrary,Location,projectID, classID,allLibrary,selectionLibrary) VALUES(?,?,?,?,?,?,?)";
        int returnValue = 1;
        try {

            Connection conn  = this.connectFeedback();
            PreparedStatement pstmt = conn.prepareStatement(insetStatement);
            pstmt.setString(1, feedbackData.getFromLibrary());
            pstmt.setString(2, feedbackData.getToLibrary());
            pstmt.setInt(3, feedbackData.getLocation());
            pstmt.setString(4, feedbackData.getProjectId());
            pstmt.setString(5, feedbackData.getClassId());
            pstmt.setString(6, feedbackData.getAllLibrary());
            pstmt.setString(7, feedbackData.getSelectionLibrary());

              //  pstmt.setDouble(2, c);
                pstmt.executeUpdate();


        } catch (SQLException e) {
            //System.out.println(e.getMessage());
            returnValue = 0;
        }
        return (returnValue);
    }


    public ArrayList <LibData> GetPerformanceValues(int  metricDomain,int metricLibraryID, int metricyear, int metricmonth, int noOfColumns){

    String  sql = "select id, library_id, name, year, month, Package, domain_id, popularity, release_frequency, issue_closing_time, issue_response_time, performance, security, backwards_compatibility " +
        " from librarycomparison_data where domain_id = "+metricDomain+" and year = "+metricyear+" and month = " + metricmonth;

      LibData libraryDataPoint;
      ArrayList <LibData> libraryList = new ArrayList<LibData> ();

        try {
            Connection connection = this.connect();
            Statement statement  = connection.createStatement();
            ResultSet resultSet    = statement.executeQuery(sql);

                while (resultSet.next()) { //while we still have results

                    libraryDataPoint = new LibData();
                    libraryDataPoint.setLibrary_id(resultSet.getInt("Library_ID"));
                    libraryDataPoint.setName(resultSet.getString("name").toString());
                    libraryDataPoint.setYear(resultSet.getInt("year"));
                    libraryDataPoint.setMonth(resultSet.getInt("month"));
                    libraryDataPoint.setPackage(resultSet.getString("Package").toString()); // Change tag to package
                    libraryDataPoint.setPopularity(resultSet.getDouble("popularity"));
                    libraryDataPoint.setRelease_frequency(resultSet.getDouble("release_frequency"));
                    libraryDataPoint.setIssue_closing_time(resultSet.getDouble("issue_closing_time"));
                    libraryDataPoint.setIssue_response_time(resultSet.getDouble("issue_response_time"));
                    libraryDataPoint.setPerformance(resultSet.getDouble("performance"));
                    libraryDataPoint.setSecurity(resultSet.getDouble("security"));
                    libraryDataPoint.setBackwards_compatibility(resultSet.getDouble("backwards_compatibility"));
                    if ( metricLibraryID == libraryDataPoint.getLibrary_id())
                    {
                        // at the first row for the selected one
                        libraryDataPoint.setName(libraryDataPoint.getName() + " (Current Library)");
                        libraryList.add(0,libraryDataPoint);
                    }
                        else
                    {
                        libraryList.add(libraryDataPoint);
                    }

         //       if (prev_Library.equals(resultSet.getString("Library_ID").toString()) == false)// {
           //     }

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
           return (libraryList); //linear list of libraries with metric values
   //     return (data);
    }

    public ArrayList<String> GetMetrics(){
        String sql = "select Title from LibraryComparison_Metrics order by Metric_ID" ;
        ArrayList<String> Terms;
        Terms = new ArrayList<String>();

        try {
            Connection connection = this.connect();
            Statement statement  = connection.createStatement();
            ResultSet resultSet    = statement.executeQuery(sql);

            while (resultSet.next()) {
              //  System.out.println(resultSet.getString("Title") );
                Terms.add(resultSet.getString("Title") );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return (Terms);
    }
}

