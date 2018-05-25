// This is a generated file. Not intended for manual editing.
package me.shutkin.muzhvay.plugin.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static me.shutkin.muzhvay.plugin.psi.LipaTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import me.shutkin.muzhvay.plugin.psi.*;

public class LipaAttributesImpl extends ASTWrapperPsiElement implements LipaAttributes {

  public LipaAttributesImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LipaVisitor visitor) {
    visitor.visitAttributes(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LipaVisitor) accept((LipaVisitor)visitor);
    else super.accept(visitor);
  }

}
