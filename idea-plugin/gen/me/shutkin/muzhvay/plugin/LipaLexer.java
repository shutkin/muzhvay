package me.shutkin.muzhvay.plugin;

import com.intellij.lexer.FlexAdapter;

public class LipaLexer extends FlexAdapter {
  public LipaLexer() {
    super(new _LipaLexer());
  }
}
