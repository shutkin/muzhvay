package me.shutkin.muzhvay.plugin.filesystem;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.util.io.URLUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class HardcodedFile extends VirtualFile {
  @NotNull
  @Override
  public VirtualFileSystem getFileSystem() {
    return MuzhvayHardcodedFileSystem.INSTANCE;
  }

  @NotNull
  @Override
  public String getPath() {
    return MuzhvayHardcodedFileSystem.INSTANCE.getProtocol() + URLUtil.SCHEME_SEPARATOR + getName();
  }

  @Override
  public boolean isWritable() {
    return false;
  }

  @Override
  public boolean isDirectory() {
    return false;
  }

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public VirtualFile getParent() {
    return null;
  }

  @Override
  public VirtualFile[] getChildren() {
    return new VirtualFile[0];
  }

  @NotNull
  @Override
  public OutputStream getOutputStream(Object requestor, long newModificationStamp, long newTimeStamp) throws IOException {
    throw new IOException("Muzhvay hardcoded file is read only");
  }

  @NotNull
  @Override
  public byte[] contentsToByteArray() {
    return getContent().getBytes();
  }

  @Override
  public long getTimeStamp() {
    return 0;
  }

  @Override
  public long getModificationStamp() {
    return 0;
  }

  @Override
  public long getLength() {
    return getContent().length();
  }

  @Override
  public void refresh(boolean asynchronous, boolean recursive, @Nullable Runnable postRunnable) {
    // do nothing
  }

  @Override
  public InputStream getInputStream() {
    return new ByteArrayInputStream(getContent().getBytes());
  }

  abstract protected String getContent();
}
