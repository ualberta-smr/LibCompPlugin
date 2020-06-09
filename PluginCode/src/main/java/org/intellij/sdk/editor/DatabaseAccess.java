package org.intellij.sdk.editor;

import com.intellij.openapi.application.PathManager;
import com.jcraft.jsch.Session;
import org.apache.commons.io.FileUtils;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;

import java.io.FileNotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;

/**
 * The DatabaseAccess class is where all connections to the databases are made
 */

public class DatabaseAccess {

    private static Session session = null;
    private int userid = 0;
    private String filePath = PathManager.getPluginsPath()+"\\Library_Comparison\\lib";

    public ArrayList<String> selectJasonAllLibraries(String librarySelected) throws IOException
    {
        String filePath = this.filePath + "\\allLibraries.json";
        ArrayList<String> Terms;
        Terms = new ArrayList<String>();
        long library_id = 0;
        long domain_id =0 ;
        String domain_name = "";
        String Package = "";

        File file = new File(filePath);
        if (file.exists())
        {
            String content = FileUtils.readFileToString(file, "utf-8");
            JSONObject obj = new JSONObject(content);
            JSONArray jsonarr_1 = obj.getJSONArray("Libraries");
            for (int i = 0; i < jsonarr_1.length(); i++)
            {
                JSONObject jsonObj = (JSONObject) jsonarr_1.get(i);
                if (jsonObj.has("id"))
                    library_id = jsonObj.getLong("id");
                if (jsonObj.has("domain"))
                    domain_id = jsonObj.getLong("domain");
                if (jsonObj.has("domain_name"))
                    domain_name = jsonObj.getString("domain_name");
                if (jsonObj.has("package"))
                    Package = jsonObj.getString("package");

                boolean isFound;
                //   if(!isNullOrEmpty(Package))
                {
                    isFound = Package.contains(librarySelected);
                    if (isFound) {
                        Terms.add(Long.toString(library_id));
                        Terms.add(Long.toString(domain_id));
                        Terms.add("0"); // for future use
                        Terms.add("0"); // for future use
                        Terms.add(domain_name);
                    }
                }
            }

        }
        return (Terms);
    }


