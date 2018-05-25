// This is a generated file. Not intended for manual editing.
package me.shutkin.muzhvay.plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public interface LipaValueContent extends PsiNameIdentifierOwner {

  @NotNull
  String getValue();

  @Nullable
  PsiElement getNameIdentifier();

  String getName();

  PsiElement setName(String name);

}
