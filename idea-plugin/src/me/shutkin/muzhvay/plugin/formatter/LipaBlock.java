package me.shutkin.muzhvay.plugin.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import me.shutkin.muzhvay.plugin.psi.LipaTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LipaBlock extends AbstractBlock {
  private static final Set<IElementType> blockNodeTypes = new HashSet<>(Arrays.asList(
          LipaTypes.SCHEME, LipaTypes.COMMENT, LipaTypes.NODE,
          LipaTypes.KEY, LipaTypes.NODE_BODY, LipaTypes.VALUE_CONTENT,
          LipaTypes.ATTR_BEGIN, LipaTypes.ATTRIBUTES, LipaTypes.ATTRIBUTE, LipaTypes.ATTR_END,
          LipaTypes.NODE_CHILDREN, LipaTypes.CHILDREN_BEGIN, LipaTypes.SEPARATOR, LipaTypes.CHILDREN_END));
  private static final Set<IElementType> nodesWithChildren = new HashSet<>(Arrays.asList(
          LipaTypes.NODE, LipaTypes.NODE_BODY, LipaTypes.NODE_CHILDREN, LipaTypes.ATTRIBUTES));

  private final SpacingBuilder spacingBuilder;

  LipaBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, SpacingBuilder spacingBuilder) {
    super(node, wrap, alignment);
    this.spacingBuilder = spacingBuilder;
  }

  @Override
  protected List<Block> buildChildren() {
    List<Block> childBlocks = new ArrayList<>();
    ASTNode childNode = myNode.getFirstChildNode();
    while (childNode != null) {
      if (blockNodeTypes.contains(childNode.getElementType())) {
        Block childBlock = new LipaBlock(childNode, Wrap.createWrap(WrapType.NORMAL, false), Alignment.createAlignment(), spacingBuilder);
        childBlocks.add(childBlock);
      }
      childNode = childNode.getTreeNext();
    }
    return childBlocks;
  }

  @Nullable
  @Override
  public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
    return spacingBuilder.getSpacing(this, child1, child2);
  }

  @Override
  public boolean isLeaf() {
    return !nodesWithChildren.contains(myNode.getElementType());
  }
}
