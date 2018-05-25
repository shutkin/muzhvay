package me.shutkin.muzhvay.plugin;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import me.shutkin.muzhvay.plugin.psi.LipaTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class LipaSyntaxHighlighter extends SyntaxHighlighterBase {
  private static final TextAttributesKey KEY =
          createTextAttributesKey("LIPA_KEY", DefaultLanguageHighlighterColors.KEYWORD);
  private static final TextAttributesKey VALUE =
          createTextAttributesKey("LIPA_VALUE", DefaultLanguageHighlighterColors.PARAMETER);
  private static final TextAttributesKey NUMBER =
          createTextAttributesKey("LIPA_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
  private static final TextAttributesKey STRING =
          createTextAttributesKey("LIPA_STRING", DefaultLanguageHighlighterColors.STRING);
  private static final TextAttributesKey ATTRIBUTE =
          createTextAttributesKey("LIPA_ATTRIBUTE", DefaultLanguageHighlighterColors.CONSTANT);
  private static final TextAttributesKey BRACES =
          createTextAttributesKey("LIPA_BRACES", DefaultLanguageHighlighterColors.BRACES);
  private static final TextAttributesKey COMMA =
          createTextAttributesKey("LIPA_COMMA", DefaultLanguageHighlighterColors.COMMA);
  private static final TextAttributesKey COMMENT =
          createTextAttributesKey("LIPA_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
  private static final TextAttributesKey SCHEME =
          createTextAttributesKey("LIPA_SCHEME", DefaultLanguageHighlighterColors.METADATA);

  private static final TextAttributesKey[] KEY_KEYS = new TextAttributesKey[]{KEY};
  private static final TextAttributesKey[] VALUE_KEYS = new TextAttributesKey[]{VALUE};
  private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
  private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
  private static final TextAttributesKey[] ATTRIBUTE_KEYS = new TextAttributesKey[]{ATTRIBUTE};
  private static final TextAttributesKey[] BRACES_KEYS = new TextAttributesKey[]{BRACES};
  private static final TextAttributesKey[] COMMA_KEYS = new TextAttributesKey[]{COMMA};
  private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
  private static final TextAttributesKey[] SCHEME_KEYS = new TextAttributesKey[]{SCHEME};
  private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

  @NotNull
  @Override
  public Lexer getHighlightingLexer() {
    return new LipaLexer();
  }

  @NotNull
  @Override
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
    if (tokenType.equals(LipaTypes.KEY_TOKEN))
      return KEY_KEYS;
    if (tokenType.equals(LipaTypes.VALUE))
      return VALUE_KEYS;
    //if (tokenType.equals(LipaTypes.VALUE_NUMBER))
    //  return NUMBER_KEYS;
    if (tokenType.equals(LipaTypes.VALUE_STRING))
      return STRING_KEYS;
    if (tokenType.equals(LipaTypes.ATTRIBUTE) || tokenType.equals(LipaTypes.ATTR_BEGIN) || tokenType.equals(LipaTypes.ATTR_END)
            || tokenType.equals(LipaTypes.ATTRIBUTES))
      return ATTRIBUTE_KEYS;
    if (tokenType.equals(LipaTypes.CHILDREN_BEGIN) || tokenType.equals(LipaTypes.CHILDREN_END))
      return BRACES_KEYS;
    if (tokenType.equals(LipaTypes.SEPARATOR))
      return COMMA_KEYS;
    if (tokenType.equals(LipaTypes.COMMENT))
      return COMMENT_KEYS;
    if (tokenType.equals(LipaTypes.SCHEME_TOKEN))
      return SCHEME_KEYS;
    return EMPTY_KEYS;
  }
}
