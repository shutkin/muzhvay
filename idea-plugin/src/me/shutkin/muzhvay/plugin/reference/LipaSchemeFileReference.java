package me.shutkin.muzhvay.plugin.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import me.shutkin.muzhvay.plugin.LipaException;
import me.shutkin.muzhvay.plugin.psi.LipaFile;
import me.shutkin.muzhvay.plugin.psi.LipaScheme;
import me.shutkin.muzhvay.plugin.utils.SchemeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LipaSchemeFileReference extends PsiReferenceBase<LipaScheme> {
  private final String schemeName;

  public LipaSchemeFileReference(@NotNull LipaScheme element) {
    super(element, new TextRange(1, element.getTextLength() - 1));
    schemeName = element.getText().substring(1, element.getTextLength() - 1);
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    try {
      final LipaFile schemeFile = SchemeUtil.findScheme(myElement.getProject(), schemeName);
      if (schemeFile != null)
        return schemeFile;
    } catch (LipaException e) {
      e.printStackTrace();
    }
    return null;
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    return new Object[0];
  }
}
