package me.shutkin.muzhvay.plugin.utils;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.io.URLUtil;
import me.shutkin.muzhvay.plugin.LipaException;
import me.shutkin.muzhvay.plugin.filesystem.MuzhvayHardcodedFileSystem;
import me.shutkin.muzhvay.plugin.psi.LipaFile;
import me.shutkin.muzhvay.plugin.psi.LipaNode;
import me.shutkin.muzhvay.plugin.psi.LipaTypes;
import me.shutkin.muzhvay.plugin.psi.LipaValueContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SchemeUtil {
  public enum NodeProperties {
    NO_VALUES, SINGLE_VALUE, TWO_VALUES, VALUES_REQUIRED, NO_ATTRIBUTES, CHILDREN_REQUIRED, NO_CHILDREN
  }

  @Nullable
  public static List<LipaNode> getSchemeForElement(@NotNull PsiElement element) {
    PsiElement root = element.getContainingFile();
    if (root == null)
      return null;
    final ASTNode schemeNode = root.getNode().findChildByType(LipaTypes.SCHEME);
    if (schemeNode == null)
      return null;
    final String schemeName = schemeNode.getText().substring(1, schemeNode.getTextLength() - 1);
    if (schemeName.isEmpty())
      return null;

    try {
      final LipaFile schemeFile = findScheme(element.getProject(), schemeName);
      if (schemeFile == null)
        return null;

      final LipaNode schemeRoot = schemeFile.findChildByClass(LipaNode.class);
      if (schemeRoot != null)
        return schemeRoot.getNodeChildren() != null ? schemeRoot.getNodeChildren().getNodeList() : null;
    } catch (LipaException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Nullable
  public static LipaFile findScheme(Project project, String schemeName) throws LipaException {
    final VirtualFile hardcodedScheme = VirtualFileManager.getInstance()
            .findFileByUrl(MuzhvayHardcodedFileSystem.INSTANCE.getProtocol() + URLUtil.SCHEME_SEPARATOR + schemeName + ".lipa");
    if (hardcodedScheme != null)
      return (LipaFile) PsiManager.getInstance(project).findFile(hardcodedScheme);

    final Collection<VirtualFile> schemeFiles = FilenameIndex.getVirtualFilesByName(project, schemeName + ".lipa",
            GlobalSearchScope.allScope(project));
    if (schemeFiles.isEmpty())
      return null;
    if (schemeFiles.size() > 1)
      throw new LipaException("Several encounters for scheme '" + schemeName + "' in the project");
    return (LipaFile) PsiManager.getInstance(project).findFile(schemeFiles.iterator().next());
  }

  @Nullable
  public static String getRootKey(@NotNull List<LipaNode> scheme) {
    final Optional<LipaNode> rootOptional = scheme.stream().filter(schemeNode -> "root".equals(schemeNode.getKey())).findAny();
    return rootOptional.map(LipaNode::getFirstValue).orElse(null);
  }

  @NotNull
  public static Set<NodeProperties> getNodeProperties(@NotNull LipaNode lipaNode, @NotNull List<LipaNode> scheme) {
    final Set<NodeProperties> properties = new HashSet<>();
    LipaNode schemeNodeDefinition = findNodeDefinition(lipaNode, scheme);
    if (schemeNodeDefinition == null)
      return properties;
    for (String attribute : schemeNodeDefinition.getAttributes()) {
      switch (attribute) {
        case "no-values":
          properties.add(NodeProperties.NO_VALUES);
          break;
        case "single-value":
          properties.add(NodeProperties.SINGLE_VALUE);
          break;
        case "two-values":
          properties.add(NodeProperties.TWO_VALUES);
          break;
        case "values-required":
          properties.add(NodeProperties.VALUES_REQUIRED);
          break;
        case "no-attributes":
          properties.add(NodeProperties.NO_ATTRIBUTES);
          break;
        case "children-required":
          properties.add(NodeProperties.CHILDREN_REQUIRED);
          break;
        case "no-children":
          properties.add(NodeProperties.NO_CHILDREN);
          break;
      }
    }
    return properties;
  }

  @Nullable
  public static List<String> getDefinedKeys(@NotNull LipaNode lipaNode, @NotNull List<LipaNode> scheme) {
    final LipaNode parentNode = LipaUtil.getParentNode(lipaNode);
    if (parentNode == null)
      return null;
    LipaNode schemeNodeDefinition = findNodeDefinition(parentNode, scheme);
    if (schemeNodeDefinition == null || schemeNodeDefinition.getNodeChildren() == null)
      return null;
    final Optional<LipaNode> childrenOptional = schemeNodeDefinition.getNodeChildren().getNodeList().stream()
            .filter(node -> "children".equals(node.getKey())).findAny();
    return childrenOptional.map(LipaNode::getValues).orElse(null);
  }

  @Nullable
  public static List<String> getDefinedAttributes(@NotNull LipaNode lipaNode, @NotNull List<LipaNode> scheme) {
    LipaNode schemeNodeDefinition = findNodeDefinition(lipaNode, scheme);
    if (schemeNodeDefinition == null || schemeNodeDefinition.getNodeChildren() == null)
      return null;
    final Optional<LipaNode> attributesOptional = schemeNodeDefinition.getNodeChildren().getNodeList().stream()
            .filter(node -> "attributes".equals(node.getKey())).findAny();
    return attributesOptional.map(LipaNode::getValues).orElse(null);
  }

  @NotNull
  private static String getNodeFullPath(LipaNode lipaNode) {
    final Deque<String> nodesPath = new LinkedList<>();
    nodesPath.add(lipaNode.getKey());
    LipaNode node = LipaUtil.getParentNode(lipaNode);
    while (node != null) {
      final LipaNode parentNode = LipaUtil.getParentNode(node);
      if (parentNode != null) // do not add root node
        nodesPath.addFirst(node.getKey());
      node = parentNode;
    }
    return nodesPath.stream().collect(Collectors.joining("."));
  }

  @Nullable
  public static LipaValueContent findNodeDeclaration(@NotNull LipaNode lipaNode, @NotNull List<LipaNode> scheme) {
    final String fullPath = getNodeFullPath(lipaNode);
    final String nodeKey = lipaNode.getKey();
    final Stream<LipaValueContent> values = scheme.stream()
            .filter(schemeNode -> "node".equals(schemeNode.getKey()))
            .flatMap(schemeNode -> schemeNode.getNodeBody().getValueContentList().stream())
            .filter(content -> content.getValue().equals(fullPath) || content.getValue().equals(nodeKey))
            .sorted(Comparator.comparingInt(content -> content.getValue().equals(nodeKey) ? 1 : -1));
    return values.findAny().orElse(null);
  }


  @Nullable
  private static LipaNode findNodeDefinition(@NotNull LipaNode lipaNode, @NotNull List<LipaNode> scheme) {
    final LipaValueContent nodeDeclaration = findNodeDeclaration(lipaNode, scheme);
    if (nodeDeclaration != null)
      return LipaUtil.getParentNode(nodeDeclaration);
    return null;
  }
}
