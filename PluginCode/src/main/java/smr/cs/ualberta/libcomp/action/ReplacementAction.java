package smr.cs.ualberta.libcomp.action;

import com.android.aapt.Resources;
import com.intellij.lang.ASTNode;
import com.intellij.lang.FileASTNode;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.xdebugger.ui.DebuggerColors;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.psi.*;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import smr.cs.ualberta.libcomp.*;
import smr.cs.ualberta.libcomp.data.DependencyStatement;
import smr.cs.ualberta.libcomp.data.ReplacementFeedback;
import smr.cs.ualberta.libcomp.data.ImportStatement;
import smr.cs.ualberta.libcomp.data.User;
import smr.cs.ualberta.libcomp.dialog.ReplacementDialog;
import smr.cs.ualberta.libcomp.utils.PositionalXMLReader;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The ActionReplacement class is the main action for the plugin
 * This is triggered by the replacement button on the main plugin dialog
 */
public class ReplacementAction extends AnAction {
    public ArrayList<ImportStatement> importObjectList;
    public ArrayList<DependencyStatement> dependObjectList;
    public ArrayList<DependencyStatement> mavenObjectList;
    private int toLibrary;
    private String selectedLibList;
    private String libraryName;
    private boolean sendToCloud = false ;

    public ReplacementAction() {
        importObjectList = new ArrayList<>();
        dependObjectList = new ArrayList<>();
        mavenObjectList = new ArrayList<>();
    }

    enum FileTypes {
        JAVA, GRADLE, MAVEN
    }
    /**
     * The actionPerformed method is called whenever an action event is executed, i.e. a right click on the editor
     * @param event is the current action event
     */
    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {

        PsiFile psiFile = event.getRequiredData(CommonDataKeys.PSI_FILE);
        if (psiFile != null) {
            FileType fileType = psiFile.getFileType();
            String fileName = psiFile.getName();
            String fileExtention = fileType.getDefaultExtension();

            if (fileExtention.equalsIgnoreCase("java")) {
                try {
                    replaceRequestedImport(event);
                    detectOnAction(event, FileTypes.JAVA);
                } catch (ParseException | IOException exception) {
                    exception.printStackTrace();
                }

            }

            if (fileName.equalsIgnoreCase("pom.xml")) {
                try {
                    replaceRequestedMaven(event);
                    detectOnAction(event, FileTypes.MAVEN);
                } catch (ParseException | IOException exception) {
                    exception.printStackTrace();
                }

            }

            if (fileExtention.equalsIgnoreCase("groovy"))  {
                try {
                    replaceRequestedDependency(event);
                    detectOnAction(event, FileTypes.GRADLE);
                } catch (ParseException | IOException exception) {
                    exception.printStackTrace();
                }

            }
            event.getPresentation().setVisible(true);
            event.getPresentation().setEnabled(true);
        }
    }

   public  Project getActiveProject() {

        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        Project activeProject = null;
        for (Project project : projects) {
            Window window = WindowManager.getInstance().suggestParentWindow(project);
            if (window != null && window.isActive()) {
                activeProject = project;
            }
        }
        return  activeProject;
    }

    public void detectAllOpenEditors() throws IOException, SAXException {

        Project proj= getActiveProject();
        String projectName = "none";
        if (proj == null)  { return;}
        projectName = proj.getName();
        FileEditorManager manager = FileEditorManager.getInstance(proj);
        VirtualFile[] filesAll = manager.getOpenFiles();
        FileEditor[] editorFileAll = manager.getAllEditors();
        int indexOpenEditors = 0;

        while (indexOpenEditors < editorFileAll.length) {

            PsiFile psiFile = PsiManager.getInstance(proj).findFile(filesAll[indexOpenEditors]);
            Editor editor = ((TextEditor)editorFileAll[indexOpenEditors]).getEditor();

            if (psiFile != null) {
                FileType fileType = psiFile.getFileType();
                String fileName = psiFile.getName();
                String fileExtention = fileType.getDefaultExtension();

                if (fileExtention.equalsIgnoreCase("java")) {
                    detectJavaImport(psiFile, editor, projectName);
                }
                if (fileName.equalsIgnoreCase("pom.xml")) {
                    detectMavenDependency(editor, psiFile, projectName);
                }
                if (fileExtention.equalsIgnoreCase("groovy")) {
                    detectGradleDependency(editor, psiFile, projectName);
                }
            }
            indexOpenEditors++;
        }
    }

