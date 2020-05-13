package org.intellij.sdk.editor;

import java.util.Date;

/**
 * The DataFeedback class is an object to store a feedback object after a user interacts with the plugin
 */

public class DataFeedback {
    private String fromLibrary;
    private String toLibrary;
    private Date dateReplace;
    private int location;
    private String projectId;
    private String classId;
    private String allLibrary;
    private String selectionLibrary;
    private String UserID;
    private int local;

    public DataFeedback(String fromLibrary, String toLibrary, int location, String projectId, String classId, String allLibrary, String selectionLibrary, String UserID, int local) {
        this.fromLibrary = fromLibrary;
        this.toLibrary = toLibrary;
        this.dateReplace = dateReplace;
        this.location = location;
        this.projectId = projectId;
        this.classId = classId;
        this.allLibrary = allLibrary;
        this.selectionLibrary = selectionLibrary;
        this.UserID = UserID;
        this.local = local;
    }

    public String getFromLibrary() { return fromLibrary; }
    public String getToLibrary() { return toLibrary; }
    public String getProjectId() { return projectId; }
    public String getClassId() { return classId; }
    public String getAllLibrary() { return allLibrary; }
    public String getSelectionLibrary() { return selectionLibrary; }
    public String getUserID() { return UserID; }
    public Date getDateReplace() { return dateReplace; }
    public int getLocation() { return location; }
    public int getLocal() { return local; }

}
