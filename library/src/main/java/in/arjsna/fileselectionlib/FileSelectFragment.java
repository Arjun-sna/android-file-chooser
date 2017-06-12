package in.arjsna.fileselectionlib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by arjun on 16/2/16.
 */
public class FileSelectFragment extends Fragment implements GestureDetector.OnGestureListener {
  public static final String BUCKET_NAME = "in.arjsna.fileselectionlib.BUCKET_NAME";
  public static final String BUCKET_ID = "in.arjsna.fileselectionlib.BUCKET_ID";
  public static final String BUCKET_CONTENT_COUNT =
      "in.arjsna.fileselectionlib.BUCKET_CONTENT_COUNT";
  private static final float SCROLL_THRESHOLD = 10;
  private String bucketName;
  private String bucketId;
  private RecyclerView mFilesListView;
  private ArrayList<FileItem> files = new ArrayList<>();
  private FilesListAdapter mFilesListAdapter;
  private int fileType;
  private float mDownX;
  private float mDownY;
  private boolean isOnClick;
  private int bucketContentCount;
  private TextView titleView;
  private TextView mAdddFilesButton;
  private View mRootView;
  private ProgressBar mProgressBar;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mRootView = inflater.inflate(R.layout.fragment_file_select, container, false);
    bucketName = getArguments().getString(BUCKET_NAME);
    bucketId = getArguments().getString(BUCKET_ID);
    bucketContentCount = getArguments().getInt(BUCKET_CONTENT_COUNT, 0);
    fileType =
        getArguments().getInt(FileLibUtils.FILE_TYPE_TO_CHOOSE, FileLibUtils.FILE_TYPE_IMAGES);
    setUpActionBar();
    initialiseViews();
    bindEvents();
    fetchFiles();
    return mRootView;
  }

  private void bindEvents() {
    mAdddFilesButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (mFilesListAdapter.getSelectedItemCount() > 0) {
          ArrayList<String> filePaths = new ArrayList<>();
          List<Integer> selectedItems = mFilesListAdapter.getSelectedItems();
          for (Integer idx : selectedItems) {
            filePaths.add(files.get(idx).filePath);
          }
          Intent intent = new Intent();
          intent.putStringArrayListExtra(FileChooseHelperActivity.SELECTED_FILES, filePaths);
          getActivity().setResult(Activity.RESULT_OK, intent);
          getActivity().finish();
        }
      }
    });
    final GestureDetector gestureDetector =
        new GestureDetector(getActivity(), FileSelectFragment.this);
    mFilesListView.setOnTouchListener(new View.OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {
        View view = mFilesListView.findChildViewUnder(event.getX(), event.getY());
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
          case MotionEvent.ACTION_DOWN:
            mDownX = event.getX();
            mDownY = event.getY();
            if (view != null) {
              view.startAnimation(
                  AnimationUtils.loadAnimation(getActivity(), R.anim.scale_down_anim));
            }
            isOnClick = true;
            break;
          case MotionEvent.ACTION_UP:
          case MotionEvent.ACTION_CANCEL:
            if (isOnClick) {
              if (view != null) {
                view.startAnimation(
                    AnimationUtils.loadAnimation(getActivity(), R.anim.scale_up_anim));
                int idx = mFilesListView.getChildAdapterPosition(view);
                mFilesListAdapter.toggleSelection(idx);
              }
            }
            break;
          case MotionEvent.ACTION_MOVE:
            if (isOnClick && (Math.abs(mDownX - event.getX()) > SCROLL_THRESHOLD
                || Math.abs(mDownY - event.getY()) > SCROLL_THRESHOLD)) {
              //                            View viewup = mFilesListView.findChildViewUnder(event.getX(), event.getY());
              if (view != null) {
                view.startAnimation(
                    AnimationUtils.loadAnimation(getActivity(), R.anim.scale_up_anim));
              }
              isOnClick = false;
            }
            break;
        }
        return gestureDetector.onTouchEvent(event);
      }
    });
  }

  private void fetchFiles() {
    Single<Boolean> fileItems = Single.fromCallable(new Callable<Boolean>() {
      @Override public Boolean call() throws Exception {
        files.clear();
        files.addAll(FileLibUtils.getFilesInBucket(getActivity(), bucketId, fileType));
        return true;
      }
    });
    fileItems.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SingleObserver<Boolean>() {
          @Override public void onSubscribe(@NonNull Disposable d) {
            mProgressBar.setVisibility(View.VISIBLE);
            mFilesListView.setVisibility(View.GONE);
          }

          @Override public void onSuccess(@NonNull Boolean aBoolean) {
            titleView.setText(
                getResources().getString(R.string.selected_item_count, bucketName, 0, files.size()));
            mFilesListAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.GONE);
            mFilesListView.setVisibility(View.VISIBLE);
          }

          @Override public void onError(@NonNull Throwable e) {
            Log.e("Error loading file ", e.getMessage());
          }
        });
  }

  private void initialiseViews() {
    mProgressBar = (ProgressBar) mRootView.findViewById(R.id.file_load_pb);
    mAdddFilesButton = (TextView) mRootView.findViewById(R.id.add_file_button);
    mFilesListView = (RecyclerView) mRootView.findViewById(R.id.files_list);
    GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
    mFilesListView.setLayoutManager(gridLayoutManager);
    mFilesListAdapter = new FilesListAdapter(getActivity(), files, selectedItemChangeListener);
    mFilesListView.setAdapter(mFilesListAdapter);
  }

  public FilesListAdapter.SelectedItemChangeListener selectedItemChangeListener =
      new FilesListAdapter.SelectedItemChangeListener() {
        @Override public void onSelectedItemsCountChanged() {
          int selectedItemCount = mFilesListAdapter.getSelectedItemCount();
          titleView.setText(
              getResources().getString(R.string.selected_item_count, bucketName, selectedItemCount,
                  bucketContentCount));
          if (selectedItemCount > 0) {
            mAdddFilesButton.setVisibility(View.VISIBLE);
          } else {
            mAdddFilesButton.setVisibility(View.GONE);
          }
        }
      };

  private void setUpActionBar() {
    Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.file_chooser_toolbar);
    toolbar.setTitle("");
    titleView = (TextView) toolbar.findViewById(R.id.file_chooser_toolBarTitle);
  }

  @Override public boolean onDown(MotionEvent e) {
    return true;
  }

  @Override public void onShowPress(MotionEvent e) {

  }

  @Override public boolean onSingleTapUp(MotionEvent e) {
    return false;
  }

  @Override
  public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    Log.i("Testscroll", "Dis " + distanceY);
    if (distanceY > 0 && mAdddFilesButton.getVisibility() == View.VISIBLE) {
      mAdddFilesButton.setVisibility(View.GONE);
    } else if (distanceY < 0
        && mFilesListAdapter.getSelectedItemCount() > 0
        && mAdddFilesButton.getVisibility() == View.GONE) {
      mAdddFilesButton.setVisibility(View.VISIBLE);
    }
    return false;
  }

  @Override public void onLongPress(MotionEvent e) {
    //        View view = mFilesListView.findChildViewUnder(e.getX(), e.getY());
    //        if(view != null) {
    //            int idx = mFilesListView.getChildAdapterPosition(view);
    //            mFilesListAdapter.toggleSelection(idx);
    //            Intent fullViewIntent = new Intent(getActivity(), FullViewActivity.class);
    //            fullViewIntent.putExtra(FullViewActivity.FILE_PATH, files.get(idx).filePath);
    //            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    //                ActivityOptions options = ActivityOptions.
    //                        makeSceneTransitionAnimation(getActivity(), view, getResources().getString(R.string.shared_image_view));
    //                startActivity(fullViewIntent, options.toBundle());
    //            } else {
    //                startActivity(fullViewIntent);
    //            }
    //        }
  }

  @Override
  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    return false;
  }

  //    @Override
  //    public void onBackPressed() {
  //        Intent result = new Intent();
  //        result.putExtra(BUCKET_ID, bucketId);
  //        result.putExtra(BUCKET_CONTENT_COUNT, files.size());
  //        setResult(Activity.RESULT_OK, result);
  //        super.onBackPressed();
  //    }
}
