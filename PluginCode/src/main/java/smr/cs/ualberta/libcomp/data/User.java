package smr.cs.ualberta.libcomp.data;

/**
 * The DataUser class is an object to store information on a user profile
 * This is based on the questions asked in the user profile dialog accessable via the Tools menu
 */

public class User {

    public User() {
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
    private String SendAllCloud;

    public void setID(String ID) {
        this.ID = ID;
    }
    public void setCloudStore(String CloudStore) {
        this.CloudStore = CloudStore;
    }
    public void setSendAllCloud(String SendAllCloud) {
        this.SendAllCloud = SendAllCloud;
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
    public String getSendAllCloud() { return SendAllCloud; }

}

