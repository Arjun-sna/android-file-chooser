package in.arjsna.filechooser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import in.arjsna.fileselectionlib.FileChooseHelperActivity;
import in.arjsna.fileselectionlib.FileLibUtils;

public class MainActivity extends AppCompatActivity {

  private static final int STORAGE_REQUEST_CODE = 100;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    findViewById(R.id.filechoose_btn).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent addPhotosIntent = new Intent(MainActivity.this, FileChooseHelperActivity.class);
        addPhotosIntent.putExtra(FileLibUtils.FILE_TYPE_TO_CHOOSE, FileLibUtils.FILE_TYPE_IMAGES);
        startActivityForResult(addPhotosIntent, STORAGE_REQUEST_CODE);
      }
    });
  }
}
