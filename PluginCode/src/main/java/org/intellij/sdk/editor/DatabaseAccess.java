package org.intellij.sdk.editor;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.mysql.cj.jdbc.MysqlDataSource;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

/**
 * The DatabaseAccess class is where all connections to the databases are made
 */

public class DatabaseAccess {

    private static Session session = null;
    private int VersionNo;
    private int VersionMonth;
    private int VersionYear;

    /**
     * Until we figure out how to access the server DB using REST API, I am using my own acount information to connect
     * I have changed all the sensitive information to placeholders so that I do not share my information on github
     */
    private Connection connectcloud(String dbName) throws ClassNotFoundException, SQLException {
        Connection connection = null;
        try {
            String sshHost = "smr.cs.ualberta.ca";
            String sshuser = "relhajj";
            //I did not put my private key on github, I am just using it for my own testing purposes until the API's are set up
            String SshKeyFilepath = System.getenv("APPDATA")+"\\LibComp\\privateKey.ppk";
            //String SshKeyFilepath = PathManager.getPluginsPath()+"\\Library_Comparison\\lib\\privateKey.ppk";
            String dbuserName = "USERNAME";
            String dbpassword = "PASSWORD";
            int localPort = 3306; // any free port can be used
            String localSSHUrl = "localhost";

            // Establish the SSH connection
            java.util.Properties config = new java.util.Properties();
            JSch jsch = new JSch();
            session = jsch.getSession(sshuser, sshHost, 22);
            jsch.addIdentity(SshKeyFilepath);
            config.put("StrictHostKeyChecking", "no");
            config.put("ConnectionAttempts", "3");
            session.setConfig(config);
            session.connect();

            // Establish the DB connection
            int forwardedPort = session.setPortForwardingL(0, localSSHUrl, localPort);
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setServerName(localSSHUrl);
            dataSource.setPortNumber(forwardedPort);
            dataSource.setUser(dbuserName);
            dataSource.setAllowMultiQueries(true);
            dataSource.setPassword(dbpassword);
            dataSource.setDatabaseName(dbName);
            dataSource.setServerTimezone("UTC");
            connection = dataSource.getConnection();
        }
        catch (SQLException | JSchException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }


    /**
     *  I am keeping this function for now but have commented it out for the time being
     */
/*    private Connection connectcloud(String dbName) throws ClassNotFoundException, SQLException {

        Class.forName("com.mysql.cj.jdbc.Driver");
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
    }*/


    /**
     *  connectLocal function connects to local database
     *  There are 2 options for the "filepath" and one is commented out, one is for local testing while the other is for when the plugin is packages
     */
    private Connection connectLocal(String dbName) throws ClassNotFoundException, SQLException {
        String filePath = System.getenv("APPDATA")+"\\LibComp";
        //String filePath = PathManager.getPluginsPath()+"\\Library_Comparison\\lib";
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

    public void updateVersionNumberLocal(int cloudVer,int year, int month) {
        String sql = "UPDATE Properties SET VersionNo = " + cloudVer + " ,VersionYear = " + year + " ,VersionMonth = " + month + ";";
        try {
            Connection connection = this.connectLocal("\\db.sqlite3");
            Statement statement = connection.createStatement();
            int noOfRecords= statement.executeUpdate(sql);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteLocalData() {
        int localVer = 0;
        String sqlData = "DELETE  from librarycomparison_data;";
        String sqlCharts = "DELETE  from librarycomparison_charts;";
        try {
            Connection connection = this.connectLocal("\\db.sqlite3");
            Statement statement = connection.createStatement();
            localVer= statement.executeUpdate(sqlData);
            localVer= statement.executeUpdate(sqlCharts);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public int updateVersionCloud(String sql) {
        try {
            Connection connection = this.connectcloud("libcomp");
            Statement statement  = connection.createStatement();
            ResultSet resultSet    = statement.executeQuery(sql);

            while (resultSet.next()) {
                this.VersionNo = resultSet.getInt("VersionNo") ;
                this.VersionMonth = resultSet.getInt("VersionMonth") ;
                this.VersionYear = resultSet.getInt("VersionYear") ;
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return (this.VersionNo);
    }

    public void updateNewChartRecord (DataCharts DataRecord) {

        String insetStatement = "INSERT INTO librarycomparison_charts (metric_DomainID, metric_line, Chart)VALUES (?,?,?);";

        try {
            Connection connection  = this.connectLocal("\\db.sqlite3");
            PreparedStatement preparedStatement = connection.prepareStatement(insetStatement);
            preparedStatement.setInt(1, DataRecord.getMetric_DomainID());
            preparedStatement.setInt(2, DataRecord.getMetric_line());
            preparedStatement.setBytes(3, DataRecord.getChart());
            preparedStatement.executeUpdate();
        }
        catch (SQLException | ClassNotFoundException e) {
            //System.out.println(e.getMessage());
        }
    }


    public void updateNewDataRecord (DataLibrary DataRecord) {
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
        }
    }

    public void UpdateNewChart(ArrayList <DataCharts> ChartList) {
        for (int i = 0; i < ChartList.size(); i++) {
            updateNewChartRecord(ChartList.get(i));
        }
    }

    public void UpdateNewData(ArrayList <DataLibrary> libraryList) {
        for (int i = 0; i < libraryList.size(); i++) {
            updateNewDataRecord(libraryList.get(i));
        }
    }

    /**
     * The updateVersionData function is for updating the data from the server to the local DB
     * This is triggered if the version number on the server > the version number locally
     */
    public int updateVersionData() {
        int returnValue = 1;
        int localVersion = 10;
        int cloudVersion = 20;

        //First Read Version Number from local DB, and Store in localVersion
        String sql = "SELECT VersionNo, VersionMonth, VersionYear from Properties;";

        //Then Read Version Number from cloud DB, and Store in cloudVersion
        localVersion= updateVersionLocal(sql);
        cloudVersion= updateVersionCloud(sql);

        //Check if data must be updated, read data from Cloud, and store in local DB
        if  (localVersion < cloudVersion) {
            ArrayList <DataLibrary> libraryList = GetVersionPerformanceValues(this.VersionYear, this.VersionMonth);
            ArrayList <DataCharts> ChartList = GetVersionChartValues(this.VersionYear, this.VersionMonth);

            if (libraryList.size()>0) {
                deleteLocalData();
                UpdateNewData(libraryList);
                UpdateNewChart(ChartList);
                updateVersionNumberLocal(cloudVersion,this.VersionYear, this.VersionMonth );
            }
        }
        return (returnValue);
    }

    public DataUser ReadUserProfile() {
        DataUser userRecord = null;
        String userID;
        String  sql = "select  rate, optionalFeedback, CloudStore, SendAllCloud, Project1, Project2, Project3 , Project4 , Project5 , Occupation , Team1 , Team2 , Team3 , Team4 , Programming , JavaSkills, userID from userprofile";

        try {
            Connection connection  = this.connectLocal("\\library_feedback.sqlite3");
            Statement statement  = connection.createStatement();
            ResultSet resultSet    = statement.executeQuery(sql);

            while (resultSet.next()) {
                userRecord = new DataUser();
                userID = resultSet.getString("userID");
                if (userID.equals("NEWUSER"))
                {
                    UUID uuid = UUID.randomUUID();
                    userID= uuid.toString();
                }
                userRecord.setUserID(userID);
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
                userRecord.setSendAllCloud(resultSet.getString("SendAllCloud"));
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

        public int updateUserProfile(DataUser userRecord){

        String insetStatement = "UPDATE userprofile SET Project1 = ?, Project2 = ?, Project3 = ?, Project4 = ?, Project5 = ?, Occupation = ?, Team1 = ?, Team2 = ?, Team3 = ?, Team4 = ?, Programming = ?, JavaSkills = ?, CloudStore = ?, userID = ?";

        int returnValue = 1;
        try {
            Connection connection;
                connection  = this.connectLocal("\\library_feedback.sqlite3");

            PreparedStatement preparedStatement = connection.prepareStatement(insetStatement);
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
            if (userRecord.getSendAllCloud().equals("1"))  {sendtoCloud();}
        }
        catch (SQLException | ClassNotFoundException e) {
            //System.out.println(e.getMessage());
            returnValue = 0;
        }
        return (returnValue);
    }

    public int CloudUserRecord(DataUser userRecord) {
        String insetStatement = "";
        String updateStatement = "";

        int returnValue = 1;

        try {
            Connection connection2;
            PreparedStatement preparedStatementInsert,preparedStatementUpdate;
            insetStatement = "INSERT INTO library_feedback.userprofile(Project1, Project2, Project3 , Project4 , Project5 , Occupation , Team1 , Team2 , Team3 , Team4 , Programming , JavaSkills, rate, optionalFeedback, CloudStore, SendAllCloud,userID) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            updateStatement = "UPDATE library_feedback.userprofile SET Project1=?, Project2=?, Project3=? , Project4=? , Project5=? , Occupation=? , Team1=? , Team2=? , Team3=? , Team4=? , Programming=? , JavaSkills=?, rate=?, optionalFeedback=?, CloudStore=?, SendAllCloud=? where userID=?";
            connection2  = this.connectcloud("library_feedback");

            preparedStatementUpdate = connection2.prepareStatement(updateStatement);

            preparedStatementUpdate.setString(1, userRecord.getProject1());
            preparedStatementUpdate.setString(2, userRecord.getProject2());
            preparedStatementUpdate.setString(3, userRecord.getProject3());
            preparedStatementUpdate.setString(4, userRecord.getProject4());
            preparedStatementUpdate.setString(5, userRecord.getProject5());
            preparedStatementUpdate.setString(6, userRecord.getOccupation());
            preparedStatementUpdate.setString(7, userRecord.getTeam1());
            preparedStatementUpdate.setString(8, userRecord.getTeam2());
            preparedStatementUpdate.setString(9, userRecord.getTeam3());
            preparedStatementUpdate.setString(10, userRecord.getTeam4());
            preparedStatementUpdate.setString(11, userRecord.getProgramming());
            preparedStatementUpdate.setString(12, userRecord.getJavaSkills());
            preparedStatementUpdate.setString(13, userRecord.getRate());
            preparedStatementUpdate.setString(14, userRecord.getOptionalFeedback());
            preparedStatementUpdate.setString(15, userRecord.getCloudStore());
            preparedStatementUpdate.setString(16, userRecord.getSendAllCloud());
            preparedStatementUpdate.setString(17, userRecord.getUserID());

            int noOfRecords = preparedStatementUpdate.executeUpdate();
            if (noOfRecords < 1)
            {
                preparedStatementInsert = connection2.prepareStatement(insetStatement);

                preparedStatementInsert.setString(1, userRecord.getProject1());
                preparedStatementInsert.setString(2, userRecord.getProject2());
                preparedStatementInsert.setString(3, userRecord.getProject3());
                preparedStatementInsert.setString(4, userRecord.getProject4());
                preparedStatementInsert.setString(5, userRecord.getProject5());
                preparedStatementInsert.setString(6, userRecord.getOccupation());
                preparedStatementInsert.setString(7, userRecord.getTeam1());
                preparedStatementInsert.setString(8, userRecord.getTeam2());
                preparedStatementInsert.setString(9, userRecord.getTeam3());
                preparedStatementInsert.setString(10, userRecord.getTeam4());
                preparedStatementInsert.setString(11, userRecord.getProgramming());
                preparedStatementInsert.setString(12, userRecord.getJavaSkills());
                preparedStatementInsert.setString(13, userRecord.getRate());
                preparedStatementInsert.setString(14, userRecord.getOptionalFeedback());
                preparedStatementInsert.setString(15, userRecord.getCloudStore());
                preparedStatementInsert.setString(16, userRecord.getSendAllCloud());
                preparedStatementInsert.setString(17, userRecord.getUserID());
                preparedStatementInsert.executeUpdate();
            }
        }
        catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            returnValue = 0;
        }
        return (returnValue);
    }
    public int CloudFeedbackRecord(DataFeedback dataFeedback){
        String insetStatement = "";
        int returnValue = 1;

        try {
            Connection connection2;
            PreparedStatement preparedStatement;
            insetStatement = "INSERT INTO library_feedback.feedback(fromLibrary,toLibrary,Location,projectID, classID,allLibrary,selectionLibrary, UserID) VALUES(?,?,?,?,?,?,?,?)";
            connection2  = this.connectcloud("library_feedback");
            preparedStatement = connection2.prepareStatement(insetStatement);
            preparedStatement.setString(1, dataFeedback.getFromLibrary());
            preparedStatement.setString(2, dataFeedback.getToLibrary());
            preparedStatement.setInt(3, dataFeedback.getLocation());
            preparedStatement.setString(4, dataFeedback.getProjectId());
            preparedStatement.setString(5, dataFeedback.getClassId());
            preparedStatement.setString(6, dataFeedback.getAllLibrary());
            preparedStatement.setString(7, dataFeedback.getSelectionLibrary());
            preparedStatement.setString(8, dataFeedback.getUserID());
            preparedStatement.executeUpdate();
        }
        catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            returnValue = 0;
        }
        return (returnValue);
    }


    public void ResetCloudRecords() {

        String sql = "UPDATE feedback SET local = 1; ";
        try {
            Connection connection  = this.connectLocal("\\library_feedback.sqlite3");
            Statement statement = connection.createStatement();
            int localVer= statement.executeUpdate(sql);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendtoCloud() {
        DataUser userRecord = null;
        String sql = "select fromLibrary,toLibrary,Location,projectID, classID,allLibrary,selectionLibrary,Local, UserID from feedback where local = 0";
        try {
            Connection connection  = this.connectLocal("\\library_feedback.sqlite3");
            Statement statement  = connection.createStatement();
            ResultSet resultSet    = statement.executeQuery(sql);
            while (resultSet.next()) {
                DataFeedback dataFeedbackPoint = new DataFeedback(resultSet.getString("fromLibrary"),resultSet.getString("toLibrary"), resultSet.getInt("Location"),resultSet.getString("projectID"),resultSet.getString("classID"),resultSet.getString("allLibrary"),resultSet.getString("selectionLibrary"),resultSet.getString("UserID"),resultSet.getInt("local") );
                CloudFeedbackRecord(dataFeedbackPoint);
            }
            ResetCloudRecords();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        // send user record to DB
        String sqlUser = "select  Project1, Project2, Project3 , Project4 , Project5 , Occupation , Team1 , Team2 , Team3 , Team4 , Programming , JavaSkills, userID, rate, optionalFeedback, CloudStore, SendAllCloud from userprofile";
        try {
            Connection connection  = this.connectLocal("\\library_feedback.sqlite3");
            Statement statement  = connection.createStatement();
            ResultSet resultSet    = statement.executeQuery(sqlUser);
            while (resultSet.next()) {
                userRecord = new DataUser();

                userRecord.setUserID(resultSet.getString("userID"));
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
                userRecord.setRate(resultSet.getString("rate"));
                userRecord.setOptionalFeedback(resultSet.getString("optionalFeedback"));
                userRecord.setCloudStore(resultSet.getString("CloudStore"));
                userRecord.setSendAllCloud(resultSet.getString("SendAllCloud"));
                CloudUserRecord(userRecord);
            }

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public int updateUserProfile2(DataUser userRecord){

        String insetStatement = "UPDATE userprofile SET rate = ?, optionalFeedback = ?, CloudStore = ?, SendAllCloud = ?, userID = ?";
        int returnValue = 1;
        try {
            Connection connection;
                connection  = this.connectLocal("\\library_feedback.sqlite3");

            PreparedStatement preparedStatement = connection.prepareStatement(insetStatement);
            preparedStatement.setString(1, userRecord.getRate());
            preparedStatement.setString(2, userRecord.getOptionalFeedback());
            preparedStatement.setString(3, userRecord.getCloudStore());
            preparedStatement.setString(4, userRecord.getSendAllCloud());
            preparedStatement.setString(5, userRecord.getUserID());
            preparedStatement.executeUpdate();
            if (userRecord.getSendAllCloud().equals("1"))  {sendtoCloud();}
        }
        catch (SQLException | ClassNotFoundException e) {
            //System.out.println(e.getMessage());
            returnValue = 0;
        }
        return (returnValue);
    }

    public int updateFeedback(DataFeedback dataFeedback){
        String insetStatement = "";
        int returnValue = 1;

        try {
            Connection connection;
            Connection connection2;

            PreparedStatement preparedStatement;

            insetStatement = "INSERT INTO feedback(fromLibrary,toLibrary,Location,projectID, classID,allLibrary,selectionLibrary,Local,UserID) VALUES(?,?,?,?,?,?,?,?,?)";
            connection  = this.connectLocal("\\library_feedback.sqlite3");
            preparedStatement = connection.prepareStatement(insetStatement);
            preparedStatement.setString(1, dataFeedback.getFromLibrary());
            preparedStatement.setString(2, dataFeedback.getToLibrary());
            preparedStatement.setInt(3, dataFeedback.getLocation());
            preparedStatement.setString(4, dataFeedback.getProjectId());
            preparedStatement.setString(5, dataFeedback.getClassId());
            preparedStatement.setString(6, dataFeedback.getAllLibrary());
            preparedStatement.setString(7, dataFeedback.getSelectionLibrary());
            preparedStatement.setInt(8, dataFeedback.getLocal());
            preparedStatement.setString(9, dataFeedback.getUserID());

            preparedStatement.executeUpdate();

            if (dataFeedback.getLocal() == 1){
                insetStatement = "INSERT INTO library_feedback.feedback(fromLibrary,toLibrary,Location,projectID, classID,allLibrary,selectionLibrary,UserID) VALUES(?,?,?,?,?,?,?,?)";
                connection2  = this.connectcloud("library_feedback");
                preparedStatement = connection2.prepareStatement(insetStatement);
                preparedStatement.setString(1, dataFeedback.getFromLibrary());
                preparedStatement.setString(2, dataFeedback.getToLibrary());
                preparedStatement.setInt(3, dataFeedback.getLocation());
                preparedStatement.setString(4, dataFeedback.getProjectId());
                preparedStatement.setString(5, dataFeedback.getClassId());
                preparedStatement.setString(6, dataFeedback.getAllLibrary());
                preparedStatement.setString(7, dataFeedback.getSelectionLibrary());
                preparedStatement.setString(8, dataFeedback.getUserID());

                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            returnValue = 0;
        }
        return (returnValue);
    }

    public ArrayList <DataLibrary> GetPerformanceValues(int  metricDomain, int metricLibraryID, int metricyear, int metricmonth){

        String  sql = "select id, domain_id, repository,library_id, name, year, month, Package, domain_id, popularity, release_frequency, issue_closing_time, issue_response_time, backwards_compatibility " +
                " from librarycomparison_data where domain_id = "+metricDomain+" and year = "+metricyear+" and month = " + metricmonth;

        DataLibrary libraryDataPoint;
        ArrayList <DataLibrary> libraryList = new ArrayList<DataLibrary> ();

        try {
            Connection connection = this.connectLocal("\\db.sqlite3");
            Statement statement  = connection.createStatement();
            ResultSet resultSet    = statement.executeQuery(sql);

            while (resultSet.next()) {
                libraryDataPoint = new DataLibrary();
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

    public Image ReadCharts(int metric_year, int metric_month, int metric_DomainID, int metric_line){
        Image img=null;
        String sql = "select Chart from librarycomparison_charts where  metric_DomainID = " + metric_DomainID + " and metric_line = " + metric_line;
        try {
            Connection connection = this.connectLocal("\\db.sqlite3");
            Statement statement  = connection.createStatement();
            ResultSet resultSet    = statement.executeQuery(sql);

            while (resultSet.next()) {
                byte[] imgArr=resultSet.getBytes("Chart");
                img= Toolkit.getDefaultToolkit().createImage(imgArr);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return (img);
    }

    public ArrayList <DataCharts> GetVersionChartValues(int metric_year, int metric_month){

        String sql = "select metric_year, metric_month, metric_DomainID, metric_line, Chart from librarycomparison_charts where metric_year = "+ metric_year + " and metric_month = "+metric_month ;
        DataCharts dataChartsPoint;
        ArrayList <DataCharts> libraryList = new ArrayList<DataCharts> ();

        try {
            Connection connection = this.connectcloud("libcomp");
            Statement statement  = connection.createStatement();
            ResultSet resultSet    = statement.executeQuery(sql);

            while (resultSet.next()) {
                dataChartsPoint = new DataCharts();
                dataChartsPoint.setMetric_DomainID(resultSet.getInt("metric_DomainID"));
                dataChartsPoint.setMetric_line(resultSet.getInt("metric_line"));
                dataChartsPoint.setChart(resultSet.getBytes("Chart"));
                libraryList.add(dataChartsPoint);
            }
        }
        catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return (libraryList);
    }

    public ArrayList <DataLibrary> GetVersionPerformanceValues(int metric_year, int metric_month){

        String  sql = "select id, domain_id, repository, library_id, name, year, month, Package, domain_id, popularity, release_frequency, issue_closing_time, issue_response_time, backwards_compatibility " +
                " from librarycomparison_data where year = "+ metric_year + " and month = "+metric_month;

        DataLibrary libraryDataPoint;
        ArrayList <DataLibrary> libraryList = new ArrayList<DataLibrary> ();

        try {
            Connection connection = this.connectcloud("libcomp");
            Statement statement  = connection.createStatement();
            ResultSet resultSet    = statement.executeQuery(sql);

            while (resultSet.next()) {
                libraryDataPoint = new DataLibrary();
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

