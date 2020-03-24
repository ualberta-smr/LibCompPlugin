package org.intellij.sdk.editor;


public class userData {

    public userData() {
    }

    private String ID;

    private String userID;
    private String rate;
    private String optionalFeedback;
    private String Project1 ;
    private String Project2 ;
    private String Project3 ;
    private String Project4 ;
    private String Project5 ;
    private String Occupation ;
    private String Team1;
    private String Team2;
    private String Team3;
    private String Team4;
    private String Programming;
    private String JavaSkills;
    private String CloudStore;

    public void setID(String ID) {
        this.ID = ID;
    }
    public void setCloudStore(String CloudStore) {
        this.CloudStore = CloudStore;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public void setRate(String rate) {
        this.rate = rate;
    }
    public void setOptionalFeedback(String optionalFeedback) {
        this.optionalFeedback = optionalFeedback;
    }
    public void setProject1(String project1) {
        Project1 = project1;
    }
    public void setProject2(String project2) {
        Project2 = project2;
    }
    public void setProject3(String project3) {
        Project3 = project3;
    }
    public void setProject4(String project4) {
        Project4 = project4;
    }
    public void setProject5(String project5) {
        Project5 = project5;
    }
    public void setOccupation(String occupation) {
        Occupation = occupation;
    }
    public void setTeam1(String team1) {
        Team1 = team1;
    }
    public void setTeam2(String team2) {
        Team2 = team2;
    }
    public void setTeam3(String team3) {
        Team3 = team3;
    }
    public void setTeam4(String team4) {
        Team4 = team4;
    }
    public void setProgramming(String programming) {
        Programming = programming;
    }
    public void setJavaSkills(String javaSkills) {
        JavaSkills = javaSkills;
    }

    public String getID() {
        return ID;
    }
    public String getUserID() {
        return userID;
    }
    public String getRate() {
        return rate;
    }
    public String getOptionalFeedback() {
        return optionalFeedback;
    }
    public String getProject1() {
        return Project1;
    }
    public String getProject2() {
        return Project2;
    }
    public String getProject3() {
        return Project3;
    }
    public String getProject4() {
        return Project4;
    }
    public String getProject5() {
        return Project5;
    }
    public String getOccupation() {
        return Occupation;
    }
    public String getTeam1() {
        return Team1;
    }
    public String getTeam2() {
        return Team2;
    }
    public String getTeam3() {
        return Team3;
    }
    public String getTeam4() {
        return Team4;
    }
    public String getProgramming() {
        return Programming;
    }
    public String getJavaSkills() { return JavaSkills; }
    public String getCloudStore() { return CloudStore; }

    public userData(String ID, String userID, String rate, String optionalFeedback, String project1, String project2, String project3, String project4, String project5, String occupation, String team1, String team2, String team3, String team4, String programming, String javaSkills) {
        this.ID = ID;
        this.userID = userID;
        this.rate = rate;
        this.optionalFeedback = optionalFeedback;
        Project1 = project1;
        Project2 = project2;
        Project3 = project3;
        Project4 = project4;
        Project5 = project5;
        Occupation = occupation;
        Team1 = team1;
        Team2 = team2;
        Team3 = team3;
        Team4 = team4;
        Programming = programming;
        JavaSkills = javaSkills;
    }
}

