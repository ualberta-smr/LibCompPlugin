package smr.cs.ualberta.libcomp;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.WindowManager;
import com.jcraft.jsch.Session;
import org.apache.commons.io.FileUtils;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;
import java.io.FileNotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;
import smr.cs.ualberta.libcomp.data.ReplacementFeedback;
import smr.cs.ualberta.libcomp.data.Library;
import smr.cs.ualberta.libcomp.data.User;
import java.io.File;

import static java.net.HttpURLConnection.HTTP_CREATED;

/**
 * The DatabaseAccess class is where all rest api calls to the databases are made
 */

public class DatabaseAccess {

    private static Session session = null;
    private int userid = 0;
    private String filePath = PathManager.getPluginsPath()+"\\Library_Comparison\\lib";
    private String feedbackUrllink = "https://smr.cs.ualberta.ca/comparelibraries/api/pluginfeedback/";

    public ArrayList<String> selectJsonAllLibraries(String librarySelected) throws IOException
    {
        String filePath = this.filePath + "\\allLibraries.json";
        ArrayList<String> domainLibraries = new ArrayList<String>();;
        long library_id = 0;
        long domain_id =0 ;
        String domain_name = "";
        String Package = "";

        File file = new File(filePath);
        if (file.exists()) {
            String content = FileUtils.readFileToString(file, "utf-8");
            JSONObject obj = new JSONObject(content);
            JSONArray libraries = obj.getJSONArray("Libraries");

            for (int i = 0; i < libraries.length(); i++) {
                JSONObject jsonObj = (JSONObject) libraries.get(i);
                if (jsonObj.has("id"))
                    library_id = jsonObj.getLong("id");
                if (jsonObj.has("domain"))
                    domain_id = jsonObj.getLong("domain");
                if (jsonObj.has("domain_name"))
                    domain_name = jsonObj.getString("domain_name");
                if (jsonObj.has("package"))
                    Package = jsonObj.getString("package");


                boolean isFound1 = Package.toLowerCase().contains(librarySelected.toLowerCase());
                boolean isFound2 = librarySelected.toLowerCase().contains(Package.toLowerCase());
                if (isFound1 || isFound2) {
                        domainLibraries.add(Long.toString(library_id));
                        domainLibraries.add(Long.toString(domain_id));
                        domainLibraries.add(domain_name);
                }

            }
        }
        return domainLibraries;
    }

