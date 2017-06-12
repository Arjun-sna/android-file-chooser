package in.arjsna.fileselectionlib;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import java.io.File;

public class FileChooseHelperActivity extends AppCompatActivity {

  private File tempPhoto;
  private int mFileTypeToChoose;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_files_choose_helper);
    mFileTypeToChoose =
        getIntent().getIntExtra(FileLibUtils.FILE_TYPE_TO_CHOOSE, FileLibUtils.FILE_TYPE_IMAGES);
    setUpActionBar();
    if (savedInstanceState == null) {
      FileBucketsListFragment bucketsListFragment = new FileBucketsListFragment();
      Bundle bundle = new Bundle();
      bundle.putInt(FileLibUtils.FILE_TYPE_TO_CHOOSE, mFileTypeToChoose);
      bucketsListFragment.setArguments(bundle);
      getSupportFragmentManager().beginTransaction()
          .add(R.id.files_selection_container, bucketsListFragment)
          .commit();
    }
  }

  private void setUpActionBar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.file_chooser_toolbar);
    toolbar.setTitle("");
    toolbar.setNavigationIcon(R.drawable.left_arrow_icon);
    setSupportActionBar(toolbar);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void onBackPressed() {
    super.onBackPressed();
    setResult(RESULT_CANCELED);
  }
}
