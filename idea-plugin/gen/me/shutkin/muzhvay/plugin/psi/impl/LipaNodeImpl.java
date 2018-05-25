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

public class LipaNodeImpl extends ASTWrapperPsiElement implements LipaNode {

  public LipaNodeImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LipaVisitor visitor) {
    visitor.visitNode(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LipaVisitor) accept((LipaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public LipaNodeBody getNodeBody() {
    return findNotNullChildByClass(LipaNodeBody.class);
  }

  @Override
  @Nullable
  public LipaNodeChildren getNodeChildren() {
    return findChildByClass(LipaNodeChildren.class);
  }

  @NotNull
  public String getKey() {
    return LipaPsiImplUtils.getKey(this);
  }

  @NotNull
  public List<String> getValues() {
    return LipaPsiImplUtils.getValues(this);
  }

  @Nullable
  public String getFirstValue() {
    return LipaPsiImplUtils.getFirstValue(this);
  }

  @NotNull
  public List<String> getAttributes() {
    return LipaPsiImplUtils.getAttributes(this);
  }

}
