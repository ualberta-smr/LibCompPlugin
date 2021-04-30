package smr.cs.ualberta.libcomp.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import smr.cs.ualberta.libcomp.dialog.UserProfileDialog;

/**
 * The ActionUserProfile class is the action for sending feedback entries to the database
 * This is triggered by the user opening the user profile dialog via the Tools menu
 */

public class UserProfileAction extends AnAction {

    public UserProfileAction() {
    }

    public void update(@NotNull final AnActionEvent event) {
        event.getPresentation().setVisible(true);
        event.getPresentation().setEnabled(true);
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {
        UserProfileDialog userProfileDialog = new UserProfileDialog("User Profile");
        userProfileDialog.init();
    }
}
