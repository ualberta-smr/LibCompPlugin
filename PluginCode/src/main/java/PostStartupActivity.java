import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import smr.cs.ualberta.libcomp.DatabaseAccess;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Author: George Bakhtadze
 * Date: 13/01/2016
 */

public class PostStartupActivity implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        int x = 1;
        DatabaseAccess dataAccessObject = new DatabaseAccess();
        try {
            dataAccessObject.updateVersionData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}