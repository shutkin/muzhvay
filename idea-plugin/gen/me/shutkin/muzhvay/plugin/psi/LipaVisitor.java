// This is a generated file. Not intended for manual editing.
package me.shutkin.muzhvay.plugin.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public class LipaVisitor extends PsiElementVisitor {

  public void visitAttributes(@NotNull LipaAttributes o) {
    visitPsiElement(o);
  }

  public void visitKey(@NotNull LipaKey o) {
    visitPsiElement(o);
  }

  public void visitNode(@NotNull LipaNode o) {
    visitPsiElement(o);
  }

  public void visitNodeBody(@NotNull LipaNodeBody o) {
    visitPsiElement(o);
  }

  public void visitNodeChildren(@NotNull LipaNodeChildren o) {
    visitPsiElement(o);
  }

  public void visitScheme(@NotNull LipaScheme o) {
    visitPsiElement(o);
  }

  public void visitValueContent(@NotNull LipaValueContent o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitPsiNameIdentifierOwner(@NotNull PsiNameIdentifierOwner o) {
    visitElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