    public String VersionCloud() throws IOException  {
        String cloudVer = "";
        String linkURL = "http://smr.cs.ualberta.ca/comparelibraries/api/metrics/?format=json&latestdate=";

        HttpURLConnection connection = (HttpURLConnection) new URL(linkURL).openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if(responseCode == 200 || responseCode == 201) {
            String line;
            InputStream response = connection.getInputStream();
            InputStream in = new BufferedInputStream(response);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                cloudVer = line;
            }
        }
        int loc = cloudVer.indexOf(":");
        cloudVer = cloudVer.substring(loc+2, cloudVer.length()-3);
        return (cloudVer);
    }

    public String VersionLocal() throws IOException {

        String filePath = this.filePath +"\\Version.json";
        String datelocal = "NewUser";
        File file = new File(filePath);

        if (file.exists()) {
            String content = FileUtils.readFileToString(file, "utf-8");
            JSONObject obj = new JSONObject(content);
            JSONArray jsonarr_1 = obj.getJSONArray("version");
            int i = 0;
            boolean found = false;
            while (i < jsonarr_1.length() && !(found)) {
                JSONObject jsonObj = (JSONObject) jsonarr_1.get(i);
                if (jsonObj.has("created_on")) {
                    datelocal = jsonObj.getString("created_on");
                }
                i++;
            }
        }
        return (datelocal);
    }

    public void uploadJson(String linkURL, String filePath, String suffix) throws IOException {

        File myFile = new File(filePath);
        FileOutputStream fOuts = new FileOutputStream(myFile);
        myFile.createNewFile();

        StringBuilder result = new StringBuilder();
        String line;
        result.append(suffix);
        HttpURLConnection connection = (HttpURLConnection) new URL(linkURL).openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if(responseCode == 200 || responseCode == 201){

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


    public void GetRestTestApi() throws IOException {
        String suffixLocal = this.filePath + "\\";
        String url = "http://smr.cs.ualberta.ca/comparelibraries/api/libraries/?format=json";
        String filePath = suffixLocal + "allLibraries.json";
        uploadJson(url, filePath, "{\"Libraries\":");

        url = "http://smr.cs.ualberta.ca/comparelibraries/api/charts/?format=json";
        filePath = suffixLocal + "allCharts.json";
        uploadJson(url, filePath, "{\"Charts\":");

        url = "http://smr.cs.ualberta.ca/comparelibraries/api/metrics/?format=json&latestdate=";
        filePath = suffixLocal + "Version.json";
        uploadJson(url, filePath, "{\"version\":");
    }

    public int updateVersionData() throws IOException {
        int returnValue = 1;
        String localVer = "";
        String cloudVer = "";

        try {
            localVer= VersionLocal();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cloudVer= VersionCloud();
        // check now the if we need to upload
        if  (!(localVer.equals(cloudVer)) )
        {
            GetRestTestApi();
        }
        return (returnValue);
    }

    public DataUser ReadUserProfile() {

        String filePath = this.filePath + "\\user.json";
        DataUser userRecord = null;
        File file = new File(filePath);
        if (!file.exists()){
            userRecord = new DataUser();
            String tempShortUserName = UUID.randomUUID().toString().substring(0,14);
       //     userRecord.setUserID(UUID.randomUUID().toString());
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
            userRecord.setSendAllCloud("0");
            userRecord.setRate("0");
            userRecord.setOptionalFeedback("Enter your feedback");
            userRecord.setCloudStore("0");
            userRecord.setSendAllCloud("0");
        }
        else {
            String content = null;
            try {
                content = FileUtils.readFileToString(file, "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject obj = new JSONObject(content);

            userRecord = new DataUser();
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

        } // end new user
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

    public String createLocalToken(String line) throws IOException {
        String tokenValue;
        int userID;
        String filePath = this.filePath + "\\token.json";
        JSONObject json = new JSONObject(line);
        tokenValue = (String) json.get("token");
        userID = (int) json.get("id");
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


        return tokenValue;
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
                        String tokenValue = createLocalToken(line);

                    }
                }
            }
        }  catch (IOException e) {
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
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    public boolean createFeedBack(String urlStr, String jsonBodyStr, String tokenValue) throws IOException {
        boolean returnValue = false;
        URL url = new URL(urlStr);
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
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                returnValue = true;
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return  returnValue;
    }



    public void SendUser(String username, String jsonString) throws IOException {
        String updateUrllink = "http://smr.cs.ualberta.ca/comparelibraries/api/pluginusers/" + username + "/";
        String InsertUrllink = "http://smr.cs.ualberta.ca/comparelibraries/api/pluginusers/";
        String tokenValue = getUserToken();

        if (tokenValue.equals("NoToken")) {
            if (createUser(InsertUrllink, jsonString)) {

            }
        }
        else
        {
            if  (updateUser(updateUrllink, jsonString,tokenValue))
            {

            }
        } // end of (tokenValue.equals("NoToken"))

    }

    public void updateUserProfile(DataUser userRecord) throws IOException {
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

        String line = obj.toString();
        File myFile = new File(filePath);
        FileOutputStream fOuts = null;
        try {
            fOuts = new FileOutputStream(myFile);

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }

        try {
            myFile.createNewFile();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        StringBuilder result = new StringBuilder();
        result.append(line);
        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOuts);
        myOutWriter.append(result);
        myOutWriter.close();

        if (Integer.parseInt(userRecord.getSendAllCloud()) == 1)
        {
            SendUser(userRecord.getUserID(), obj.toString());
        }
    }


    public void updateFeedback(DataFeedback dataFeedback) throws IOException {
        String tokenValue = getUserToken();
        JSONObject obj = new JSONObject();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        obj.put("user", this.userid);
        obj.put("full_lib_list", dataFeedback.getFull_lib_list());
        obj.put("to_library", dataFeedback.getTo_library_id());
        obj.put("from_library", dataFeedback.getFrom_library_id());
        obj.put("line_num", dataFeedback.getLine_num());
        obj.put("project_name", dataFeedback.getProject_name());
        obj.put("class_name", dataFeedback.getClass_name());
        obj.put("action_date", df.format(dataFeedback.getAction_date()));

        String JsonString = obj.toString();
        String InsertUrllink = "http://smr.cs.ualberta.ca/comparelibraries/api/pluginfeedback/";

        if (createFeedBack(InsertUrllink,JsonString, tokenValue))
        {
            // later
        }

    }

    public ArrayList <DataLibrary> GetJsonPerformanceValues(int  metricDomain,int metricLibraryID)
    {
        String filePath = this.filePath +"\\allLibraries.json";
        int domain_id;
        DataLibrary libraryDataPoint;
        ArrayList <DataLibrary> libraryList = new ArrayList<DataLibrary> ();

        File file = new File(filePath);
        if (file.exists()) {
            String content = null;
            try {
                content = FileUtils.readFileToString(file, "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject obj = new JSONObject(content);
            JSONArray jsonarr_1 = obj.getJSONArray("Libraries");

            for (int i = 0; i < jsonarr_1.length(); i++) {
                JSONObject jsonObj = (JSONObject) jsonarr_1.get(i);
                if (jsonObj.has("domain")) {
                    domain_id = jsonObj.getInt("domain");
                    if (domain_id == metricDomain) {
                        libraryDataPoint = new DataLibrary();
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
        return (libraryList);
    }


    public Image ReadCharts(int metric_DomainID,int metric_line) throws IOException {
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
        while (i<jsonarr_1.length() && !(found))
        {
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
        return (img);
    }
}

