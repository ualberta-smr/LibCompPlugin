package org.intellij.sdk.editor;

import java.sql.*;
import java.util.ArrayList;

public class SelectRecords {

    private Connection connectcloud(String dbName) throws ClassNotFoundException, SQLException {

        Class.forName("com.mysql.cj.jdbc.Driver");
        //String url = "jdbc:mysql://35.238.166.65:3306/db";
        String url = "jdbc:mysql://35.238.166.65:3306/"+dbName;
        String username = "root";
        String password = "uofa_2000";

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url,username,password);
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

    private Connection connectLocal(String dbName) throws ClassNotFoundException, SQLException {
        String filePath = System.getenv("APPDATA")+"\\LibComp";
        String url = "jdbc:sqlite:" + filePath + dbName;
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

        String sql = "select library_id, year, month, domain_id, librarycomparison_domain.name from librarycomparison_data, librarycomparison_domain where librarycomparison_data.domain_id = librarycomparison_domain.id and instr(Package,'" + librarySelected +"') > 0  order by year, month desc";
        ArrayList<String> Terms;
        Terms = new ArrayList<String>();

        try {
            Connection connection = this.connectLocal("\\db.sqlite3");
            Statement statement  = connection.createStatement();
            ResultSet resultSet    = statement.executeQuery(sql);

            while (resultSet.next()) {
                Terms.add(resultSet.getString("library_id") );
                Terms.add(resultSet.getString("domain_id") );
                Terms.add(resultSet.getString("year") );
                Terms.add(resultSet.getString("month") );
                Terms.add(resultSet.getString("name") );
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return (Terms);
    }

    public int updateVersionLocal(String sql) {
        int localVer = 0;
        try {
            Connection connection = this.connectLocal("\\db.sqlite3");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                localVer = resultSet.getInt("VersionNo");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return (localVer);
    }

    public int updateVersionNumberLocal(int localVer, int cloudVer) {
        String sql = "UPDATE Properties SET VersionNo = " + cloudVer +  " WHERE VersionNo = " + localVer+";";
        try {
            Connection connection = this.connectLocal("\\db.sqlite3");
            Statement statement = connection.createStatement();
            localVer= statement.executeUpdate(sql);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return (localVer);
    }


    public int deleteLocalData() {
        int localVer = 0;
        String sql = "DELETE  from librarycomparison_data;";
        try {
            Connection connection = this.connectLocal("\\db.sqlite3");
            Statement statement = connection.createStatement();
            localVer= statement.executeUpdate(sql);

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return (localVer);
    }


    public int updateVersionCloud(String sql) {
        int cloudVer = 0;
        try {
            Connection connection = this.connectcloud("db");
            Statement statement  = connection.createStatement();
            ResultSet resultSet    = statement.executeQuery(sql);

            while (resultSet.next()) {
                cloudVer = resultSet.getInt("VersionNo") ;
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return (cloudVer);
    }



    public int updateNewDataRecord (LibData DataRecord)
    {
        int returnValue = 0;
        String insetStatement = "INSERT INTO librarycomparison_data (id,name,repository,popularity,release_frequency,issue_closing_time,issue_response_time,domain_id,month,year,library_id,Package,performance,security)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        try {
            Connection conn  = this.connectLocal("\\db.sqlite3");
            PreparedStatement pstmt = conn.prepareStatement(insetStatement);
            pstmt.setInt(1, DataRecord.getId());
            pstmt.setString(2, DataRecord.getName());
            pstmt.setString(3, DataRecord.getRepository());
            pstmt.setInt(4, (int) DataRecord.getPopularity());
            pstmt.setInt(5, (int) DataRecord.getRelease_frequency());
            pstmt.setInt(6, (int) DataRecord.getIssue_closing_time());
            pstmt.setInt(7, (int) DataRecord.getIssue_response_time());
            pstmt.setInt(8, DataRecord.getDomain_id()); // domain_id
            pstmt.setInt(9, DataRecord.getMonth()); // month
            pstmt.setInt(10, DataRecord.getYear()); // year
            pstmt.setInt(11, DataRecord.getLibrary_id()); // library_id
            pstmt.setString(12, DataRecord.getPackage()); // Package
            pstmt.setInt(13, 0); // performance
            pstmt.setInt(14, 0); // security
            pstmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            //System.out.println(e.getMessage());
            returnValue = 0;
        }
        return (returnValue);
    }

    public int UpdateNewData(ArrayList <LibData> libraryList) {
        int returnValue = 0;
        String sql = null;
        for (int i = 0; i < libraryList.size(); i++) {
            returnValue = updateNewDataRecord(libraryList.get(i));
        }
        return (returnValue);
    }


    public int updateVersionData() {
        int returnValue = 1;
        int localVer = 10;
        int cloudVer = 20;
        // First Read Version No from local Db, and Store in localVer;

        String sql = "SELECT VersionNo from Properties;";
        // Then Read Version No from cloud Db, and Store in cloudVerlVer;
        localVer= updateVersionLocal(sql);
        cloudVer= updateVersionCloud(sql);

        // check now the if we need to upload
        if  (localVer < cloudVer)
        {
            // read data from Cloud, and store in local Db
            ArrayList <LibData> libraryList = GetVersionPerformanceValues();
            if (libraryList.size()>0)
            {
                returnValue = deleteLocalData();
                returnValue = UpdateNewData(libraryList);
                returnValue = updateVersionNumberLocal(localVer,cloudVer );
            }
        }
        return (returnValue);
    }


    public int updateUserProfile(userData userRecord, int type){

        String insetStatement = "UPDATE userprofile SET rate = ?, optionalFeedback = ?, Project1 = ?, Project2 = ?, Project3 = ?, Project4 = ?, Project5 = ?, Occupation = ?, Team1 = ?, Team2 = ?, Team3 = ?, Team4 = ?, Programming = ?, JavaSkills = ?, CloudStore = ? WHERE userID = ?";

        int returnValue = 1;
        try {
            Connection conn;
            if (type == 1)
            {
                conn  = this.connectLocal("\\library_feedback.sqlite3");
            }
            else
            {
                conn  = this.connectcloud("library_feedback");
            }

            PreparedStatement pstmt = conn.prepareStatement(insetStatement);
            pstmt.setString(1, userRecord.getRate());
            pstmt.setString(2, userRecord.getOptionalFeedback());
            pstmt.setString(3, userRecord.getProject1());
            pstmt.setString(4, userRecord.getProject2());
            pstmt.setString(5, userRecord.getProject3());
            pstmt.setString(6, userRecord.getProject4());
            pstmt.setString(7, userRecord.getProject5());
            pstmt.setString(8, userRecord.getOccupation());
            pstmt.setString(9, userRecord.getTeam1());
            pstmt.setString(10, userRecord.getTeam2());
            pstmt.setString(11, userRecord.getTeam3());
            pstmt.setString(12, userRecord.getTeam4());
            pstmt.setString(13, userRecord.getProgramming());
            pstmt.setString(14, userRecord.getJavaSkills());
            pstmt.setString(15, userRecord.getCloudStore());
            pstmt.setString(16, userRecord.getUserID());
            pstmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            //System.out.println(e.getMessage());
            returnValue = 0;
        }
        return (returnValue);
    }


    public int updateFeedback(feedback feedbackData, int type){
        String insetStatement = "";
        int returnValue = 1;
        try {
            Connection conn;
            PreparedStatement pstmt;

            insetStatement = "INSERT INTO feedback(fromLibrary,toLibrary,Location,projectID, classID,allLibrary,selectionLibrary,Local) VALUES(?,?,?,?,?,?,?,?)";
            conn  = this.connectLocal("\\library_feedback.sqlite3");
            pstmt = conn.prepareStatement(insetStatement);
            pstmt.setString(1, feedbackData.getFromLibrary());
            pstmt.setString(2, feedbackData.getToLibrary());
            pstmt.setInt(3, feedbackData.getLocation());
            pstmt.setString(4, feedbackData.getProjectId());
            pstmt.setString(5, feedbackData.getClassId());
            pstmt.setString(6, feedbackData.getAllLibrary());
            pstmt.setString(7, feedbackData.getSelectionLibrary());
            pstmt.setInt(8, type); // o sent to the cloud, 1 do not send, true local
            pstmt.executeUpdate();


            if (type == 0)

            {
                insetStatement = "INSERT INTO library_feedback.feedback(fromLibrary,toLibrary,Location,projectID, classID,allLibrary,selectionLibrary) VALUES(?,?,?,?,?,?,?)";
                conn  = this.connectcloud("library_feedback");
                pstmt = conn.prepareStatement(insetStatement);
                pstmt.setString(1, feedbackData.getFromLibrary());
                pstmt.setString(2, feedbackData.getToLibrary());
                pstmt.setInt(3, feedbackData.getLocation());
                pstmt.setString(4, feedbackData.getProjectId());
                pstmt.setString(5, feedbackData.getClassId());
                pstmt.setString(6, feedbackData.getAllLibrary());
                pstmt.setString(7, feedbackData.getSelectionLibrary());
                pstmt.executeUpdate();

            }


        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            returnValue = 0;
        }
        return (returnValue);
    }


    public ArrayList <LibData> GetPerformanceValues(int  metricDomain,int metricLibraryID, int metricyear, int metricmonth){

        String  sql = "select id, domain_id, repository,library_id, name, year, month, Package, domain_id, popularity, release_frequency, issue_closing_time, issue_response_time, backwards_compatibility " +
                " from librarycomparison_data where domain_id = "+metricDomain+" and year = "+metricyear+" and month = " + metricmonth;

        LibData libraryDataPoint;
        ArrayList <LibData> libraryList = new ArrayList<LibData> ();

        try {
            Connection connection = this.connectLocal("\\db.sqlite3");

            Statement statement  = connection.createStatement();
            ResultSet resultSet    = statement.executeQuery(sql);

            while (resultSet.next()) { //while we still have results

                libraryDataPoint = new LibData();
                libraryDataPoint.setId(resultSet.getInt("id"));
                libraryDataPoint.setLibrary_id(resultSet.getInt("Library_ID"));
                libraryDataPoint.setDomain_id(resultSet.getInt("domain_id"));
                libraryDataPoint.setName(resultSet.getString("name"));
                libraryDataPoint.setRepository(resultSet.getString("repository"));
                libraryDataPoint.setYear(resultSet.getInt("year"));
                libraryDataPoint.setMonth(resultSet.getInt("month"));
                libraryDataPoint.setPackage(resultSet.getString("Package")); // Change tag to package
                libraryDataPoint.setPopularity(resultSet.getDouble("popularity"));
                libraryDataPoint.setRelease_frequency(resultSet.getDouble("release_frequency"));
                libraryDataPoint.setIssue_closing_time(resultSet.getDouble("issue_closing_time"));
                libraryDataPoint.setIssue_response_time(resultSet.getDouble("issue_response_time"));
                //      libraryDataPoint.setPerformance(resultSet.getDouble("performance"));
                //     libraryDataPoint.setSecurity(resultSet.getDouble("security"));
                libraryDataPoint.setBackwards_compatibility(resultSet.getDouble("backwards_compatibility"));
                if ( metricLibraryID == libraryDataPoint.getLibrary_id())
                {
                    // at the first row for the selected one
                    libraryDataPoint.setName(libraryDataPoint.getName() + " \n(Current \n Library)");
                    libraryList.add(0,libraryDataPoint);
                }
                else
                {
                    libraryList.add(libraryDataPoint);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return (libraryList); //linear list of libraries with metric values
    }

    public ArrayList <LibData> GetVersionPerformanceValues(){

        String  sql = "select id, domain_id, repository, library_id, name, year, month, Package, domain_id, popularity, release_frequency, issue_closing_time, issue_response_time, backwards_compatibility " +
                " from librarycomparison_data";

        LibData libraryDataPoint;
        ArrayList <LibData> libraryList = new ArrayList<LibData> ();

        try {
            Connection connection = this.connectcloud("db");

            Statement statement  = connection.createStatement();
            ResultSet resultSet    = statement.executeQuery(sql);

            while (resultSet.next()) { //while we still have results

                libraryDataPoint = new LibData();
                libraryDataPoint.setId(resultSet.getInt("id"));
                libraryDataPoint.setLibrary_id(resultSet.getInt("library_ID"));
                libraryDataPoint.setDomain_id(resultSet.getInt("domain_id"));
                libraryDataPoint.setRepository(resultSet.getString("repository"));
                libraryDataPoint.setName(resultSet.getString("name"));
                libraryDataPoint.setYear(resultSet.getInt("year"));
                libraryDataPoint.setMonth(resultSet.getInt("month"));
                libraryDataPoint.setPackage(resultSet.getString("Package")); // Change tag to package
                libraryDataPoint.setPopularity(resultSet.getDouble("popularity"));
                libraryDataPoint.setRelease_frequency(resultSet.getDouble("release_frequency"));
                libraryDataPoint.setIssue_closing_time(resultSet.getDouble("issue_closing_time"));
                libraryDataPoint.setIssue_response_time(resultSet.getDouble("issue_response_time"));
                libraryDataPoint.setBackwards_compatibility(resultSet.getDouble("backwards_compatibility"));
                libraryList.add(libraryDataPoint);

            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return (libraryList); //linear list of libraries with metric values
        //     return (data);
    }
}

