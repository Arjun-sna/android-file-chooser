# android-file-chooser
Android library to provide chooser for files in External storage

## Demo

<a href='https://play.google.com/store/apps/details?id=package in.arjsna.filechooser' target='_blank'><img height='50' style='border:0px;height:50px;' src='https://cdn.rawgit.com/Arjun-sna/Arjun-sna.github.io/f8228c83/raw/GooglePlay.png' border='0' alt='GooglePlay Link' /></a>

<img src="https://arjun-sna.github.io/raw/filechooser_demo_1.jpg" width="200" />  <img src="https://arjun-sna.github.io/raw/filechooser_demo_2.jpg" width="200" />  <img src="https://arjun-sna.github.io/raw/filechooser_demo_3.jpg" width="200" />  <img src="https://arjun-sna.github.io/raw/filechooser_demo_4.jpg" width="200" />

## Installation
Add gradle dependency
```
repositories {
    jcenter()
}
dependencies {
    compile 'in.arjsna:filechooser:1.0.0'
}

```

## Usage

To choose from all image files

```java
  Intent addPhotosIntent = new Intent(MainActivity.this, FileChooseHelperActivity.class);
  addPhotosIntent.putExtra(FileLibUtils.FILE_TYPE_TO_CHOOSE, FileLibUtils.FILE_TYPE_IMAGES);
  startActivityForResult(addPhotosIntent, STORAGE_REQUEST_CODE);
```

To choose from all video files

```java
  Intent addPhotosIntent = new Intent(MainActivity.this, FileChooseHelperActivity.class);
  addPhotosIntent.putExtra(FileLibUtils.FILE_TYPE_TO_CHOOSE, FileLibUtils.FILE_TYPE_VIDEOS);
  startActivityForResult(addPhotosIntent, STORAGE_REQUEST_CODE);
```

You can get the result of above two request in `onActivityResult()` of the activity

```java
  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == STORAGE_REQUEST_CODE && resultCode == RESULT_OK) {
      ArrayList<String> stringArrayExtra = data.getStringArrayListExtra(FileLibUtils.SELECTED_FILES);
                            //the ArrayList will contain the absolute paths of selected files
      ....


      ....
    }
  }
```

You can also use the below method to get Folder and File lists

```java
  ArrayList<Bucket> buckets =
          FileLibUtils.fetchLocalBuckets(MainActivity.this, FileLibUtils.FILE_TYPE_IMAGES);
  ArrayList<FileItem> filesInBucket =
      FileLibUtils.getFilesInBucket(MainActivity.this, buckets.get(0).bucketId,
          FileLibUtils.FILE_TYPE_IMAGES);
```




Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License.
You may obtain a copy of the License in the LICENSE file, or at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.


