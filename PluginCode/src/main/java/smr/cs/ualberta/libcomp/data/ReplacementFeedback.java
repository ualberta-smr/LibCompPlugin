package smr.cs.ualberta.libcomp.data;

import java.util.Date;

/**
 * The DataFeedback class is an object to store a feedback object after a user interacts with the plugin
 */

public class ReplacementFeedback {

    private int id;
    private Date action_date;
    private int line_num;
    private String project_name;
    private String class_name;
    private String full_lib_list;
    private int from_library_id;
    private int to_library_id;

    public int getId() { return id; }
    public Date getAction_date() { return action_date; }
    public int getLine_num() { return line_num; }
    public String getProject_name() { return project_name; }
    public String getClass_name() { return class_name; }
    public String getFull_lib_list() { return full_lib_list;}
    public int getFrom_library_id() {return from_library_id;}
    public int getTo_library_id() { return to_library_id; }

    public ReplacementFeedback(int id, Date action_date, int line_num, String project_name, String class_name, String full_lib_list, int from_library_id, int to_library_id) {
        this.id = id;
        this.action_date = action_date;
        this.line_num = line_num;
        this.project_name = project_name;
        this.class_name = class_name;
        this.full_lib_list = full_lib_list;
        this.from_library_id = from_library_id;
        this.to_library_id = to_library_id;
    }

}
