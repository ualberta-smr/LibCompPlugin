# library metric comparison plugin

INSTALLING THE PLUGIN:

* Put the database in the specified location: `C:\Users\[userid]\AppData\Roaming` (this is a hidden file). This is just done temporarily for testing purposes, later on the database will be made not local. 
* The main code which completed the functionality described is in two main classes called `EditorIllustrationAction.java`  and `SelectRecords.java` which can be found by navigating to `libmetricplugin/pluginPhase1/My_Plugin/src/main/java/org/intellij/sdk/editor/` 


HOW TO USE THE PLUGIN:

* If a user right clicks on a line that is not highlighted and selects libraycomparison, a dialog with a message indicating there is no match will appear. If they click on a line which is highkighted, a dialog with possible options will appear for them to choose from. If they click cancel nothing will happen. If they click a replacement then it will rpelace the old library being imported. 


DATABASE SCHEMA:

* db.sqlite database is taken from the LibraryComparisonWebsite repo, click here to see the schema on their repo
* feedback.sqlite database is for tracking feedback data 

NEXT STEPS:

1. work on fixing the onLoad functionality (I have opened an issue about this) 

2. add sorting functionality 

3. need a proper user study 

4. look into adding graphs and trends 

5. What is the plan with the database? will it stay local or be kept on a server?


