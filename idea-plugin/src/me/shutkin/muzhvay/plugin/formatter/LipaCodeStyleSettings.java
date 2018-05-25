package me.shutkin.muzhvay.plugin.formatter;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;

public class LipaCodeStyleSettings extends CustomCodeStyleSettings {
  protected LipaCodeStyleSettings(CodeStyleSettings settings) {
    super("LipaCodeStyleSettings", settings);
  }

  static boolean SPACE_BEFORE_CHILDREN = true;
  static boolean SPACE_INSIDE_CHILDREN = true;
}
