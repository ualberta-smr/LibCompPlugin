import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerAdapter;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import smr.cs.ualberta.libcomp.DatabaseAccess;
import org.jetbrains.annotations.NotNull;
import smr.cs.ualberta.libcomp.action.ReplacementAction;
import javax.swing.*;
import java.io.IOException;

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
            public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
               // super.fileOpened(source, file);
            }

            @Override
            public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
               // super.fileClosed(source, file);
            }

            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {
               // super.selectionChanged(event);
                ReplacementAction actionPerformed = new ReplacementAction();
                try {
                    actionPerformed.detectAllOpenEditors();
                } catch (IOException e) {
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}