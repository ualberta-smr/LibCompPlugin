package smr.cs.ualberta.libcomp.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * The ActionUserProfile class is the action for sending feedback entries to the database
 * This is triggered by the user opening the user profile dialog via the Tools menu
 */

public class UserProfile extends AnAction {

    public UserProfile() {
    }

    public void update(@NotNull final AnActionEvent event) {
        event.getPresentation().setVisible(true);
        event.getPresentation().setEnabled(true);
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {
        smr.cs.ualberta.libcomp.dialog.UserProfile userProfile = new smr.cs.ualberta.libcomp.dialog.UserProfile("User Profile");
        userProfile.init();

    }
}
