package me.shutkin.muzhvay.plugin.actions;

import com.intellij.lang.ASTNode;
import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import me.shutkin.muzhvay.lipa.JSONCodeGenerator;
import me.shutkin.muzhvay.lipa.Lipa;
import me.shutkin.muzhvay.lipa.LipaNode;
import me.shutkin.muzhvay.plugin.psi.LipaTypes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JSONCodeGenerateAction extends AnAction {
  private static final NotificationGroup notifGroup = new NotificationGroup("Muzhvay notifications", NotificationDisplayType.BALLOON, true);

  @Override
  public void actionPerformed(AnActionEvent event) {
    final List<String> generatedFiles = new ArrayList<>();
    final Project project = event.getProject();
    if (project != null) {
      final PsiManager psiManager = PsiManager.getInstance(project);
      final Collection<VirtualFile> lipaFiles = FilenameIndex.getAllFilesByExt(project, "lipa");
      for (VirtualFile lipaFile : lipaFiles) {
        PsiFile file = psiManager.findFile(lipaFile);
        if (file == null)
          continue;
        final ASTNode schemeNode = file.getNode().findChildByType(LipaTypes.SCHEME);
        if (schemeNode != null && schemeNode.getText().equals("[json-scheme]")) {
          String outputFilename = processLipaFile(lipaFile, project);
          if (outputFilename != null)
            generatedFiles.add(outputFilename);
        }
      }
    }
    final String notificationText = generatedFiles.isEmpty() ? "None deserializers were created" :
            "Following deserializers were created:\n" + generatedFiles.stream().collect(Collectors.joining("\n"));
    final Notification notification = notifGroup.createNotification(notificationText, NotificationType.INFORMATION);
    Notifications.Bus.notify(notification, project);
  }

  private String processLipaFile(VirtualFile file, Project project) {
    final Module module = ModuleUtil.findModuleForFile(file, project);
    if (module == null)
      return null;
    ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
    final VirtualFile[] sourceRoots = moduleRootManager.getSourceRoots(false);
    if (sourceRoots.length == 0)
      return null;
    final String[] sourceRootPaths = new String[sourceRoots.length];
    for (int i = 0; i < sourceRoots.length; i++)
      sourceRootPaths[i] = sourceRoots[i].getCanonicalPath();
    try {
      me.shutkin.muzhvay.lipa.LipaFile lipaFile = Lipa.parseLipa(file.getInputStream());
      if (lipaFile.getRootNode() != null) {
        final List<LipaNode> rootChildren = lipaFile.getRootNode().getChildren();
        final Optional<LipaNode> packageOptional = rootChildren.stream().filter(node -> node.getKey().equals("package")).findAny();
        if (!packageOptional.isPresent())
          return null;
        final Optional<LipaNode> classOptional = rootChildren.stream().filter(node -> node.getKey().equals("class")).findAny();
        if (!classOptional.isPresent())
          return null;
        String code = JSONCodeGenerator.generateJSONDeserializer(lipaFile.getRootNode());
        return writeCode(code, sourceRootPaths, packageOptional.get().getValues().get(0), classOptional.get().getValues().get(0));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private static String writeCode(String code, String[] sourceRoots, String classPackage, String className) throws IOException {
    final String[] dirs = classPackage.split("\\.");
    String path = selectSourceRoot(sourceRoots, dirs);
    for (String dir : dirs) path += '/' + dir;
    final File dirFile = new File(path);
    if (!dirFile.exists() && !dirFile.mkdirs())
      throw new IOException("Can't create directories for package '" + classPackage + "'");
    final File codeFile = new File(path + '/' + className + ".kt");
    if (!codeFile.exists() && !codeFile.createNewFile())
      throw new IOException("Can't create source file '" + className + ".kt' in package '" + classPackage + "'");
    final FileOutputStream stream = new FileOutputStream(codeFile);
    stream.write(code.getBytes());
    stream.flush();
    stream.close();
    return codeFile.getCanonicalPath();
  }

  private static String selectSourceRoot(String[] sourceRoots, String[] packageDirs) {
    String bestRoot = sourceRoots[0];
    int bestRootDeep = 0;
    for (String sourceRoot : sourceRoots) {
      String path = sourceRoot;
      int deep;
      for (deep = 0; deep < packageDirs.length; deep++) {
        final String nextPath = path + '/' + packageDirs[deep];
        final File nextDirFile = new File(nextPath);
        final boolean ex = nextDirFile.exists();
        if (!nextDirFile.exists() || !nextDirFile.isDirectory())
          break;
        path = nextPath;
      }
      if (deep > bestRootDeep) {
        bestRoot = sourceRoot;
        bestRootDeep = deep;
      }
    }
    return bestRoot;
  }
}
