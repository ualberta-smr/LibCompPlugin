# LibComp: An IntelliJ IDE Plugin for Java Library Metric Comparison

## Installing LibComp:

1. Clone the repository 

2. Navigate to `C:\Users\[userid]\AppData\Roaming` on your machine and insert the LibComp folder there. This is where the library data DB and feedback DB as well as all of the .png's showing the trends of library metrics are stored. 

3. Open the project `PluginCode` in your intelliJ IDE

4. Compile and run the plugin (a new IDE window will open with the plugin running, you may now open any Java files of your choice and use the tool to analyze your library packages)

`Currently working on making this an installable plugin`

## How to use LibComp:

* Right click on the java file you want our plugin to analyze, all the libraries we have information on will be highlighted in pink 

* If you want to compare a specific library package (only valid for the ones highlighted in pink), right click on the import statement and click `Library Comparison`

* A dialog will now pop up with all alternative libraries. We provide the ability to sort by any chosen metric (in ascending or descending order) and there are trends shown in graphs. 

* To replace a library package, select the relavent column and click `replace`. Otherwise click `cancel`. Note that you cannot replace a library with itself. 

## Sending us feedback:
To help us understand the types of users and projects LibComp is most relevent to, we have set up a feedback collection system. To be a part of this, follow these steps:

1. Navigate to `Tools > LibComp > User Profile` 

2. Fill in the form and click the `update` button. **Congratulations, you have just created a user profile!** (You are free to update your profile at any point as the information about you is updated, just navigate to the same dialog, update the relevant information, and click `Update`)

3. Navigate to `Tools > LibComp > Send Us Your Feedback` 

4. Here you have 3 capabilities: you may rate our plugin from 1 (worst) to 5 (best), you may send us a comment about your user experience, and you may click `save to the cloud` for all of your previous feedback data to be sent to our central database. 

5. We will not be saving any feedback data to our central database unless you explicitly allow it
