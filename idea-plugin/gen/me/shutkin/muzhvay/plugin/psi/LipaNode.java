// This is a generated file. Not intended for manual editing.
package me.shutkin.muzhvay.plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface LipaNode extends PsiElement {

  @NotNull
  LipaNodeBody getNodeBody();

  @Nullable
  LipaNodeChildren getNodeChildren();

  @NotNull
  String getKey();

  @NotNull
  List<String> getValues();

  @Nullable
  String getFirstValue();

  @NotNull
  List<String> getAttributes();

}
