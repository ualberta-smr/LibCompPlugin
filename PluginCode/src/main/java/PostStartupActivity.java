import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;
import smr.cs.ualberta.libcomp.DatabaseAccess;
import smr.cs.ualberta.libcomp.action.ReplacementAction;
import javax.swing.*;
import java.io.IOException;
import java.util.List;

/**
 * PostStartupActivity class is triggered on load to start the plugin
 */

public class PostStartupActivity implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        DatabaseAccess dataAccessObject = new DatabaseAccess();

        MessageBusConnection connection = project.getMessageBus().connect(project);
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {


                ReplacementAction actionPerformed = new ReplacementAction();
                try {
                    actionPerformed.detectAllOpenEditors();
                } catch (IOException | SAXException e) {
                    e.printStackTrace();
                }
            }
        });

        connection.subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            @Override
            public void after(@NotNull List<? extends VFileEvent> events) {
                ReplacementAction actionPerformed = new ReplacementAction();
                try {
                    actionPerformed.detectAllOpenEditors();
                } catch (IOException | SAXException e) {
                    e.printStackTrace();
                }

            }
        });


        try {
            dataAccessObject.updateMetricsData();

            SwingUtilities.invokeLater(() -> {
                ReplacementAction actionPerformed = new ReplacementAction();
                try {
                    actionPerformed.detectAllOpenEditors();
                } catch (IOException | SAXException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}