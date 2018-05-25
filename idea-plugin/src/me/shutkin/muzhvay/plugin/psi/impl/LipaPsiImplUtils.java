package me.shutkin.muzhvay.plugin.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.roots.FileIndex;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.light.LightPackageReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceHelper;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceUtil;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.PsiFileReference;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import me.shutkin.muzhvay.plugin.LipaException;
import me.shutkin.muzhvay.plugin.psi.*;
import me.shutkin.muzhvay.plugin.reference.LipaSchemeFileReference;
import me.shutkin.muzhvay.plugin.reference.LipaSchemeNodeReference;
import me.shutkin.muzhvay.plugin.utils.SchemeUtil;
import org.gradle.plugins.ide.eclipse.model.internal.FileReferenceFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LipaPsiImplUtils {
  /* LipaValueContent */

  public static String getName(LipaValueContent valueContent) {
    return valueContent.getText();
  }

  public static PsiElement setName(LipaValueContent valueContent, String name) {
    // todo: implement
    return valueContent;
  }

  @Nullable
  public static PsiElement getNameIdentifier(LipaValueContent valueContent) {
    return valueContent;
  }


  /* LipaScheme */
  public static PsiReference getReference(LipaScheme scheme) {
    return new LipaSchemeFileReference(scheme);
  }

  /* LipaKey */
  public static PsiReference getReference(LipaKey key) {
    return new LipaSchemeNodeReference(key);
  }


  /* LipaValueContent */

  @NotNull
  public static String getValue(LipaValueContent valueContent) {
    final PsiElement valueElement = valueContent.getFirstChild();
    if (valueElement != null) {
      final String text = valueElement.getText();
      if (valueElement.getNode().getElementType() == LipaTypes.VALUE_STRING) {
        if (text.length() < 3)
          return "";
        else
          return text.substring(1, text.length() - 1);
      }
      return text;
    }
    return "";
  }


  /* LipaNode */

  @NotNull
  public static String getKey(LipaNode node) {
    final ASTNode keyNode = node.getNodeBody().getNode().findChildByType(LipaTypes.KEY);
    return keyNode != null ? keyNode.getText() : "";
  }

  @NotNull
  public static List<String> getValues(LipaNode node) {
    return node.getNodeBody().getValueContentList().stream().map(LipaValueContent::getValue).collect(Collectors.toList());
  }

  @Nullable
  public static String getFirstValue(LipaNode node) {
    final List<String> values = getValues(node);
    if (values.isEmpty())
      return null;
    return values.get(0);
  }

  @NotNull
  public static List<String> getAttributes(LipaNode node) {
    final LipaAttributes attributesElement = node.getNodeBody().getAttributes();
    if (attributesElement == null)
      return Collections.emptyList();
    final List<String> attributes = new ArrayList<>();
    ASTNode childNode = attributesElement.getNode().getFirstChildNode();
    while (childNode != null) {
      if (childNode.getElementType() == LipaTypes.ATTRIBUTE)
        attributes.add(childNode.getText());
      childNode = childNode.getTreeNext();
    }
    return attributes;
  }
}
