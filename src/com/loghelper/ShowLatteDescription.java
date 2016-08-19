package com.loghelper;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.psi.PsiDirectory;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.psi.util.PsiUtilBase;
import com.oracle.tools.packager.Log;

/**
 * Created by 835127729qq.com on 16/8/19.
 */
public class ShowLatteDescription extends AnAction{
    Project project;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        PsiDirectory baseDir  = PsiDirectoryFactory.getInstance(project).createDirectory(project.getBaseDir());
        printClassNames(project.getBaseDir());
        /*
        PsiDirectory src = baseDir.findSubdirectory("src");
        if(src==null){
            return;
        }
        //printClassNames(src);
        //src.getSubdirectories()
        Messages.showMessageDialog(project,src.getName(), "title"+src.getFiles().length,Messages.getInformationIcon());
        */
    }

    public void printClassNames(VirtualFile root){
        VfsUtilCore.iterateChildrenRecursively(root, null, new ContentIterator() {
            @Override
            public boolean processFile(VirtualFile virtualFile) {
                if(virtualFile.getFileType().getName()==JavaFileType.INSTANCE.getName()){
                    dealWithJava(virtualFile);
                }
                for (VirtualFile v : virtualFile.getChildren()) {
                    printClassNames(v);
                }
                return false;
            }
        });
    }

    private void dealWithJava(VirtualFile virtualFile){
        Messages.showMessageDialog(project, virtualFile.getPath(), "title" + virtualFile.getFileType().getName(), Messages.getInformationIcon());
    }
}
