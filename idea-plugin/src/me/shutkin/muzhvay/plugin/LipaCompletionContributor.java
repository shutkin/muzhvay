package me.shutkin.muzhvay.plugin;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.util.ProcessingContext;
import me.shutkin.muzhvay.plugin.psi.LipaNode;
import me.shutkin.muzhvay.plugin.psi.LipaTypes;
import me.shutkin.muzhvay.plugin.utils.LipaUtil;
import me.shutkin.muzhvay.plugin.utils.SchemeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class LipaCompletionContributor extends CompletionContributor {
  public LipaCompletionContributor() {
    extend(
            CompletionType.BASIC,
            psiElement(LipaTypes.SCHEME_TOKEN).withLanguage(LipaLanguage.INSTANCE),
            new CompletionProvider<CompletionParameters>() {
              @Override
              protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                final List<String> schemes = getSchemes(parameters.getPosition().getProject());
                if (schemes != null)
                  schemes.forEach(attr -> result.addElement(LookupElementBuilder.create(attr)));
              }
            }
    );

    extend(
            CompletionType.BASIC,
            psiElement(LipaTypes.ATTRIBUTE).withLanguage(LipaLanguage.INSTANCE),
            new CompletionProvider<CompletionParameters>() {
              @Override
              protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                final List<String> definedAttributes = getAttributes(parameters.getPosition());
                if (definedAttributes != null)
                  definedAttributes.forEach(attr -> result.addElement(LookupElementBuilder.create(attr)));
              }
            }
    );

    extend(
            CompletionType.BASIC,
            psiElement(LipaTypes.KEY_TOKEN).withLanguage(LipaLanguage.INSTANCE),
            new CompletionProvider<CompletionParameters>() {
              @Override
              protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                final List<String> definedKeys = getKeys(parameters.getPosition());
                if (definedKeys != null)
                  definedKeys.forEach(attr -> result.addElement(LookupElementBuilder.create(attr)));
              }
            }
    );
  }

  @Nullable
  private static List<String> getKeys(@NotNull PsiElement element) {
    final List<LipaNode> scheme = SchemeUtil.getSchemeForElement(element);
    if (scheme == null)
      return null;
    final LipaNode lipaNode = LipaUtil.getParentNode(element);
    return lipaNode != null ? SchemeUtil.getDefinedKeys(lipaNode, scheme) : null;
  }

  @Nullable
  private static List<String> getAttributes(@NotNull PsiElement element) {
    final List<LipaNode> scheme = SchemeUtil.getSchemeForElement(element);
    if (scheme == null)
      return null;
    final LipaNode lipaNode = LipaUtil.getParentNode(element);
    return lipaNode != null ? SchemeUtil.getDefinedAttributes(lipaNode, scheme) : null;
  }

  private static List<String> getSchemes(Project project) {
    final PsiManager psiManager = PsiManager.getInstance(project);
    final Collection<VirtualFile> lipaFiles = FilenameIndex.getAllFilesByExt(project, "lipa");
    return lipaFiles.stream().filter(file -> {
      final PsiFile psiFile = psiManager.findFile(file);
      if (psiFile == null) return false;
      final ASTNode schemeNode = psiFile.getNode().findChildByType(LipaTypes.SCHEME);
      if (schemeNode == null) return false;
      final String schemeName = schemeNode.getText().substring(1, schemeNode.getTextLength() - 1);
      return schemeName.equals("lipa-scheme");
    }).map(VirtualFile::getNameWithoutExtension).collect(Collectors.toList());
  }
}
