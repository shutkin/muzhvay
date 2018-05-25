package me.shutkin.muzhvay.plugin.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import me.shutkin.muzhvay.plugin.psi.LipaFile;
import me.shutkin.muzhvay.plugin.psi.LipaNode;

import java.util.Collection;

public class LipaUtil {
  public static LipaNode getParentNode(PsiElement element) {
    PsiElement parent = element.getParent();
    while (parent != null) {
      if (parent instanceof LipaNode)
        return (LipaNode) parent;
      if (parent instanceof LipaFile)
        break;
      parent = parent.getParent();
    }
    return null;
  }
}