    public void detectOnAction(@NotNull final AnActionEvent event, @NotNull final FileTypes fileType) throws IOException {

        Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        PsiFile psiFile = event.getRequiredData(CommonDataKeys.PSI_FILE);
        final Project project = event.getRequiredData(CommonDataKeys.PROJECT);
        String projectName = project.getName();

        try {
            if (fileType.equals(FileTypes.JAVA)) {
                detectJavaImport(psiFile, editor, projectName);
            } else if (fileType.equals(FileTypes.MAVEN)) {
                detectMavenDependency(editor, psiFile, projectName);
            } else if (fileType.equals(FileTypes.GRADLE)) {
                detectGradleDependency(editor, psiFile, projectName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The update method is called whenever the action event is updated.
     * This will later be replaced with the onLoad method once I figure that out
     * The condition in this method ensures that there is al least one caret available
     * The method then calls a separate method to detect import statements
     * @param event is the current action event
     */
    @Override
    public void update(@NotNull final AnActionEvent event) {

        try {
            detectAllOpenEditors();
        } catch (IOException | SAXException exception) {
            exception.printStackTrace();
        }

        PsiFile psiFile = event.getRequiredData(CommonDataKeys.PSI_FILE);
        if (psiFile != null) {
            FileType fileType = psiFile.getFileType();
            if (fileType.getDefaultExtension().equalsIgnoreCase("java")) {
                try {
                    detectOnAction(event, FileTypes.JAVA);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
            if (psiFile.getName().equalsIgnoreCase("pom.xml")) {
                try {
                    detectOnAction(event, FileTypes.MAVEN);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
            if (fileType.getDefaultExtension().equalsIgnoreCase("groovy")) {
                try {
                    detectOnAction(event, FileTypes.GRADLE);
                    event.getPresentation().setEnabledAndVisible(true);
                }
                catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
             event.getPresentation().setVisible(true);
             event.getPresentation().setEnabled(true);
        }
    }

     /**
     * The replaceRequestedImport method will replace the import statement on the line which the user clicked on with the library which the user chooses from the dialog
     * This will only occur if the library in the initial import statement is valid (i.e. in database)
     * For now, the libraries in the database are the last term of the import statement
     * ex. for my.import.statement.rehab, the term queried against in the database is "rehab"
     * This can be changed with some lexical analysis once we discuss exactly which part of the statement must be queried against the database
     * @param event This is the current action event
     */
    public void replaceRequestedImport(@NotNull final AnActionEvent event) throws ParseException {
        final Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = event.getRequiredData(CommonDataKeys.PROJECT);
        final Document document = editor.getDocument();
        PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(document);

        String projectName = project.getName();
        String className = "";
        if (file instanceof PsiJavaFile) {
            PsiClass[] classes = ((PsiJavaFile) file).getClasses();
            if (classes.length > 0) {
                className= classes[0].getQualifiedName();
            }
        }

        Date actionDate = new Date();
        int fromLibrary;

        User userRecord = new User();
        DatabaseAccess dataAccessObject = new DatabaseAccess();
        userRecord = dataAccessObject.readUserProfile();
        sendToCloud = (Integer.parseInt(userRecord.getSendAllCloud()) == 1);

        //Work off of the primary caret to get the selection info
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();

        //Get the location of the mouse click, get the caret offset, then from the offset get the line number
        int mouseClickLocation = primaryCaret.getOffset();
        int clickedLineNumber = document.getLineNumber(mouseClickLocation);

        //Check if the user clicked on a line that is potentially replaceable (i.e. import statement is in database)
        int currentLine = 0;

        while (currentLine < importObjectList.size()) {
            if (importObjectList.get(currentLine).getImportLocation() == clickedLineNumber) {

                //found a replaceable import statement
                String importStatementFull;
                String importStatementLastWord; //this importStatementObject the last library of the import statement (i.e. last word at the end)
                int offsetLastWord = 0;
                int lineNum;

                fromLibrary = importObjectList.get(currentLine).getImportLib();
                int finalFromLibrary = fromLibrary;
                PsiImportStatementBase importStatementObject =  importObjectList.get(currentLine).getImportListBase();
                importStatementFull = importStatementObject.getImportReference().getQualifiedName();
                importStatementLastWord = importStatementObject.getImportReference().getReferenceName();

                offsetLastWord = importStatementObject.getImportReference().getTextOffset();
                lineNum = document.getLineNumber(offsetLastWord);

                int locationStartOfImport = offsetLastWord - (importStatementFull.length() - importStatementLastWord.length());
                int locationEndOfImport = document.getLineEndOffset(lineNum) - 1;

                ReplacementDialog replacementDialog =new ReplacementDialog(importObjectList.get(currentLine).getDomainName(), importObjectList.get(currentLine).getImportDomain(),importObjectList.get(currentLine).getImportLib());

                String finalClassName = className;
                WindowAdapter adapter = new WindowAdapter() {

                    @Override
                    public void windowLostFocus(WindowEvent e) {
                        String finalChoice = replacementDialog.getLibraryReturned();
                        toLibrary = replacementDialog.getto_library();
                        selectedLibList = replacementDialog.getSelectionLibrary();

                        if (finalChoice.equals("None") == false) {
                            finalChoice = finalChoice + ".*";
                            String finalChoice1 = finalChoice;
                            WriteCommandAction.runWriteCommandAction(project, () ->
                                    document.replaceString(locationStartOfImport, locationEndOfImport, finalChoice1));
                        }

                        try {
                                DatabaseAccess dataAccessObject = new DatabaseAccess();
                                ReplacementFeedback replacementFeedbackPoint = new ReplacementFeedback(0, actionDate, lineNum, projectName, finalClassName, selectedLibList, finalFromLibrary, toLibrary);
                                dataAccessObject.updateFeedback(sendToCloud, replacementFeedbackPoint);
                        }
                        catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                        String finalChoice = replacementDialog.getLibraryReturned();
                        toLibrary = replacementDialog.getto_library();
                        selectedLibList = replacementDialog.getSelectionLibrary();

                        if (finalChoice.equals("None") == false) {
                            finalChoice = finalChoice + ".*";
                            String finalChoice1 = finalChoice;
                            WriteCommandAction.runWriteCommandAction(project, () ->
                                    document.replaceString(locationStartOfImport, locationEndOfImport, finalChoice1));
                            try {
                                    DatabaseAccess dataAccessObject = new DatabaseAccess();
                                    ReplacementFeedback replacementFeedbackPoint = new ReplacementFeedback(0, actionDate, lineNum, projectName, finalClassName, selectedLibList, finalFromLibrary, toLibrary);
                                    dataAccessObject.updateFeedback(sendToCloud, replacementFeedbackPoint);
                            }
                            catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                        try {
                                DatabaseAccess dataAccessObject = new DatabaseAccess();
                                ReplacementFeedback replacementFeedbackPoint = new ReplacementFeedback(0, actionDate, lineNum, projectName, finalClassName, selectedLibList, finalFromLibrary, toLibrary);
                                dataAccessObject.updateFeedback(sendToCloud, replacementFeedbackPoint);
                            }
                        catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                };
               replacementDialog.addWindowListener(adapter);
               replacementDialog.addWindowFocusListener(adapter);
               replacementDialog.setVisible(true);
            }
            currentLine++;
        }
    }

    public void replaceRequestedMavenGradle(@NotNull final AnActionEvent event,
                                          @NotNull final String className,
                                          @NotNull final int typeofMaven,
                                          @NotNull final ArrayList<DependencyStatement> dependencyList) throws ParseException {
        final Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = event.getRequiredData(CommonDataKeys.PROJECT);
        final Document document = editor.getDocument();
        PsiFile psiFile = event.getRequiredData(CommonDataKeys.PSI_FILE);

        String projectName = project.getName();

        Date actionDate = new Date();
        int fromLibrary;

        User userRecord = new User();
        DatabaseAccess dataAccessObject = new DatabaseAccess();
        userRecord = dataAccessObject.readUserProfile();
        sendToCloud = (Integer.parseInt(userRecord.getSendAllCloud())==1);

        //Work off of the primary caret to get the selection info
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();

        //Get the location of the mouse click, get the caret offset, then from the offset get the line number
        int mouseClickLocation = primaryCaret.getOffset();
        int clickedLineNumber = document.getLineNumber(mouseClickLocation);

        //Check if the user clicked on a line that is potentially replaceable (i.e. import statement is in database)
        int currentLine = 0;
        int locationStartOfImport;
        int locationEndOfImport;

        while (currentLine < dependencyList.size()) {
            if (dependencyList.get(currentLine).getImportLocation() == clickedLineNumber) {
                fromLibrary = dependencyList.get(currentLine).getImportLib();
                locationStartOfImport = dependencyList.get(currentLine).getFromlocation();
                locationEndOfImport = dependencyList.get(currentLine).getTolocation();
                int finalFromLibrary = fromLibrary;

                ReplacementDialog replacementDialog =new ReplacementDialog(dependencyList.get(currentLine).getDomainName(), dependencyList.get(currentLine).getImportDomain(),dependencyList.get(currentLine).getImportLib());

                int finalLocationStartOfImport = document.getLineStartOffset(clickedLineNumber - 1);
                int finalLocationEndOfImport = locationEndOfImport;
                finalLocationEndOfImport = document.getLineEndOffset(clickedLineNumber + 4);
                String finalClassName = className;
                int finalLocationEndOfImport1 = finalLocationEndOfImport;
                int finalLocationEndOfImport2 = finalLocationEndOfImport;
                WindowAdapter adapter = new WindowAdapter() {

                    @Override
                    public void windowLostFocus(WindowEvent e) {
                        String finalChoice = replacementDialog.getMavenReturned();

                        toLibrary = replacementDialog.getto_library();
                        selectedLibList = replacementDialog.getSelectionLibrary();
                        libraryName = replacementDialog.getLibraryname();

                        if (finalChoice.equals("None") == false) {
                            try {
                                finalChoice = dataAccessObject.readMavenVersion(finalChoice,typeofMaven);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }

                            String finalChoice1 = finalChoice;
                            WriteCommandAction.runWriteCommandAction(project, () ->
                                    document.replaceString(finalLocationStartOfImport, finalLocationEndOfImport1, finalChoice1));

                            try {
                                detectMavenDependency(editor, psiFile, projectName);
                            } catch (IOException | SAXException exception) {
                                exception.printStackTrace();
                            }
                        }

                        try {
                            DatabaseAccess dataAccessObject = new DatabaseAccess();
                            ReplacementFeedback replacementFeedbackPoint = new ReplacementFeedback(0, actionDate, clickedLineNumber, projectName, finalClassName, selectedLibList, finalFromLibrary, toLibrary);
                            dataAccessObject.updateFeedback(sendToCloud, replacementFeedbackPoint);
                        }
                        catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                        String finalChoice = replacementDialog.getMavenReturned();
                        toLibrary = replacementDialog.getto_library();
                        selectedLibList = replacementDialog.getSelectionLibrary();
                        libraryName = replacementDialog.getLibraryname();

                        if (finalChoice.equals("None") == false) {

                            try {
                                finalChoice = dataAccessObject.readMavenVersion(finalChoice, typeofMaven);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }

                            String finalChoice1 = finalChoice;

                            WriteCommandAction.runWriteCommandAction(project, () ->
                                    document.replaceString(finalLocationStartOfImport, finalLocationEndOfImport2, finalChoice1));

                            try {
                                detectMavenDependency(editor, psiFile, projectName);
                            } catch (IOException | SAXException exception) {
                                exception.printStackTrace();
                            }


                            try {
                                DatabaseAccess dataAccessObject = new DatabaseAccess();
                                ReplacementFeedback replacementFeedbackPoint = new ReplacementFeedback(0, actionDate, clickedLineNumber, projectName, finalClassName, selectedLibList, finalFromLibrary, toLibrary);
                                dataAccessObject.updateFeedback(sendToCloud, replacementFeedbackPoint);

                            }
                            catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                        try {

                            DatabaseAccess dataAccessObject = new DatabaseAccess();
                            ReplacementFeedback replacementFeedbackPoint = new ReplacementFeedback(0, actionDate, clickedLineNumber, projectName, finalClassName, selectedLibList, finalFromLibrary, toLibrary);
                            dataAccessObject.updateFeedback(sendToCloud, replacementFeedbackPoint);

                        }
                        catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                };
                replacementDialog.addWindowListener(adapter);
                replacementDialog.addWindowFocusListener(adapter);
                replacementDialog.setVisible(true);
            }
            currentLine++;
        }
    }

    public void replaceRequestedMaven(@NotNull final AnActionEvent event) throws ParseException {
        String className = "Maven File";

        try {
            replaceRequestedMavenGradle(event, className, 2, mavenObjectList);
        }
        catch (ParseException parseException) {
            parseException.printStackTrace();
        }
    }


    public void replaceRequestedDependency(@NotNull final AnActionEvent event) throws ParseException {
        String className = "groovy Class";

        try {
            replaceRequestedMavenGradle(event, className, 1, dependObjectList);
        }
        catch (ParseException parseException) {
            parseException.printStackTrace();
        }
    }

    public Boolean domainsEnabled (int domain)
    {
        Boolean enabledDomain = false;

        if (domain < 2) { enabledDomain = true; }

        return enabledDomain;
    }

    /**
     * The detectJavaImport method will got through the current open file and test the import statements to see if they are in the database
     * The trigger for this to occur is a right click anywhere in the editor, I have yet to figure out how to have it work onLoad
     * Right now, the library being queried is the "last word" of the import statement
     * ex. for my.import.statement.rehab, the term queried against in the database is "rehab"
     * @param editor is currently focused editor instance.
     * @param psiFile is a PSI (Program Structure Interface) file, the root of a structure representing a file's contents as a hierarchy of elements
     * @param projectName is the name of current project
     */
    public void detectJavaImport(@NotNull final PsiFile psiFile, @NotNull final Editor editor, @NotNull final  String projectName ) {

        try {
            final MarkupModel editorModel = editor.getMarkupModel();
            final Document document = editor.getDocument();
            PsiJavaFile javaFile = (PsiJavaFile)psiFile;

            TextAttributes attributes =
                    EditorColorsManager.getInstance().getGlobalScheme().getAttributes(EditorColors.WRITE_SEARCH_RESULT_ATTRIBUTES);

            TextAttributes softerAttributes = attributes.clone();

            PsiImportList importList = javaFile.getImportList();
            if (importList == null) {

                return;
            }

            importObjectList.clear();
            editorModel.removeAllHighlighters();

            int locationLastWord = 0;
            int importLineNumber;

            for (PsiImportStatementBase importStatementObject : importList.getAllImportStatements()){

                //get location of import statement
                String TermSelected = importStatementObject.getImportReference().getReferenceName();
                locationLastWord = importStatementObject.getImportReference().getTextOffset();
                importLineNumber = document.getLineNumber(locationLastWord);

                //check database
                DatabaseAccess dataAccessObject = new DatabaseAccess();
                ArrayList<String> choicesArray = dataAccessObject.selectJsonAllLibraries(TermSelected);

                if (choicesArray.size()>0){
                    ImportStatement impObj = new ImportStatement();
                    impObj.setImportListBase(importStatementObject);
                    impObj.setImportLocation(importLineNumber);
                    impObj.setImportLib(Integer.parseInt(choicesArray.get(0)));
                    impObj.setImportDomain(Integer.parseInt(choicesArray.get(1)));
                    impObj.setDomainName(choicesArray.get(2));
                    if (dataAccessObject.isEnabled(Integer.parseInt(choicesArray.get(1)), projectName)) {
                        impObj.setEnableddomain(false);
                    }
                    else {
                        impObj.setEnableddomain(true);
                    }
                    importObjectList.add(impObj);

                    if (impObj.getEnableddomain()) {
                        editorModel.addLineHighlighter(importLineNumber,
                        DebuggerColors.BREAKPOINT_HIGHLIGHTER_LAYER + 1,softerAttributes);
                    }
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The detectGradleStartLocation method parses PSI file to detect the PSI dependencies node and return the start location for dependencies.
     * @param psinode is an abstract syntax tree(AST) node corresponding to elements for gradle file.
     * @return the location of PSI dependencies node.
     */
    public int detectGradleStartLocation(FileASTNode psinode)
    {
        int startLocation = -1;
        ASTNode child = psinode.getFirstChildNode();
        String name = child.getText();
        boolean isFound = name.contains("dependencies");
        boolean isReadAll = false;
        while ((!isFound) && (!isReadAll)) {
            child = child.getTreeNext();
            if (child != null)
            {
                name = child.getText();
                isFound = name.contains("dependencies");
            }
            else isReadAll = true;
        }
        if (isFound) {
            startLocation = child.getStartOffset();
        }
        return startLocation;
    }

    /**
     * The detectGradleDependency method gets through the current open gradle file and test the dependency statements to see if they are in the database.
     * The detectGradleDependency method calls detectStatement method to highlight targeted libraries which are in the database.
     * @param editor is currently focused editor instance.
     * @param psiFile is a PSI (Program Structure Interface) file, the root of a structure representing a file's contents as a hierarchy of elements
     * @param projectName is the name of current project
     */
    public void detectGradleDependency(@NotNull final Editor editor, @NotNull final PsiFile psiFile, @NotNull final  String projectName ) throws IOException {

        final MarkupModel editorModel = editor.getMarkupModel();
        final Document document = editor.getDocument();
        TextAttributes attributes = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(EditorColors.WRITE_SEARCH_RESULT_ATTRIBUTES);
        TextAttributes softerAttributes = attributes.clone();
        boolean dependenciesExists = false;
        int currentLine = 0;
        int loc = detectGradleStartLocation(psiFile.getNode()); // Parse PSI to detect the PSI dependencies node
        if (loc!= -1) // dependencies exists
        {
            dependenciesExists = true;
            currentLine = document.getLineNumber(loc); // line number of the dependencies PSI node
        }
        String lineText;
        String selectedTerm;
        dependObjectList.clear();
        editorModel.removeAllHighlighters();

        while (dependenciesExists)
        {
            int startOffset = document.getLineStartOffset(currentLine);
            int endOffset = document.getLineEndOffset(currentLine);
            lineText = null;
            String[] valuesInQuotes = new String[0];
            valuesInQuotes = null;

            lineText = document.getText(new TextRange(startOffset, endOffset)).trim();

            if ((lineText != null) && (lineText.length()>2))
            {valuesInQuotes = StringUtils.substringsBetween(lineText, "\'", "\'");}

            if (valuesInQuotes != null) {
                selectedTerm = valuesInQuotes[0];

                DatabaseAccess dataAccessObject = new DatabaseAccess();
                ArrayList<String> choicesArray = dataAccessObject.selectJsonAllLibraries(selectedTerm);
                if (choicesArray.size() > 0) {
                    DependencyStatement depObj = new DependencyStatement();
                    depObj.setImportLocation(currentLine);
                    depObj.setImportLib(Integer.parseInt(choicesArray.get(0)));
                    depObj.setImportDomain(Integer.parseInt(choicesArray.get(1)));
                    depObj.setDomainName(choicesArray.get(2));
                    depObj.setFromlocation(startOffset);
                    depObj.setTolocation(endOffset);

                    if (dataAccessObject.isEnabled(Integer.parseInt(choicesArray.get(1)), projectName)) {
                        depObj.setEnableddomain(false);
                    }
                    else {
                        depObj.setEnableddomain(true);
                    }

                    dependObjectList.add(depObj);

                    if (depObj.getEnableddomain()) {
                        editorModel.addLineHighlighter(currentLine,
                                DebuggerColors.BREAKPOINT_HIGHLIGHTER_LAYER + 1, softerAttributes);

                    }
                }
            }
            ++currentLine;
            boolean isContainsEnd = lineText.contains("}");
            if (isContainsEnd) {
                dependenciesExists = false;
            }
        }
    }

    /**
     * The detectMavenDependency method gets through the current open maven file and test the dependency statements to see if they are in the database.
     * This will highlight targeted libraries which are in the database.
     * @param editor is currently focused editor instance.
     * @param psiFile is a PSI (Program Structure Interface) file, the root of a structure representing a file's contents as a hierarchy of elements.
     * @param projectName is the name of current project.
     */
    public void detectMavenDependency(@NotNull final Editor editor, @NotNull final PsiFile psiFile, @NotNull final  String projectName ) throws IOException, SAXException {

        final MarkupModel editorModel = editor.getMarkupModel();
        final Document document = editor.getDocument();
        TextAttributes attributes = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(EditorColors.WRITE_SEARCH_RESULT_ATTRIBUTES);
        TextAttributes softerAttributes = attributes.clone();

        XmlFile xmlFile = (XmlFile) psiFile;
        InputStream is = new ByteArrayInputStream(xmlFile.getText().getBytes());
        org.w3c.dom.Document doc = PositionalXMLReader.readXML(is);
        is.close();

        int nodeIndex = 0;
        NodeList nodeList = doc.getElementsByTagName("groupId");

        MavenXpp3Reader Xpp3Reader = new MavenXpp3Reader();
        editorModel.removeAllHighlighters();
        VirtualFile vFile = psiFile.getOriginalFile().getVirtualFile();
        String path = vFile.getPath();
        mavenObjectList.clear();
        int currentLine;
        try {
            Model model = Xpp3Reader.read(new FileReader(path));
            List<Dependency> dependencies = model.getDependencies();
            String selectedTerm = null;

            for (Dependency dependency : dependencies) {
                selectedTerm = dependency.getGroupId();

                DatabaseAccess dataAccessObject = new DatabaseAccess();
                ArrayList<String> choicesArray = dataAccessObject.selectJsonAllLibraries(selectedTerm);

                if (choicesArray.size() > 0) {
                    while (!selectedTerm.equals(nodeList.item(nodeIndex).getTextContent())) {
                        nodeIndex += 1;
                    }
                    String s = (String) nodeList.item(nodeIndex).getUserData("lineNumber");
                    currentLine = Integer.valueOf(s);

                    DependencyStatement depObj = new DependencyStatement();
                    depObj.setImportLocation(currentLine-1);
                    depObj.setImportLib(Integer.parseInt(choicesArray.get(0)));
                    depObj.setImportDomain(Integer.parseInt(choicesArray.get(1)));
                    depObj.setDomainName(choicesArray.get(2));

                    if (dataAccessObject.isEnabled(Integer.parseInt(choicesArray.get(1)), projectName)) {
                        depObj.setEnableddomain(false);
                    }
                    else {
                        depObj.setEnableddomain(true);
                    }

                    mavenObjectList.add(depObj);

                    if (depObj.getEnableddomain()) {
                        editorModel.addLineHighlighter(currentLine-1,
                                DebuggerColors.BREAKPOINT_HIGHLIGHTER_LAYER + 1, softerAttributes);
                    }
                    nodeIndex += 1;
                }
            }
        }
        catch (IOException | XmlPullParserException exception) {
            exception.printStackTrace();
        }
    }
}
