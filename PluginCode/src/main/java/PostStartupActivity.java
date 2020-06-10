import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
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