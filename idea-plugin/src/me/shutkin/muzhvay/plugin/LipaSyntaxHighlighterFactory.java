package me.shutkin.muzhvay.plugin;

import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class LipaSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
  @NotNull
  @Override
  public SyntaxHighlighter getSyntaxHighlighter(Project project, VirtualFile virtualFile) {
    return new LipaSyntaxHighlighter();
  }
}