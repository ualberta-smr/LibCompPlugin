package org.intellij.sdk.editor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
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

    /**
     * The selectAllLibraries method will query the database with a given library to get all the other Libraries within the same domain
     * @param librarySelected is the library selected by the user to be replaced with other similar libraries
     * @return A list of all the possibly library replacements is returned
     */
    public ArrayList<String> selectAllLibraries(String librarySelected){
        String sql = "select Replaceterm from Librarycomparion_suggest where keyterm = \"" + librarySelected +"\"";
        ArrayList<String> Terms;
        Terms = new ArrayList<String>();

        try {
            Connection connection = this.connect();
            Statement statement  = connection.createStatement();
            ResultSet resultSet    = statement.executeQuery(sql);

            while (resultSet.next()) {
                System.out.println(resultSet.getString("Replaceterm") );
                Terms.add(resultSet.getString("Replaceterm") );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return (Terms);
    }


}
