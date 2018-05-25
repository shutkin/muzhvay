package me.shutkin.muzhvay.plugin;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import me.shutkin.muzhvay.plugin.psi.LipaNode;
import me.shutkin.muzhvay.plugin.psi.LipaTypes;
import me.shutkin.muzhvay.plugin.utils.LipaUtil;
import me.shutkin.muzhvay.plugin.utils.SchemeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LipaAnnotator implements Annotator {
  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (element.getNode().getElementType() == LipaTypes.KEY) {
      final List<LipaNode> scheme = SchemeUtil.getSchemeForElement(element);
      if (scheme != null)
        annotateKeyByScheme(element, scheme, holder);
    } else if (element.getNode().getElementType() == LipaTypes.ATTRIBUTE) {
      final List<LipaNode> scheme = SchemeUtil.getSchemeForElement(element);
      if (scheme != null)
        annotateAttributeByScheme(element, scheme, holder);
    } else if (element instanceof LipaNode) {
      final List<LipaNode> scheme = SchemeUtil.getSchemeForElement(element);
      if (scheme != null)
        annotateNodeByScheme((LipaNode) element, scheme, holder);
    }
  }

  private static void annotateKeyByScheme(@NotNull PsiElement keyElement, @NotNull List<LipaNode> scheme, @NotNull AnnotationHolder holder) {
    LipaNode lipaNode = LipaUtil.getParentNode(keyElement);
    if (lipaNode == null)
      return;

    if (LipaUtil.getParentNode(lipaNode) == null) {
      final String rootKey = SchemeUtil.getRootKey(scheme);
      if (rootKey != null && !rootKey.equals(keyElement.getText()))
        holder.createErrorAnnotation(keyElement, "Root must be '" + rootKey + "'");
    }

    final List<String> expectedKeys = SchemeUtil.getDefinedKeys(lipaNode, scheme);
    if (expectedKeys != null && expectedKeys.stream().noneMatch(key -> key.equals(keyElement.getText()))) {
      holder.createErrorAnnotation(keyElement, "Expected keys: " + expectedKeys.stream().collect(Collectors.joining(", ")));
    }
  }

  private static void annotateAttributeByScheme(@NotNull PsiElement attributeElement, @NotNull List<LipaNode> scheme, @NotNull AnnotationHolder holder) {
    LipaNode lipaNode = LipaUtil.getParentNode(attributeElement);
    if (lipaNode == null)
      return;

    final List<String> expectedAttributes = SchemeUtil.getDefinedAttributes(lipaNode, scheme);
    if (expectedAttributes != null && expectedAttributes.indexOf(attributeElement.getText()) < 0) {
      holder.createErrorAnnotation(attributeElement, "Expected attributes: " + expectedAttributes.stream().collect(Collectors.joining(", ")));
    }
  }

  private static void annotateNodeByScheme(@NotNull LipaNode node, @NotNull List<LipaNode> scheme, @NotNull AnnotationHolder holder) {
    final Set<SchemeUtil.NodeProperties> nodeProperties = SchemeUtil.getNodeProperties(node, scheme);

    final int valuesQuantity = node.getNodeBody().getValueContentList().size();
    if (nodeProperties.contains(SchemeUtil.NodeProperties.SINGLE_VALUE) && valuesQuantity != 1)
      holder.createErrorAnnotation(node, "Node '" + node.getKey() + "' must have one value");
    if (nodeProperties.contains(SchemeUtil.NodeProperties.TWO_VALUES) && valuesQuantity != 2)
      holder.createErrorAnnotation(node, "Node '" + node.getKey() + "' must have two values");
    if (nodeProperties.contains(SchemeUtil.NodeProperties.NO_VALUES) && valuesQuantity != 0)
      holder.createErrorAnnotation(node, "Node '" + node.getKey() + "' must not have any values");
    if (nodeProperties.contains(SchemeUtil.NodeProperties.VALUES_REQUIRED) && valuesQuantity == 0)
      holder.createErrorAnnotation(node, "Node '" + node.getKey() + "' must have values");

    final int childrenQuantity = node.getNodeChildren() != null ? node.getNodeChildren().getNodeList().size() : 0;
    if (nodeProperties.contains(SchemeUtil.NodeProperties.CHILDREN_REQUIRED) && childrenQuantity == 0)
      holder.createErrorAnnotation(node, "Node '" + node.getKey() + "' must have children");
    if (nodeProperties.contains(SchemeUtil.NodeProperties.NO_CHILDREN) && childrenQuantity != 0)
      holder.createErrorAnnotation(node, "Node '" + node.getKey() + "' must not have any children");

    if (nodeProperties.contains(SchemeUtil.NodeProperties.NO_ATTRIBUTES) && node.getNodeBody().getAttributes() != null)
      holder.createErrorAnnotation(node.getNodeBody().getAttributes(), "Node '" + node.getKey() + "' must not have any attributes");
  }
}
