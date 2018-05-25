package me.shutkin.muzhvay.plugin;

import com.intellij.lang.Language;

public class LipaLanguage extends Language {
  public static final LipaLanguage INSTANCE = new LipaLanguage();

  private LipaLanguage() {
    super("Lipa");
  }
}
