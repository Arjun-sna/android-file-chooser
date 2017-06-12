package in.arjsna.filechooser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import in.arjsna.fileselectionlib.FileChooseHelperActivity;
import in.arjsna.fileselectionlib.FileLibUtils;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

  private static final int STORAGE_REQUEST_CODE = 100;
  RecyclerView recyclerView;
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    recyclerView = (RecyclerView) findViewById(R.id.selected_file_list);
    LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, layoutManager.getOrientation()));
    findViewById(R.id.filechoose_btn).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent addPhotosIntent = new Intent(MainActivity.this, FileChooseHelperActivity.class);
        addPhotosIntent.putExtra(FileLibUtils.FILE_TYPE_TO_CHOOSE, FileLibUtils.FILE_TYPE_IMAGES);
        startActivityForResult(addPhotosIntent, STORAGE_REQUEST_CODE);
      }
    });
    findViewById(R.id.filechoose_btn_video).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent addPhotosIntent = new Intent(MainActivity.this, FileChooseHelperActivity.class);
        addPhotosIntent.putExtra(FileLibUtils.FILE_TYPE_TO_CHOOSE, FileLibUtils.FILE_TYPE_VIDEOS);
        startActivityForResult(addPhotosIntent, STORAGE_REQUEST_CODE);
      }
    });
    //findViewById(R.id.filechoose_btn_audio).setOnClickListener(new View.OnClickListener() {
    //  @Override public void onClick(View v) {
    //    Intent addPhotosIntent = new Intent(MainActivity.this, FileChooseHelperActivity.class);
    //    addPhotosIntent.putExtra(FileLibUtils.FILE_TYPE_TO_CHOOSE, FileLibUtils.FILE_TYPE_AUDIO);
    //    startActivityForResult(addPhotosIntent, STORAGE_REQUEST_CODE);
    //  }
    //});
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == STORAGE_REQUEST_CODE && resultCode == RESULT_OK) {
      ArrayList<String> stringArrayExtra = data.getStringArrayListExtra(FileLibUtils.SELECTED_FILES);
      FileListAdaptper fileListAdaptper = new FileListAdaptper(MainActivity.this, stringArrayExtra);
      recyclerView.setAdapter(fileListAdaptper);
    }
  }
}
