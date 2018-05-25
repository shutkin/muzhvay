package me.shutkin.muzhvay.plugin;

import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import org.jetbrains.annotations.NotNull;

public class LipaLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {
  @NotNull
  @Override
  public Language getLanguage() {
    return LipaLanguage.INSTANCE;
  }

  @Override
  public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType) {
    super.customizeSettings(consumer, settingsType);
  }

  @Override
  public String getCodeSample(@NotNull SettingsType settingsType) {
    return "example {" +
            "node1 foo bar <attr0 attr1> {child foo, another-child bar}," +
            "node2 value" +
            "}";
  }
}
