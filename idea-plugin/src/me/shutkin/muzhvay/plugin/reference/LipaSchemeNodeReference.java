package me.shutkin.muzhvay.plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import me.shutkin.muzhvay.plugin.psi.LipaKey;
import me.shutkin.muzhvay.plugin.psi.LipaNode;
import me.shutkin.muzhvay.plugin.psi.LipaValueContent;
import me.shutkin.muzhvay.plugin.utils.LipaUtil;
import me.shutkin.muzhvay.plugin.utils.SchemeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LipaSchemeNodeReference extends PsiReferenceBase<LipaKey> {
  private final LipaNode node;

  public LipaSchemeNodeReference(@NotNull LipaKey element) {
    super(element, new TextRange(0, element.getTextLength()));
    node = LipaUtil.getParentNode(element);
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    if (node == null)
      return null;

    final List<LipaNode> scheme = SchemeUtil.getSchemeForElement(myElement);
    if (scheme == null)
      return null;
    return SchemeUtil.findNodeDeclaration(node, scheme);
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    return new Object[0];
  }
}
