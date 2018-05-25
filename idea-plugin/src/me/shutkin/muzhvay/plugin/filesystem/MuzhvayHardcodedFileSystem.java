package me.shutkin.muzhvay.plugin.filesystem;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.vfs.*;
import com.intellij.openapi.vfs.impl.BulkVirtualFileListenerAdapter;
import com.intellij.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class MuzhvayHardcodedFileSystem extends VirtualFileSystem implements ApplicationComponent {
  public static final MuzhvayHardcodedFileSystem INSTANCE = new MuzhvayHardcodedFileSystem();

  private final EventDispatcher<VirtualFileListener> eventDispatcher = EventDispatcher.create(VirtualFileListener.class);
  private static final HardcodedFile files[] = new HardcodedFile[] {new LipaSchemeFile(), new JsonSchemeFile()};

  private MuzhvayHardcodedFileSystem() {
    super();
    Application app = ApplicationManager.getApplication();
    if (app != null)
      app.getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkVirtualFileListenerAdapter(eventDispatcher.getMulticaster(), this));
  }

  @NotNull
  @Override
  public String getComponentName() {
    return "MuzhvayHardcodedFileSystem";
  }

  @Override
  public void initComponent() {
  }

  @Override
  public void disposeComponent() {
  }

  @NotNull
  @Override
  public String getProtocol() {
    return "muzhvay";
  }

  @Nullable
  @Override
  public VirtualFile findFileByPath(@NotNull String path) {
    for (HardcodedFile file : files)
      if (file.getPath().equals(path) || VirtualFileManager.extractPath(file.getPath()).equals(path))
        return file;
    return null;
  }

  @Override
  public void refresh(boolean asynchronous) {
    for (HardcodedFile file : files) {
      VirtualFileEvent event = new VirtualFileEvent(null, file, file.getName(), file.getParent());
      eventDispatcher.getMulticaster().fileCreated(event);
    }
  }

  @Nullable
  @Override
  public VirtualFile refreshAndFindFileByPath(@NotNull String path) {
    return findFileByPath(path);
  }

  @Override
  public void addVirtualFileListener(@NotNull VirtualFileListener listener) {
    eventDispatcher.addListener(listener);
  }

  @Override
  public void removeVirtualFileListener(@NotNull VirtualFileListener listener) {
    eventDispatcher.removeListener(listener);
  }

  @Override
  protected void deleteFile(Object requestor, @NotNull VirtualFile vFile) throws IOException {
    throw new IOException("Muzhvay File System is read only");
  }

  @Override
  protected void moveFile(Object requestor, @NotNull VirtualFile vFile, @NotNull VirtualFile newParent) throws IOException {
    throw new IOException("Muzhvay File System is read only");
  }

  @Override
  protected void renameFile(Object requestor, @NotNull VirtualFile vFile, @NotNull String newName) throws IOException {
    throw new IOException("Muzhvay File System is read only");
  }

  @NotNull
  @Override
  public VirtualFile createChildFile(Object requestor, @NotNull VirtualFile vDir, @NotNull String fileName) throws IOException {
    throw new IOException("Muzhvay File System is read only");
  }

  @NotNull
  @Override
  public VirtualFile createChildDirectory(Object requestor, @NotNull VirtualFile vDir, @NotNull String dirName) throws IOException {
    throw new IOException("Muzhvay File System is read only");
  }

  @NotNull
  @Override
  public VirtualFile copyFile(Object requestor, @NotNull VirtualFile virtualFile, @NotNull VirtualFile newParent, @NotNull String copyName) throws IOException {
    throw new IOException("Muzhvay File System is read only");
  }

  @Override
  public boolean isReadOnly() {
    return true;
  }

  @Override
  public boolean isCaseSensitive() {
    return true;
  }

  @Override
  public boolean isValidName(@NotNull String name) {
    for (HardcodedFile file : files)
      if (file.getName().equals(name))
        return true;
    return false;
  }
}
