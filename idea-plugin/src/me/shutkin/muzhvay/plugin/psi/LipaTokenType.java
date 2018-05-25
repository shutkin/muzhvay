package me.shutkin.muzhvay.plugin.psi;

import com.intellij.psi.tree.IElementType;
import me.shutkin.muzhvay.plugin.LipaLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class LipaTokenType extends IElementType {
  public LipaTokenType(@NotNull @NonNls String debugName) {
    super(debugName, LipaLanguage.INSTANCE);
  }

  @Override
  public String toString() {
    return "LipaTokenType." + super.toString();
  }
}
