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
        int cloudVersion = 0;
        try {
            Connection connection = this.connectcloud("db");
            Statement statement  = connection.createStatement();
            ResultSet resultSet    = statement.executeQuery(sql);

            while (resultSet.next()) {
                cloudVersion = resultSet.getInt("VersionNo") ;
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return (cloudVersion);
    }

    public int updateNewDataRecord (LibData DataRecord) {
        int returnValue = 0;
        String insetStatement = "INSERT INTO librarycomparison_data (id,name,repository,popularity,release_frequency,issue_closing_time,issue_response_time,domain_id,month,year,library_id,Package,performance,security)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

        try {
            Connection connection  = this.connectLocal("\\db.sqlite3");
            PreparedStatement preparedStatement = connection.prepareStatement(insetStatement);
            preparedStatement.setInt(1, DataRecord.getId());
            preparedStatement.setString(2, DataRecord.getName());
            preparedStatement.setString(3, DataRecord.getRepository());
            preparedStatement.setInt(4, (int) DataRecord.getPopularity());
            preparedStatement.setInt(5, (int) DataRecord.getRelease_frequency());
            preparedStatement.setInt(6, (int) DataRecord.getIssue_closing_time());
            preparedStatement.setInt(7, (int) DataRecord.getIssue_response_time());
            preparedStatement.setInt(8, DataRecord.getDomain_id()); // domain_id
            preparedStatement.setInt(9, DataRecord.getMonth()); // month
            preparedStatement.setInt(10, DataRecord.getYear()); // year
            preparedStatement.setInt(11, DataRecord.getLibrary_id()); // library_id
            preparedStatement.setString(12, DataRecord.getPackage()); // Package
            preparedStatement.setInt(13, 0); // performance
            preparedStatement.setInt(14, 0); // security
            preparedStatement.executeUpdate();
        }
        catch (SQLException | ClassNotFoundException e) {
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
        int localVersion = 10;
        int cloudVersion = 20;

        //First Read Version Number from local DB, and Store in localVersion
        String sql = "SELECT VersionNo from Properties;";

        //Then Read Version Number from cloud DB, and Store in cloudVersion
        localVersion= updateVersionLocal(sql);
        cloudVersion= updateVersionCloud(sql);

        //Check if data must be updated
        if  (localVersion < cloudVersion) {
            // read data from Cloud, and store in local DB
            ArrayList <LibData> libraryList = GetVersionPerformanceValues();
            if (libraryList.size()>0) {
                returnValue = deleteLocalData();
                returnValue = UpdateNewData(libraryList);
                returnValue = updateVersionNumberLocal(localVersion,cloudVersion );
            }
        }
        return (returnValue);
    }

    public userData ReadUserProfile() {
        userData userRecord = null;

        String  sql = "select  Project1, Project2, Project3 , Project4 , Project5 , Occupation , Team1 , Team2 , Team3 , Team4 , Programming , JavaSkills from userprofile";

        try {
            Connection connection  = this.connectLocal("\\library_feedback.sqlite3");
            Statement statement  = connection.createStatement();
            ResultSet resultSet    = statement.executeQuery(sql);

            while (resultSet.next()) {
                userRecord = new userData();
                userRecord.setProject1(resultSet.getString("Project1"));
                userRecord.setProject2(resultSet.getString("Project2"));
                userRecord.setProject3(resultSet.getString("Project3"));
                userRecord.setProject4(resultSet.getString("Project4"));
                userRecord.setProject5(resultSet.getString("Project5"));
                userRecord.setOccupation(resultSet.getString("Occupation"));
                userRecord.setTeam1(resultSet.getString("Team1"));
                userRecord.setTeam2(resultSet.getString("Team2"));
                userRecord.setTeam3(resultSet.getString("Team3"));
                userRecord.setTeam4(resultSet.getString("Team4"));
                userRecord.setProgramming(resultSet.getString("Programming"));
                userRecord.setJavaSkills(resultSet.getString("JavaSkills"));
        }
        }
        catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return userRecord;
    }

        public int updateUserProfile(userData userRecord, int type){

        String insetStatement = "UPDATE userprofile SET Project1 = ?, Project2 = ?, Project3 = ?, Project4 = ?, Project5 = ?, Occupation = ?, Team1 = ?, Team2 = ?, Team3 = ?, Team4 = ?, Programming = ?, JavaSkills = ?, CloudStore = ? WHERE userID = ?";

        int returnValue = 1;
        try {
            Connection connection;
            if (type == 1) {
                connection  = this.connectLocal("\\library_feedback.sqlite3");
            }
            else {
                connection  = this.connectcloud("library_feedback");
            }

            PreparedStatement preparedStatement = connection.prepareStatement(insetStatement);
            //preparedStatement.setString(1, userRecord.getRate());
            //preparedStatement.setString(2, userRecord.getOptionalFeedback());
            preparedStatement.setString(1, userRecord.getProject1());
            preparedStatement.setString(2, userRecord.getProject2());
            preparedStatement.setString(3, userRecord.getProject3());
            preparedStatement.setString(4, userRecord.getProject4());
            preparedStatement.setString(5, userRecord.getProject5());
            preparedStatement.setString(6, userRecord.getOccupation());
            preparedStatement.setString(7, userRecord.getTeam1());
            preparedStatement.setString(8, userRecord.getTeam2());
            preparedStatement.setString(9, userRecord.getTeam3());
            preparedStatement.setString(10, userRecord.getTeam4());
            preparedStatement.setString(11, userRecord.getProgramming());
            preparedStatement.setString(12, userRecord.getJavaSkills());
            preparedStatement.setString(13, userRecord.getCloudStore());
            preparedStatement.setString(14, userRecord.getUserID());
            preparedStatement.executeUpdate();
        }
        catch (SQLException | ClassNotFoundException e) {
            //System.out.println(e.getMessage());
            returnValue = 0;
        }
        return (returnValue);
    }


    public userData ReadUserProfile2() {
        userData userRecord = null;

        String  sql = "select  rate, optionalFeedback, CloudStore from userprofile";

        try {
            Connection connection  = this.connectLocal("\\library_feedback.sqlite3");
            Statement statement  = connection.createStatement();
            ResultSet resultSet    = statement.executeQuery(sql);

            while (resultSet.next()) {
                userRecord = new userData();
                userRecord.setRate(resultSet.getString("rate"));
                userRecord.setOptionalFeedback(resultSet.getString("optionalFeedback"));
                userRecord.setCloudStore(resultSet.getString("CloudStore"));
            }
        }
        catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return userRecord;
    }


    public int updateUserProfile2(userData userRecord, int type){

        String insetStatement = "UPDATE userprofile SET rate = ?, optionalFeedback = ?, CloudStore = ? WHERE userID = ?";

        int returnValue = 1;
        try {
            Connection connection;
            if (type == 1) {
                connection  = this.connectLocal("\\library_feedback.sqlite3");
            }
            else {
                connection  = this.connectcloud("library_feedback");
            }

            PreparedStatement preparedStatement = connection.prepareStatement(insetStatement);
            preparedStatement.setString(1, userRecord.getRate());
            preparedStatement.setString(2, userRecord.getOptionalFeedback());
            preparedStatement.setString(3, userRecord.getCloudStore());
            preparedStatement.setString(4, userRecord.getUserID());
            preparedStatement.executeUpdate();

        }
        catch (SQLException | ClassNotFoundException e) {
            //System.out.println(e.getMessage());
            returnValue = 0;
        }
        return (returnValue);
    }

    public int updateFeedback(FeedbackData feedbackData, int type){
        String insetStatement = "";
        int returnValue = 1;

        try {
            Connection connection;
            PreparedStatement preparedStatement;

            insetStatement = "INSERT INTO feedback(fromLibrary,toLibrary,Location,projectID, classID,allLibrary,selectionLibrary,Local) VALUES(?,?,?,?,?,?,?,?)";
            connection  = this.connectLocal("\\library_feedback.sqlite3");
            preparedStatement = connection.prepareStatement(insetStatement);
            preparedStatement.setString(1, feedbackData.getFromLibrary());
            preparedStatement.setString(2, feedbackData.getToLibrary());
            preparedStatement.setInt(3, feedbackData.getLocation());
            preparedStatement.setString(4, feedbackData.getProjectId());
            preparedStatement.setString(5, feedbackData.getClassId());
            preparedStatement.setString(6, feedbackData.getAllLibrary());
            preparedStatement.setString(7, feedbackData.getSelectionLibrary());
            preparedStatement.setInt(8, type);
            preparedStatement.executeUpdate();

            if (type == 0){
                insetStatement = "INSERT INTO library_feedback.feedback(fromLibrary,toLibrary,Location,projectID, classID,allLibrary,selectionLibrary) VALUES(?,?,?,?,?,?,?)";
                connection  = this.connectcloud("library_feedback");
                preparedStatement = connection.prepareStatement(insetStatement);
                preparedStatement.setString(1, feedbackData.getFromLibrary());
                preparedStatement.setString(2, feedbackData.getToLibrary());
                preparedStatement.setInt(3, feedbackData.getLocation());
                preparedStatement.setString(4, feedbackData.getProjectId());
                preparedStatement.setString(5, feedbackData.getClassId());
                preparedStatement.setString(6, feedbackData.getAllLibrary());
                preparedStatement.setString(7, feedbackData.getSelectionLibrary());
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException | ClassNotFoundException e) {
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

            while (resultSet.next()) {
                libraryDataPoint = new LibData();
                libraryDataPoint.setId(resultSet.getInt("id"));
                libraryDataPoint.setLibrary_id(resultSet.getInt("Library_ID"));
                libraryDataPoint.setDomain_id(resultSet.getInt("domain_id"));
                libraryDataPoint.setName(resultSet.getString("name"));
                libraryDataPoint.setRepository(resultSet.getString("repository"));
                libraryDataPoint.setYear(resultSet.getInt("year"));
                libraryDataPoint.setMonth(resultSet.getInt("month"));
                libraryDataPoint.setPackage(resultSet.getString("Package"));
                libraryDataPoint.setPopularity(resultSet.getDouble("popularity"));
                libraryDataPoint.setRelease_frequency(resultSet.getDouble("release_frequency"));
                libraryDataPoint.setIssue_closing_time(resultSet.getDouble("issue_closing_time"));
                libraryDataPoint.setIssue_response_time(resultSet.getDouble("issue_response_time"));
                //libraryDataPoint.setPerformance(resultSet.getDouble("performance"));
                //libraryDataPoint.setSecurity(resultSet.getDouble("security"));
                libraryDataPoint.setBackwards_compatibility(resultSet.getDouble("backwards_compatibility"));

                if (metricLibraryID == libraryDataPoint.getLibrary_id()) {
                    libraryDataPoint.setName(libraryDataPoint.getName() + " \n(Current \n Library)");
                    libraryList.add(0,libraryDataPoint);
                }
                else {
                    libraryList.add(libraryDataPoint);
                }
            }
        }
        catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return (libraryList);
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

            while (resultSet.next()) {
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
        }
        catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        //linear list of libraries with metric values
        return (libraryList);
    }
}

