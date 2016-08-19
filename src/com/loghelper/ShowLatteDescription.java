package com.loghelper;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.psi.*;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.psi.impl.PsiElementFactoryImpl;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiMethodUtil;
import com.intellij.psi.util.PsiUtilBase;
import com.oracle.tools.packager.Log;
import com.sun.istack.internal.NotNull;

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
        //PsiManager.getInstance(project)
        GlobalSearchScope searchScope = GlobalSearchScope.fileScope(project,virtualFile);
        String name = virtualFile.getName().substring(0,virtualFile.getName().length()-virtualFile.getExtension().length()-1);
        //Messages.showMessageDialog(project, name, "title", Messages.getInformationIcon());
        PsiClass[] psiClasses = PsiShortNamesCache.getInstance(project).getClassesByName(name,searchScope);
        PsiClass psiClass = psiClasses[0];
        dealAllJavaMethod(psiClass);
        //Messages.showMessageDialog(project, psiClass.getName(), "title", Messages.getInformationIcon());
    }

    private void dealAllJavaMethod(PsiClass psiClass){
        PsiMethod[] psiMethods = psiClass.getMethods();
        for (PsiMethod psiMethod:psiMethods){
            //Messages.showMessageDialog(project, psiMethod.getName(), "title", Messages.getInformationIcon());
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    new WriteCommandAction(project) {
                        @Override
                        protected void run(@NotNull Result result) throws Throwable {
                            //writing to file
                            PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
                            PsiJavaToken lBrace = psiMethod.getBody().getLBrace();
                            psiMethod.getBody().addAfter(factory.createFieldFromText("long startNanos = System.nanoTime();",psiMethod),lBrace);
                            psiMethod.getBody().add(factory.createFieldFromText("long stopNanos = System.nanoTime();", psiMethod));
                            //System.out.println(stopNanos-startNanos+"")
                            psiMethod.getBody().add(factory.createCodeBlockFromText("{System.out.println(stopNanos-startNanos+\"\");}", psiMethod));
                        }
                    }.execute();
                }
            });
        }
    }
}