    public String getLatestMetricDate() throws IOException  {
        String cloudVer = "";
        String linkURL = "https://smr.cs.ualberta.ca/comparelibraries/api/metrics/?format=json&latestdate=";

        HttpURLConnection connection = (HttpURLConnection) new URL(linkURL).openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
            InputStream response = connection.getInputStream();
            InputStream in = new BufferedInputStream(response);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                cloudVer = line;
            }
        }
        int loc = cloudVer.indexOf(":");
        if (loc > -1)
            cloudVer = cloudVer.substring(loc+2, cloudVer.length()-3);
        return cloudVer;
    }

    public String checkLocalDate() throws IOException {

        String filePath = this.filePath +"\\Version.json";
        String datelocal = "NewUser";
        File file = new File(filePath);

        if (file.exists()) {
            String content = FileUtils.readFileToString(file, "utf-8");
            JSONObject obj = new JSONObject(content);
            JSONArray localDate = obj.getJSONArray("version");
            int i = 0;
            boolean found = false;
            while (i < localDate.length() && !(found)) {
                JSONObject jsonObj = (JSONObject) localDate.get(i);
                if (jsonObj.has("created_on")) {
                    datelocal = jsonObj.getString("created_on");
                }
                i++;
            }
        }
        return datelocal;
    }

    public void getJson(String linkURL, String filePath, String suffix) throws IOException {

        File jsonFile = new File(filePath);
        FileOutputStream fOuts = new FileOutputStream(jsonFile);
        jsonFile.createNewFile();

        StringBuilder result = new StringBuilder();
        String line;
        result.append(suffix);
        HttpURLConnection connection = (HttpURLConnection) new URL(linkURL).openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED){

            InputStream response = connection.getInputStream();
            InputStream in = new BufferedInputStream(response);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            result.append("}");
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOuts);
            myOutWriter.append(result);
            myOutWriter.close();
        }
    }

    public void getDataRestApi() throws IOException {
        String suffixLocal = this.filePath + "\\";
        String url = "https://smr.cs.ualberta.ca/comparelibraries/api/libraries/?format=json";
        String filePath = suffixLocal + "allLibraries.json";
        getJson(url, filePath, "{\"Libraries\":");

        url = "https://smr.cs.ualberta.ca/comparelibraries/api/charts/?format=json";
        filePath = suffixLocal + "allCharts.json";
        getJson(url, filePath, "{\"Charts\":");

        url = "https://smr.cs.ualberta.ca/comparelibraries/api/metrics/?format=json&latestdate=";
        filePath = suffixLocal + "Version.json";
        getJson(url, filePath, "{\"version\":");
    }

    public int updateMetricsData() throws IOException {
        int returnValue = 1;
        String localVersion = "";
        String smrServerVersion = "";

        try {
            localVersion= checkLocalDate();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        smrServerVersion= getLatestMetricDate();

        if  (!(localVersion.equals(smrServerVersion)) ) {
            getDataRestApi();
        }
        return returnValue;
    }

    public User readUserProfile() {

        String filePath = this.filePath + "\\user.json";
        User userRecord = null;
        File file = new File(filePath);

        if (!file.exists()){
            userRecord = new User();
            String tempShortUserName = UUID.randomUUID().toString().substring(0,14);
            userRecord.setUserID(tempShortUserName);
            userRecord.setProject1("1");
            userRecord.setProject2("0");
            userRecord.setProject3("0");
            userRecord.setProject4("0");
            userRecord.setProject5("0");
            userRecord.setOccupation("0");
            userRecord.setTeam1("1");
            userRecord.setTeam2("0");
            userRecord.setTeam3("0");
            userRecord.setTeam4("0");
            userRecord.setProgramming("0");
            userRecord.setJavaSkills("0");
            userRecord.setRate("0");
            userRecord.setOptionalFeedback("Enter your feedback");
            userRecord.setCloudStore("0");
            userRecord.setSendAllCloud("0");
        }
        else {
            String content = null;
            try {
                content = FileUtils.readFileToString(file, "utf-8");
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject obj = new JSONObject(content);

            userRecord = new User();
            userRecord.setUserID(obj.getString("username"));
            userRecord.setProject1("0");
            userRecord.setProject2("0");
            userRecord.setProject3("0");
            userRecord.setProject4("0");
            userRecord.setProject5("0");
            userRecord.setTeam1("0");
            userRecord.setTeam2("0");
            userRecord.setTeam3("0");
            userRecord.setTeam4("0");

            JSONArray projectsArray = obj.getJSONArray("projects");
            for(int i=0;i<projectsArray.length();i++){
                switch ((int) projectsArray.get(i)) {
                    case 1:
                        userRecord.setProject1("1");
                        break;
                    case 2:
                        userRecord.setProject2("1");
                        break;
                    case 3:
                        userRecord.setProject3("1");
                        break;
                    case 4:
                        userRecord.setProject4("1");
                        break;
                    case 5:
                        userRecord.setProject5("1");
                        break;
                }
            }

            JSONArray teamsArray = obj.getJSONArray("teams");
            for(int i=0;i<teamsArray.length();i++){
                switch ((int) teamsArray.get(i)) {
                    case 1:
                        userRecord.setTeam1("1");
                        break;
                    case 2:
                        userRecord.setTeam2("1");
                        break;
                    case 3:
                        userRecord.setTeam3("1");
                        break;
                    case 4:
                        userRecord.setTeam4("1");
                        break;
                }
            }
            userRecord.setOccupation(obj.getString("occupation"));
            userRecord.setProgramming(String.valueOf(obj.getLong("programming_skills")));
            userRecord.setJavaSkills(String.valueOf(obj.getLong("java_skills")));
            userRecord.setOptionalFeedback(obj.getString("optional_feedback"));
            userRecord.setRate(String.valueOf(obj.getLong("plugin_rating")));
            userRecord.setSendAllCloud(String.valueOf(obj.getLong("Send_Feedback")));
            userRecord.setCloudStore(String.valueOf(obj.getLong("Accept")));

        }
        return userRecord;
    }


    public String getUserToken() throws IOException {
        String tokenValue = "NoToken";
        String filePath = this.filePath + "\\token.json";
        File file = new File(filePath);

        if (file.exists()) {
            String content = FileUtils.readFileToString(file, "utf-8");
            JSONObject obj = new JSONObject(content);

            if (obj.has("token"))
                tokenValue = "Token " + obj.getString("token");
            if (obj.has("id"))
                this.userid = obj.getInt("id");
        }
        return tokenValue;
    }

    public void storeLocalToken(String jsonData) throws IOException {

        String filePath = this.filePath + "\\token.json";
        JSONObject json = new JSONObject(jsonData);
        String tokenValue = (String) json.get("token");
        int userID = (int) json.get("id");
        File myFile = new File(filePath);

            FileOutputStream fOuts = new FileOutputStream(myFile);
            myFile.createNewFile();
            JSONObject obj = new JSONObject();
            obj.put("token", tokenValue);
            obj.put("id", userID);
            String result = obj.toString();
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOuts);
            myOutWriter.append(result);
            myOutWriter.close();

    }

    public boolean createUser(String urlStr, String jsonBodyStr) throws IOException {
        boolean returnValue = false;
        URL url = new URL(urlStr);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-Type", "application/json");  //; utf-8
        httpURLConnection.setRequestProperty("Accept", "application/json");

        try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
            outputStream.write(jsonBodyStr.getBytes());
            outputStream.flush();
        }

        try {
            if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        returnValue = true;
                        storeLocalToken(line);

                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return  returnValue;
    }

    public boolean updateUser(String urlStr, String jsonBodyStr, String tokenValue) throws IOException {

        boolean returnValue = false;
        URL url = new URL(urlStr);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("PUT");
        httpURLConnection.setRequestProperty("Content-Type", "application/json");  //; utf-8
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setRequestProperty("Authorization", tokenValue);

        try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
            outputStream.write(jsonBodyStr.getBytes());
            outputStream.flush();
        }

        try {
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                returnValue = true;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    public boolean sendFeedback(String jsonBodyStr, String tokenValue) throws IOException {

        // This function will send one record of feedback to the server.
        boolean returnValue = false;
        URL url = new URL(this.feedbackUrllink);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-Type", "application/json");  //; utf-8
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setRequestProperty("Authorization", tokenValue);

        try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
            outputStream.write(jsonBodyStr.getBytes());
            outputStream.flush();
        }

        try {
            if (httpURLConnection.getResponseCode() == HTTP_CREATED) {
                returnValue = true;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return  returnValue;
    }

    public void sendUser(String username, String jsonString) throws IOException {

        String updateUrllink = "https://smr.cs.ualberta.ca/comparelibraries/api/pluginusers/" + username + "/";
        String InsertUrllink = "https://smr.cs.ualberta.ca/comparelibraries/api/pluginusers/";
        String tokenValue = getUserToken();

        if (tokenValue.equals("NoToken")) {
            createUser(InsertUrllink, jsonString);
        }
        else {
            updateUser(updateUrllink, jsonString,tokenValue);
        }
    }

    public void updateUserProfile(User userRecord) throws IOException {
        String filePath = this.filePath  + "\\user.json";
        JSONObject obj = new JSONObject();
        java.util.List<Integer> projects = new ArrayList<Integer>();

        if (userRecord.getProject1().equals("1"))
            projects.add(1);
        if (userRecord.getProject2().equals("1"))
            projects.add(2);
        if (userRecord.getProject3().equals("1"))
            projects.add(3);
        if (userRecord.getProject4().equals("1"))
            projects.add(4);
        if (userRecord.getProject5().equals("1"))
            projects.add(5);

        ArrayList<Integer> teams = new ArrayList<Integer>();
        if (userRecord.getTeam1().equals("1"))
            teams.add(1);
        if (userRecord.getTeam2().equals("1"))
            teams.add(2);
        if (userRecord.getTeam3().equals("1"))
            teams.add(3);
        if (userRecord.getTeam4().equals("1"))
            teams.add(4);

        obj.put("username", userRecord.getUserID());
        obj.put("occupation", userRecord.getOccupation());
        obj.put("programming_skills", Integer.parseInt(userRecord.getProgramming()));
        obj.put("java_skills", Integer.parseInt(userRecord.getJavaSkills()));
        obj.put("plugin_rating", Integer.parseInt(userRecord.getRate()));
        obj.put("optional_feedback", userRecord.getOptionalFeedback());
        obj.put("Send_Feedback", Integer.parseInt(userRecord.getSendAllCloud()));
        obj.put("Accept", Integer.parseInt(userRecord.getCloudStore()));
        obj.put("projects", projects);
        obj.put("teams", teams);

        File userFile = new File(filePath);
        FileOutputStream fOuts = null;
        try {
            fOuts = new FileOutputStream(userFile);

        }
        catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }

        try {
            userFile.createNewFile();
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        }

        StringBuilder result = new StringBuilder();
        String jsonData = obj.toString();
        result.append(jsonData);
        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOuts);
        myOutWriter.append(result);
        myOutWriter.close();

        if (Integer.parseInt(userRecord.getCloudStore()) == 1) {
            sendUser(userRecord.getUserID(), obj.toString());

            sendFeedbackToCloud(); // check if the user has local feedback stored previously, send them now.
        }
    }

    public void storeLocalFeedback(JSONObject obj) throws IOException {

        String localFeedbackPath = this.filePath +"\\Feedback.json";
        File localFeedbackFile = new File(localFeedbackPath);
        JSONObject feedbackObject = null; // = new JSONObject();
        if (localFeedbackFile.exists()) {
            String content = FileUtils.readFileToString(localFeedbackFile, "utf-8");
            JSONObject objM = new JSONObject(content);
            JSONArray jsonarr = objM.getJSONArray("Feedback");
            jsonarr.put(obj);
            feedbackObject = new JSONObject();
            feedbackObject.put("Feedback", jsonarr);
        } else
        {
            feedbackObject = new JSONObject();
            JSONArray feedbackEntryArray = new JSONArray();
            feedbackEntryArray.put(obj);
            feedbackObject.put("Feedback", feedbackEntryArray);
        }

        FileOutputStream fOuts = new FileOutputStream(localFeedbackFile);
        String result = feedbackObject.toString();
        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOuts);
        myOutWriter.append(result);
        myOutWriter.close();
    }

    public void sendFeedbackToCloud() throws IOException {
        // this function sends all records in feedback.json, it will iterate through each record which will be sent via sendFeedback() function
        String filePath = this.filePath +"\\Feedback.json";
        String tokenValue = getUserToken();
        File feedbackFile = new File(filePath);
        int successfulsent = 0;
        if (feedbackFile.exists()) {
            String content = FileUtils.readFileToString(feedbackFile, "utf-8");
            JSONObject objM = new JSONObject(content);
            JSONArray jsonarr = objM.getJSONArray("Feedback");
            int i = 0;
            while (i<jsonarr.length() )
            {
                JSONObject jsonObj = (JSONObject)jsonarr.get(i);
                jsonObj.put("user", this.userid);
                String JsonString = jsonObj.toString();
                if (sendFeedback(JsonString, tokenValue)) {++successfulsent;}
                i++;
            }
            if (successfulsent<=jsonarr.length()) {
                Files.deleteIfExists(Paths.get(filePath));
            }

        }
    }

    public String readMavenVersion(String urlStr) throws IOException {
        String returnValue = "// " + urlStr;
        urlStr = urlStr + "/latest";
        try {
            URL oracle = new URL(urlStr);
            URLConnection yc = oracle.openConnection();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream()))) {
                String inputLine;
                int location = -1;
                while (((inputLine = in.readLine()) != null) && (location == -1)) {
                    location = inputLine.indexOf("gradle-div");
                    if (location != -1) {
                       returnValue = "    " + in.readLine() + "\n" + "    "  + in.readLine();
                    }
                }
                in.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return  returnValue;
    }

    public void updateFeedback(boolean sendToCloud, ReplacementFeedback replacementFeedback) throws IOException {

        String tokenValue = getUserToken();
        JSONObject obj = new JSONObject();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        if (sendToCloud) {obj.put("user", this.userid);} // no userid to be stored locally, as it is not yet set
        obj.put("full_lib_list", replacementFeedback.getFull_lib_list());
        obj.put("to_library", replacementFeedback.getTo_library_id());
        obj.put("from_library", replacementFeedback.getFrom_library_id());
        obj.put("line_num", replacementFeedback.getLine_num());
        obj.put("project_name", replacementFeedback.getProject_name());
        obj.put("class_name", replacementFeedback.getClass_name());
        obj.put("action_date", df.format(replacementFeedback.getAction_date()));

        String JsonString = obj.toString();
        if (sendToCloud)
        {
           sendFeedback(JsonString, tokenValue);
        }
        else
        {
            storeLocalFeedback(obj); // create local json file until the user decided to send his feedback
        }
    }



    public static LocalDate StringToDate(String dob) throws ParseException {
        dob = dob.substring(0,10);
        LocalDate date = LocalDate.parse(dob);
        return date;
    }

    public ArrayList <Library> getMetricsData(int  metricDomain, int metricLibraryID) throws ParseException {

        String filePath = this.filePath +"\\allLibraries.json";
        int domain_id;
        Library libraryDataPoint;
        ArrayList <Library> libraryList = new ArrayList<Library> ();

        File libraryFile = new File(filePath);
        if (libraryFile.exists()) {
            String content = null;
            try {
                content = FileUtils.readFileToString(libraryFile, "utf-8");
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject obj = new JSONObject(content);
            JSONArray domainLibraries = obj.getJSONArray("Libraries");

            for (int i = 0; i < domainLibraries.length(); i++) {
                JSONObject jsonObj = (JSONObject) domainLibraries.get(i);
                if (jsonObj.has("domain")) {
                    domain_id = jsonObj.getInt("domain");
                    if (domain_id == metricDomain) {
                        libraryDataPoint = new Library();
                        if (jsonObj.has("id"))
                            libraryDataPoint.setId(jsonObj.getInt("id"));
                        if (jsonObj.has("id"))
                            libraryDataPoint.setLibrary_id(jsonObj.getInt("id"));
                        if (jsonObj.has("domain"))
                            libraryDataPoint.setDomain_id(jsonObj.getInt("domain"));
                        if (jsonObj.has("name"))
                            libraryDataPoint.setName(jsonObj.getString("name"));
                        if (jsonObj.has("github_repo"))
                            libraryDataPoint.setRepository(jsonObj.getString("github_repo"));
                        if (jsonObj.has("package"))
                            libraryDataPoint.setPackage(jsonObj.getString("package")); // Change tag to package
                        if (jsonObj.has("maven_url"))
                            libraryDataPoint.setMavenlink(jsonObj.getString("maven_url")); // Change tag to maven
// will be removed later
                    //    libraryDataPoint.setMavenlink("https://mvnrepository.com/artifact/junit/junit");

                        if (jsonObj.has("popularity"))
                            libraryDataPoint.setPopularity(jsonObj.getDouble("popularity"));
                        if (jsonObj.has("release_frequency"))
                            libraryDataPoint.setRelease_frequency(jsonObj.getDouble("release_frequency"));
                        if (jsonObj.has("issue_closing_time"))
                            libraryDataPoint.setIssue_closing_time(jsonObj.getDouble("issue_closing_time"));
                        if (jsonObj.has("issue_response_time"))
                            libraryDataPoint.setIssue_response_time(jsonObj.getDouble("issue_response_time"));
                        if (jsonObj.has("performance"))
                            libraryDataPoint.setPerformance(jsonObj.getDouble("performance") / 100);
                        if (jsonObj.has("security"))
                            libraryDataPoint.setSecurity(jsonObj.getDouble("security") / 100);
                        if (jsonObj.has("backwards_compatibility"))
                            libraryDataPoint.setBackwards_compatibility(jsonObj.getDouble("backwards_compatibility"));

                        if (jsonObj.has("overall_score"))
                            libraryDataPoint.setOverall_score(jsonObj.getDouble("overall_score"));
                        if (jsonObj.has("license"))
                            libraryDataPoint.setLicense(jsonObj.getString("license"));
                        if (jsonObj.has("last_modification_date"))
                            libraryDataPoint.setLast_modification_date(StringToDate(jsonObj.getString("last_modification_date")));
                        if (jsonObj.has("last_discussed_so")) {
                            Object tempDateObject = jsonObj.get("last_discussed_so");
                            String tempDate;
                            if (tempDateObject.equals(null))
                            {  tempDate = "1900-01-01"; }
                            else
                            { tempDate = jsonObj.getString("last_discussed_so"); }
                            libraryDataPoint.setLast_discussed_so(StringToDate(tempDate));
                        }

                        if (metricLibraryID == libraryDataPoint.getLibrary_id()) {
                            // at the first row for the selected one
                            libraryDataPoint.setName(libraryDataPoint.getName() + " \n(Current \n Library)");
                            libraryList.add(0, libraryDataPoint);
                        } else {
                            libraryList.add(libraryDataPoint);
                        }
                    }
                }
            }
        }
        return libraryList;
    }


    public Image readCharts(int metric_DomainID, int metric_line) throws IOException {

        Image img=null;
        String filePath = this.filePath +"\\allCharts.json";
        long metric = 0;
        long domain =0 ;
        String chartfile = null;

        File file = new File(filePath);
        String content = FileUtils.readFileToString(file, "utf-8");
        JSONObject obj = new JSONObject(content);
        JSONArray jsonarr_1 = obj.getJSONArray("Charts");
        int i = 0;
        boolean found = false;

        while (i<jsonarr_1.length() && !(found)) {
            JSONObject jsonObj = (JSONObject)jsonarr_1.get(i);
            if(jsonObj.has("metric"))
                metric = jsonObj.getLong("metric");
            if(jsonObj.has("domain"))
                domain = jsonObj.getLong("domain");
            if (metric_DomainID == domain && metric_line == metric )
            {
                if(jsonObj.has("chart"))
                    chartfile = jsonObj.getString("chart");
                byte[] imgArr = Base64.getDecoder().decode(chartfile);
                found = true;
                img= Toolkit.getDefaultToolkit().createImage(imgArr);
            }
            i++;
        }
        return img;
    }



    public void EnabledDomain(int domain, String project_name) {
        if (isEnabled(domain, project_name))
        { setdisabled(domain, project_name);}
        else
        { setEnabled(domain, project_name);}

    }

    private void setdisabled(int domain, String project_name) {

        String filePath = this.filePath +"\\"+ project_name +"domains.json";
        File myFile = new File(filePath);
        JSONObject Mainobj = null; // = new JSONObject();
        if (myFile.exists()) {
            String content = null;
            try {
                content = FileUtils.readFileToString(myFile, "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONObject objM = new JSONObject(content);
            JSONArray jsonarr = objM.getJSONArray("Domains");
            int location = domainExist(domain, jsonarr);
            jsonarr.remove(location);
            Mainobj = new JSONObject();
            Mainobj.put("Domains", jsonarr);
        }
        FileOutputStream fOuts = null;
        try {
            fOuts = new FileOutputStream(myFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String result = Mainobj.toString();
        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOuts);
        try {
            myOutWriter.append(result);
            myOutWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void setEnabled(int domain, String project_name) {

        String filePath = this.filePath +"\\"+ project_name +"domains.json";
        File myFile = new File(filePath);
        JSONObject Mainobj = null; // = new JSONObject();
        if (myFile.exists()) {
            String content = null;
            try {
                content = FileUtils.readFileToString(myFile, "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONObject objM = new JSONObject(content);
            JSONArray jsonarr = objM.getJSONArray("Domains");
            jsonarr.put(domain);
            Mainobj = new JSONObject();
            Mainobj.put("Domains", jsonarr);
        } else
        {
            Mainobj = new JSONObject();
            JSONArray array = new JSONArray();
            array.put(domain);
            Mainobj.put("Domains", array);
        }

        FileOutputStream fOuts = null;
        try {
            fOuts = new FileOutputStream(myFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String result = Mainobj.toString();
        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOuts);
        try {
            myOutWriter.append(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            myOutWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private int domainExist(int domain, JSONArray arr)
    {
        Boolean found = false;
        int index = 0;
        while (!found && index < arr.length() )
        {
            if (arr.getInt(index) == domain)
            {
                found = true;
            } else {++index;}
        }
        if (!found) index = -1;
        return index;
    }

    public boolean isEnabled(int domain, String project_name) {
        boolean disabled = false;
        String filePath = this.filePath +"\\"+ project_name +"domains.json";

        File myFile = new File(filePath);
        JSONObject Mainobj = null; // = new JSONObject();
        if (myFile.exists()) {
            String content = null;
            try {
                content = FileUtils.readFileToString(myFile, "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONObject objM = new JSONObject(content);
            JSONArray arr = objM.getJSONArray("Domains");
            int location = domainExist(domain, arr);
            if (location > -1)
            { disabled = true; }
        } else
        { disabled = false; }

        return disabled;
    }



}

