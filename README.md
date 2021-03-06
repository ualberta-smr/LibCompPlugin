# LibComp
LibComp is an IntelliJ IDE plugin for Java library metric comparison. Using LibComp, you are able to compare metrics such as library popularity, frequency of releases, performance and more through available data, view visuals of metric data, and sort by metrics that are most important to you. If you are looking for the version of the code in our FSE 2020 submission, please check the [FSE '20 Submission release](https://github.com/ualberta-smr/LibCompPlugin/releases/tag/v1.0).

![](replacement.gif)
  
## Installing LibComp:

1. **Ensure your intelliJ version is updated to 2020,** then, download the file `Library_Comparison-1.0.2020.zip` and copy the path to the location where you saved this file onto your clipboard 

2. Open your IntelliJ IDE and click `File > settings` 

3. A popup should appear, click on `plugins` at the left, then on the `gear icon` at the top, then on `install plugin from disk`

4. Another popup should appear, paste the path to the .zip file from your clipboard and click `ok`

5. Restart the IntelliJ IDE, the plugin should now be installed!

## How to Use LibComp:

* When the project you are working on has loaded, all open files/editors will be analyzed and all the libraries we have information on will be highlighted in pink 

* If you want to compare a specific library package (only valid for the ones highlighted in pink), right click on the import statement and click `Library Comparison`

* A dialogue will now pop up with all alternative libraries. We provide the ability to sort by any chosen metric (in ascending or descending order) and there are trends shown in graphs. 

* To replace a library package, select the relevant column and click `replace`. Otherwise, click `cancel`. Note that you cannot replace a library with itself. 

* **`Important Note:`** On project load, all files are automatically analyzed, however once the project has already loaded, if you wish to open another file in the project to analyze it, open the file and LibComp will automatically analyze the file and highlight relevant lines. If you wish to manually trigger libcomp to analyze a file at any point, you can also trigger libcomp by  right-clicking with your mouse or mouse-pad on the editor you want analyzed. 

## Sending Us Feedback:

To help us understand which libraries get replaced, as well as the types of users and projects LibComp is most relevant to, we have set up a feedback collection system. To be a part of this, follow these steps:

* Navigate to `Tools > LibComp > User Profile`.

* Fill in the form and click the `update` button. **Congratulations, you have just created a user profile!** (You are free to update your profile at any point).

* Navigate to `Tools > LibComp > Send Us Your Feedback`.

* Here is where you can check the relevant boxes to confirm that you are giving consent for your tracked plugin usage to be sent to our server. You also have the option to send a feedback comment and/or rate the plugin from 1 (worst) to 5 (best) and send this to our database. Whenever you wish to send your information, click `Send library interaction data`. 

* All interaction data with the plugin is stored locally. We do not send any data to our central database unless you explicitly allow it. Please note that we do not collect any personal information, such as your name, email, or phone number. We do collect the name of the project and files a library comparison or replacement was made in so if your project/file names contain sensitive information, please do not consent to having your data sent. The data is sent using encrypted channels (https) to our secure server. For GDPR compliance, please check out this [pending issue](https://github.com/ualberta-smr/LibCompPlugin/issues/39).

## Running LibComp Locally:

1. Clone the repository 

2. Open the project `PluginCode` in your IntelliJ IDE

3. Compile and run as a plugin (you will have the option to select a project to open on a new IDE instance where the plugin will be running)

## Publishing a New Version of LibComp:

To publish a new version of LibComp, follow these steps

1. Ensure the plugin runs with no errors before attempting to publish, also ensure that the IntelliJ version number is correct in the `build.gradle` file 

2. Increment the version number of the plugin in the `build.gradle` and `plugin.xml` files _(For example, version 1.07 becomes 1.08)_

3. Click `Run` then `publishPlugin` 

4. A new version of the plugin will be generated as a .zip file. This .zip file will be located in `LibCompPlugin\PluginCode\build\distributions` relative to the root of this repository.

## Contributors
- Rehab El-hajj  (relhajj at ualberta dot ca)
- Sarah Nadi (nadi at ualberta dot ca)
- Xiaole Zeng (xiaole2 at ualberta dot ca)

## To Contribute

We welcome contributions in terms of new features, bug fixes, or GUI enhancements. Please submit a PR and assign Sarah Nadi as a reviewer so we receive a notification.
