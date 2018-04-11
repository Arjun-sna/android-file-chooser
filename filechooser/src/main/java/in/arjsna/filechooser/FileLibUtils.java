package in.arjsna.filechooser;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by arjun on 6/1/16.
 */
public class FileLibUtils {
  //public final static int FILE_TYPE_ALL = 500;
  public final static int FILE_TYPE_IMAGES = 501;
  public final static int FILE_TYPE_VIDEOS = 502;
  //public final static int FILE_TYPE_AUDIO = 503;
  //public final static int TAKE_PICTURE = 504;
  //public final static int TAKE_VIDEO = 505;

  //selection mode
  public final static int SINGLE_SELECTION_MODE = 506;
  public final static int MULTI_SELECTION_MODE = 507;

  public final static Map<Integer, String> titleMap = new HashMap<Integer, String>() {
    {
      //put(FILE_TYPE_ALL, "All files");
      put(FILE_TYPE_IMAGES, "All Images");
      put(FILE_TYPE_VIDEOS, "All Videos");
      //put(FILE_TYPE_AUDIO, "All Audios");
    }
  };

  public final static Map<Integer, Uri> uriMap = new HashMap<Integer, Uri>() {
    {
      //            put(FILE_TYPE_ALL, "All files");
      put(FILE_TYPE_IMAGES, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
      put(FILE_TYPE_VIDEOS, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
      //put(FILE_TYPE_AUDIO, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
    }
  };

  public final static String FILE_TYPE_TO_CHOOSE = "in.arj.fileselectionlib.FILE_TYPE_TO_CHOOSE";
  public static final String SELECTED_FILES = "selected_files";
  public static final String FILE_SELECTION_MODE = "file_selection_mode";

  public static ArrayList<Bucket> fetchLocalBuckets(Context context, int mFileTypeToChoose) {
    ArrayList<Bucket> buckets = new ArrayList<>();
    Map<String, Integer> bucketFileCountMap = new HashMap<>();
    String[] projection = getBucketProjection(mFileTypeToChoose);
    Uri queryUri = uriMap.get(mFileTypeToChoose);
    // Create the cursor pointing to the SDCard
    Cursor cursor =
        context.getContentResolver().query(queryUri, projection, // Which columns to return
            null,       // Return all rows
            null, MediaStore.MediaColumns.DATE_ADDED + " DESC");
    int columnIndexBucketId = cursor.getColumnIndexOrThrow(projection[0]);
    int columnIndexBucketName = cursor.getColumnIndexOrThrow(projection[1]);
    int columnIndexFilePath = cursor.getColumnIndexOrThrow(projection[2]);
    while (cursor.moveToNext()) {
      //            Log.i("BUCKET_DISPLAY_NAME ", cursor.getString(columnIndexBucketName) + " " + cursor.getString(columnIndexBucketId));
      String bucketId = cursor.getString(columnIndexBucketId);
      if (bucketFileCountMap.containsKey(bucketId)) {
        bucketFileCountMap.put(bucketId, bucketFileCountMap.get(bucketId) + 1);
      } else {
        Bucket bucket = new Bucket();
        bucket.bucketId = bucketId;
        bucket.bucketName = cursor.getString(columnIndexBucketName);
        bucket.bucketCoverImageFilePath = cursor.getString(columnIndexFilePath);
        bucket.fileType = mFileTypeToChoose;
        buckets.add(bucket);
        bucketFileCountMap.put(bucketId, 1);
      }
    }
    for (Bucket bucket : buckets) {
      bucket.bucketContentCount = bucketFileCountMap.get(bucket.bucketId);
    }
    cursor.close();
    return buckets;
  }

  private static String[] getBucketProjection(int fileType) {
    if (fileType == FILE_TYPE_IMAGES) {
      return new String[] {
          MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
          MediaStore.Images.Media.DATA
      };
    } else if (fileType == FILE_TYPE_VIDEOS) {
      return new String[] {
          MediaStore.Video.Media.BUCKET_ID, MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
          MediaStore.Video.Media.DATA
      };
    } else {
      return new String[] {
          MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.AudioColumns.ALBUM,
          MediaStore.Audio.Media.DATA
      };
    }
  }

  public static ArrayList<FileItem> getFilesInBucket(Context context, String bucketId,
      int fileType) {
    ArrayList<FileItem> fileItems = new ArrayList<>();
    String[] projection = getFileProjection(fileType);
    Uri queryUri = uriMap.get(fileType);
    // Create the cursor pointing to the SDCard
    Cursor cursor =
        context.getContentResolver().query(queryUri, projection, // Which columns to return
            projection[4] + " = '" + bucketId + "'",       // Return all rows
            null, MediaStore.MediaColumns.DATE_ADDED + " DESC");
    int columnIndexFileId = cursor.getColumnIndexOrThrow(projection[0]);
    int columnIndexFileName = cursor.getColumnIndexOrThrow(projection[1]);
    int columnIndexFileSize = cursor.getColumnIndexOrThrow(projection[2]);
    int columnIndexFilePath = cursor.getColumnIndexOrThrow(projection[3]);
    int columnIndexFileThumbPath = cursor.getColumnIndexOrThrow(projection[5]);
    while (cursor.moveToNext()) {
      FileItem fileItem = new FileItem(fileType);
      fileItem.fileId = cursor.getString(columnIndexFileId);
      fileItem.fileName = cursor.getString(columnIndexFileName);
      fileItem.filePath = cursor.getString(columnIndexFilePath);
      fileItem.fileSize = cursor.getString(columnIndexFileSize);
      fileItem.fileThumbnailPath = cursor.getString(columnIndexFileThumbPath);
      fileItems.add(fileItem);
    }
    cursor.close();
    return fileItems;
  }

  private static String[] getFileProjection(int fileType) {
    switch (fileType) {
      default:
      case FILE_TYPE_IMAGES:
        return new String[] {
            MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Thumbnails.DATA
        };
      case FILE_TYPE_VIDEOS:
        return new String[] {
            MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.BUCKET_ID, MediaStore.Video.Thumbnails.DATA
        };
      //case FILE_TYPE_AUDIO:
      //  return new String[] {
      //      MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME,
      //      MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.DATA,
      //      MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.SIZE
      //  };
    }
  }
}

