package org.intellij.sdk.editor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class UserProfileAction extends AnAction {

    public UserProfileAction() {
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {
        UserProfileDialog userProfileDialog = new UserProfileDialog("User Profile");
        userProfileDialog.init();

    }
}
