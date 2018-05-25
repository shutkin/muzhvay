package me.shutkin.muzhvay.plugin;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import me.shutkin.muzhvay.plugin.psi.LipaTypes;

%%

%{
  public _LipaLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _LipaLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

WHITE_SPACE=[\ \n\t\f\R]
COMMENT=#.*#
SCHEME_TOKEN=\[[a-zA-Z0-9\_\-]+\]
SEPARATOR=\,
CHILDREN_BEGIN=\{
CHILDREN_END=\}
ATTR_BEGIN=\<
ATTR_END=\>
VALUE_STRING=('([^'\\]|\\.)*'|\"([^\"\\]|\\.)*\")
VALUE=[0-9a-zA-Z\_\-\.]+
KEY_TOKEN=[a-zA-Z][0-9a-zA-Z\_\-\.]*

%state KEY_STATE
%state VALUE_STATE
%state ATTR_STATE

%%

<YYINITIAL> {WHITE_SPACE}+ { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }
<YYINITIAL> {SCHEME_TOKEN} { yybegin(YYINITIAL); return LipaTypes.SCHEME_TOKEN; }
<YYINITIAL> {COMMENT} { yybegin(YYINITIAL); return LipaTypes.COMMENT; }
<YYINITIAL> {KEY_TOKEN} { yybegin(KEY_STATE); return LipaTypes.KEY_TOKEN; }
<KEY_STATE> {WHITE_SPACE}+ { yybegin(VALUE_STATE); return TokenType.WHITE_SPACE; }
<VALUE_STATE> {WHITE_SPACE}+ { yybegin(VALUE_STATE); return TokenType.WHITE_SPACE; }
<VALUE_STATE> {VALUE} { yybegin(VALUE_STATE); return LipaTypes.VALUE; }
<VALUE_STATE> {VALUE_STRING} { yybegin(VALUE_STATE); return LipaTypes.VALUE_STRING; }
<VALUE_STATE> {ATTR_BEGIN} { yybegin(ATTR_STATE); return LipaTypes.ATTR_BEGIN; }
<ATTR_STATE> {WHITE_SPACE}+ { yybegin(ATTR_STATE); return TokenType.WHITE_SPACE; }
<ATTR_STATE> {VALUE} { yybegin(ATTR_STATE); return LipaTypes.ATTRIBUTE; }
<ATTR_STATE> {ATTR_END} { yybegin(VALUE_STATE); return LipaTypes.ATTR_END; }
<VALUE_STATE> {SEPARATOR} { yybegin(YYINITIAL); return LipaTypes.SEPARATOR; }
<VALUE_STATE> {CHILDREN_BEGIN} { yybegin(YYINITIAL); return LipaTypes.CHILDREN_BEGIN; }
<VALUE_STATE> {CHILDREN_END} { yybegin(VALUE_STATE); return LipaTypes.CHILDREN_END; }
. { return TokenType.BAD_CHARACTER; }
