// This is a generated file. Not intended for manual editing.
package me.shutkin.muzhvay.plugin.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static me.shutkin.muzhvay.plugin.psi.LipaTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class LipaParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    if (t == ATTRIBUTES) {
      r = attributes(b, 0);
    }
    else if (t == KEY) {
      r = key(b, 0);
    }
    else if (t == NODE) {
      r = node(b, 0);
    }
    else if (t == NODE_BODY) {
      r = node_body(b, 0);
    }
    else if (t == NODE_CHILDREN) {
      r = node_children(b, 0);
    }
    else if (t == SCHEME) {
      r = scheme(b, 0);
    }
    else if (t == VALUE_CONTENT) {
      r = value_content(b, 0);
    }
    else {
      r = parse_root_(t, b, 0);
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return root(b, l + 1);
  }

  /* ********************************************************** */
  // ATTR_BEGIN ATTRIBUTE* ATTR_END
  public static boolean attributes(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attributes")) return false;
    if (!nextTokenIs(b, ATTR_BEGIN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ATTRIBUTES, null);
    r = consumeToken(b, ATTR_BEGIN);
    p = r; // pin = 1
    r = r && report_error_(b, attributes_1(b, l + 1));
    r = p && consumeToken(b, ATTR_END) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // ATTRIBUTE*
  private static boolean attributes_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attributes_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!consumeToken(b, ATTRIBUTE)) break;
      if (!empty_element_parsed_guard_(b, "attributes_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // KEY_TOKEN
  public static boolean key(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "key")) return false;
    if (!nextTokenIs(b, KEY_TOKEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KEY_TOKEN);
    exit_section_(b, m, KEY, r);
    return r;
  }

  /* ********************************************************** */
  // COMMENT? node_body node_children?
  public static boolean node(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "node")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NODE, "<node>");
    r = node_0(b, l + 1);
    r = r && node_body(b, l + 1);
    r = r && node_2(b, l + 1);
    exit_section_(b, l, m, r, false, node_recover_parser_);
    return r;
  }

  // COMMENT?
  private static boolean node_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "node_0")) return false;
    consumeToken(b, COMMENT);
    return true;
  }

  // node_children?
  private static boolean node_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "node_2")) return false;
    node_children(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // key value_content* attributes?
  public static boolean node_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "node_body")) return false;
    if (!nextTokenIs(b, KEY_TOKEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = key(b, l + 1);
    r = r && node_body_1(b, l + 1);
    r = r && node_body_2(b, l + 1);
    exit_section_(b, m, NODE_BODY, r);
    return r;
  }

  // value_content*
  private static boolean node_body_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "node_body_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!value_content(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "node_body_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // attributes?
  private static boolean node_body_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "node_body_2")) return false;
    attributes(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // CHILDREN_BEGIN node (SEPARATOR node)* CHILDREN_END
  public static boolean node_children(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "node_children")) return false;
    if (!nextTokenIs(b, CHILDREN_BEGIN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NODE_CHILDREN, null);
    r = consumeToken(b, CHILDREN_BEGIN);
    p = r; // pin = 1
    r = r && report_error_(b, node(b, l + 1));
    r = p && report_error_(b, node_children_2(b, l + 1)) && r;
    r = p && consumeToken(b, CHILDREN_END) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (SEPARATOR node)*
  private static boolean node_children_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "node_children_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!node_children_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "node_children_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // SEPARATOR node
  private static boolean node_children_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "node_children_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SEPARATOR);
    r = r && node(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // !(',' | '}')
  static boolean node_recover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "node_recover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !node_recover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ',' | '}'
  private static boolean node_recover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "node_recover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ",");
    if (!r) r = consumeToken(b, "}");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // scheme? node
  static boolean root(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = root_0(b, l + 1);
    r = r && node(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // scheme?
  private static boolean root_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_0")) return false;
    scheme(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // SCHEME_TOKEN
  public static boolean scheme(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "scheme")) return false;
    if (!nextTokenIs(b, SCHEME_TOKEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SCHEME_TOKEN);
    exit_section_(b, m, SCHEME, r);
    return r;
  }

  /* ********************************************************** */
  // VALUE | VALUE_STRING
  public static boolean value_content(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value_content")) return false;
    if (!nextTokenIs(b, "<value content>", VALUE, VALUE_STRING)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VALUE_CONTENT, "<value content>");
    r = consumeToken(b, VALUE);
    if (!r) r = consumeToken(b, VALUE_STRING);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  final static Parser node_recover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return node_recover(b, l + 1);
    }
  };
}
