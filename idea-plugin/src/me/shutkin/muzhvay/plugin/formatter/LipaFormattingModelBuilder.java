package me.shutkin.muzhvay.plugin.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import me.shutkin.muzhvay.plugin.LipaLanguage;
import me.shutkin.muzhvay.plugin.psi.LipaTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LipaFormattingModelBuilder implements FormattingModelBuilder {
  @NotNull
  @Override
  public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
    return FormattingModelProvider.createFormattingModelForPsiFile(
            element.getContainingFile(),
            new LipaBlock(element.getNode(), Wrap.createWrap(WrapType.NONE, false), Alignment.createAlignment(), createSpacingBuilder(settings)),
            settings
    );
  }

  @Nullable
  @Override
  public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
    return null;
  }

  private static SpacingBuilder createSpacingBuilder(CodeStyleSettings settings) {
    return new SpacingBuilder(settings, LipaLanguage.INSTANCE)
            .before(LipaTypes.CHILDREN_BEGIN).spaceIf(LipaCodeStyleSettings.SPACE_BEFORE_CHILDREN)
            .after(LipaTypes.CHILDREN_BEGIN).spaceIf(LipaCodeStyleSettings.SPACE_INSIDE_CHILDREN);
  }
}
