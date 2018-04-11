package in.arjsna.filechooser;

/**
 * Created by arjun on 16/2/16.
 */
public class FileItem {
  public String fileId;
  public String fileName;
  public String filePath;
  public String fileSize;
  public String fileThumbnailPath;
  public int fileType;

  public FileItem(int fileType) {
    this.fileType = fileType;
  }
}
