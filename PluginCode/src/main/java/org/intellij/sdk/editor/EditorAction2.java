package org.intellij.sdk.editor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.xdebugger.ui.DebuggerColors;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class EditorAction2 extends AnAction {
    public EditorAction2() {
      //  JOptionPane.showMessageDialog(null,"message 1");
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {

        // JOptionPane.showMessageDialog(null,"message 2");
        UserProfile f = new UserProfile("User Profile");
        f.init();

    }
}
