package me.shutkin.muzhvay.plugin.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import me.shutkin.muzhvay.plugin.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class LipaFile extends PsiFileBase {
  public LipaFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, LipaLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return LipaFileType.INSTANCE;
  }

  @Override
  public String toString() {
    return "Lipa File";
  }

  @Override
  public Icon getIcon(int flags) {
    return super.getIcon(flags);
  }
}
