package me.shutkin.muzhvay.plugin.psi;

import com.intellij.psi.tree.IElementType;
import me.shutkin.muzhvay.plugin.LipaLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class LipaElementType extends IElementType {
  LipaElementType(@NotNull @NonNls String debugName) {
    super(debugName, LipaLanguage.INSTANCE);
  }
}
