# library metric comparison plugin phase 1

CURRENT FUNCTIONALITY:

* This is my work on plugin phase 1. So far, I am able to use psi to get the import statements, get their exact starting and ending location using the getOffset() method, and from the offset I can get the line which they are on. The plugin iterates through the import statements within the currently open java file and if the import statement is within the database it will highlight that line.

* If a user right clicks on a line that is not highlighted and selects libraycomparison, a dialog with a message indicating there is no match will appear. If they click on a line which is highkighted, a dialog with possible options will appear for them to choose from. If they click cancel nothing will happen. If they click a replacement then it will rpelace the old library being imported. 


ROOM FOR IMPROVEMENT:

1. The trigger for this is the user right clicking anyehere in the editor. I am working on getting it done onLoad() but I have yet to figure it out (although I do have a good idea of how to do it for automatically detecting changes in the editor).

2. Right now, the part of the import statement I am querying against the database is the last line. I'm not quite sure what I should be querying though, the first part? The whole thing? etc. This would simply require lexical analysis. 

3. Right now I am using a dummy library to simplify testing, eventually I will swicth over to using the actuak necessary database.

NEXT STEPS:

1. work on having the plugin run on load

2. work on having the plugin keep working and re-detecting import statements as the user types on the editor

3. work on gathering feedback data 

Note: The main code which completed the functionality described is in two main classes called `EditorIllustrationAction.java`  and `SelectRecords.java` which can be found by navigating to `libmetricplugin/pluginPhase1/My_Plugin/src/main/java/org/intellij/sdk/editor/` 
