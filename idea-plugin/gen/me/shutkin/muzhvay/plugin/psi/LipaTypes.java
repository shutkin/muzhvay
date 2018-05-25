// This is a generated file. Not intended for manual editing.
package me.shutkin.muzhvay.plugin.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import me.shutkin.muzhvay.plugin.psi.impl.*;

public interface LipaTypes {

  IElementType ATTRIBUTES = new LipaElementType("ATTRIBUTES");
  IElementType KEY = new LipaElementType("KEY");
  IElementType NODE = new LipaElementType("NODE");
  IElementType NODE_BODY = new LipaElementType("NODE_BODY");
  IElementType NODE_CHILDREN = new LipaElementType("NODE_CHILDREN");
  IElementType SCHEME = new LipaElementType("SCHEME");
  IElementType VALUE_CONTENT = new LipaElementType("VALUE_CONTENT");

  IElementType ATTRIBUTE = new LipaTokenType("ATTRIBUTE");
  IElementType ATTR_BEGIN = new LipaTokenType("ATTR_BEGIN");
  IElementType ATTR_END = new LipaTokenType("ATTR_END");
  IElementType CHILDREN_BEGIN = new LipaTokenType("CHILDREN_BEGIN");
  IElementType CHILDREN_END = new LipaTokenType("CHILDREN_END");
  IElementType COMMENT = new LipaTokenType("COMMENT");
  IElementType KEY_TOKEN = new LipaTokenType("KEY_TOKEN");
  IElementType SCHEME_TOKEN = new LipaTokenType("SCHEME_TOKEN");
  IElementType SEPARATOR = new LipaTokenType("SEPARATOR");
  IElementType VALUE = new LipaTokenType("VALUE");
  IElementType VALUE_STRING = new LipaTokenType("VALUE_STRING");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == ATTRIBUTES) {
        return new LipaAttributesImpl(node);
      }
      else if (type == KEY) {
        return new LipaKeyImpl(node);
      }
      else if (type == NODE) {
        return new LipaNodeImpl(node);
      }
      else if (type == NODE_BODY) {
        return new LipaNodeBodyImpl(node);
      }
      else if (type == NODE_CHILDREN) {
        return new LipaNodeChildrenImpl(node);
      }
      else if (type == SCHEME) {
        return new LipaSchemeImpl(node);
      }
      else if (type == VALUE_CONTENT) {
        return new LipaValueContentImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
