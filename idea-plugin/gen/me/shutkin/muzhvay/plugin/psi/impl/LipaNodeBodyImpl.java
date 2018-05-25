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

public class LipaNodeBodyImpl extends ASTWrapperPsiElement implements LipaNodeBody {

  public LipaNodeBodyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LipaVisitor visitor) {
    visitor.visitNodeBody(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LipaVisitor) accept((LipaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LipaAttributes getAttributes() {
    return findChildByClass(LipaAttributes.class);
  }

  @Override
  @NotNull
  public LipaKey getKey() {
    return findNotNullChildByClass(LipaKey.class);
  }

  @Override
  @NotNull
  public List<LipaValueContent> getValueContentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LipaValueContent.class);
  }

}
