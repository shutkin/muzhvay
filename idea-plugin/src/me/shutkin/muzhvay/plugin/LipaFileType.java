package me.shutkin.muzhvay.plugin;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class LipaFileType extends LanguageFileType {
  public static final LipaFileType INSTANCE = new LipaFileType();

  private LipaFileType() {
    super(LipaLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public String getName() {
    return "Lipa file";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "Lipa language file";
  }

  @NotNull
  @Override
  public String getDefaultExtension() {
    return "lipa";
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return LipaIcons.FILE;
  }
}
