package org.intellij.sdk.editor;

import java.util.Date;

public class FeedbackData {
    private String fromLibrary;
    private String toLibrary;
    private Date dateReplace;
    private int location;
    private String projectId;
    private String classId;
    private String allLibrary;
    private String selectionLibrary;

    public FeedbackData(String fromLibrary, String toLibrary, int location, String projectId, String classId, String allLibrary, String selectionLibrary  ) {
        this.fromLibrary = fromLibrary;
        this.toLibrary = toLibrary;
        this.dateReplace = dateReplace;
        this.location = location;
        this.projectId = projectId;
        this.classId = classId;
        this.allLibrary = allLibrary;
        this.selectionLibrary = selectionLibrary;
    }

    public String getFromLibrary() { return fromLibrary; }
    public String getToLibrary() { return toLibrary; }
    public String getProjectId() { return projectId; }
    public String getClassId() { return classId; }
    public String getAllLibrary() { return allLibrary; }
    public String getSelectionLibrary() { return selectionLibrary; }
    public Date getDateReplace() { return dateReplace; }
    public int getLocation() { return location; }

}
