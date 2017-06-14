package in.arjsna.filechooser;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
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
import java.util.concurrent.Callable;

/**
 * Created by arjun on 6/4/16.
 */
public class FileBucketsListFragment extends Fragment {
  private static final int STORAGE_PERM_REQ = 200;
  private View mRootView;
  private RecyclerView mBucketListView;
  private BucketListAdapter mBucketListAdapter;
  private ArrayList<Bucket> buckets = new ArrayList<>();

  private static final int REQUEST_CODE = 321;

  private final float SCROLL_THRESHOLD = 10;
  private boolean isOnClick;
  private float mDownX;
  private float mDownY;
  private int currentViewingIdx;
  private Animation scaleDownAnim;
  private Animation scaleUpAnim;
  private int mFileTypeToChoose;
  private ProgressBar mProgressBar;

  public FileBucketsListFragment() {
    setRetainInstance(true);
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mRootView = inflater.inflate(R.layout.fragment_file_buckets, container, false);
    mFileTypeToChoose =
        getArguments().getInt(FileLibUtils.FILE_TYPE_TO_CHOOSE, FileLibUtils.FILE_TYPE_IMAGES);
    setUpActionBar();
    initialiseViews();
    bindEvents();
    if (buckets.size() < 1 && getPermissions()) {
      fetchBuckets();
    }
    return mRootView;
  }

  private boolean getPermissions() {
    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECEIVE_SMS)
        != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, STORAGE_PERM_REQ);
      return false;
    }
    return true;
  }

  @Override public void onRequestPermissionsResult(int requestCode, @android.support.annotation.NonNull
      String[] permissions,
      @android.support.annotation.NonNull int[] grantResults) {
    if (requestCode != STORAGE_PERM_REQ) {
      return;
    }
    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      fetchBuckets();
    } else {
      getActivity().finish();
    }
  }

  private void setUpActionBar() {
    Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.file_chooser_toolbar);
    TextView titleView = (TextView) toolbar.findViewById(R.id.file_chooser_toolBarTitle);
    titleView.setText(FileLibUtils.titleMap.get(mFileTypeToChoose));
  }

  private void bindEvents() {
    mBucketListView.setOnTouchListener(new View.OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
          case MotionEvent.ACTION_DOWN:
            mDownX = event.getX();
            mDownY = event.getY();
            View view = mBucketListView.findChildViewUnder(event.getX(), event.getY());
            if (view != null) {
              view.startAnimation(
                  AnimationUtils.loadAnimation(getActivity(), R.anim.scale_down_anim));
            }
            isOnClick = true;
            break;
          case MotionEvent.ACTION_UP:
          case MotionEvent.ACTION_CANCEL:
            if (isOnClick) {
              final View viewup = mBucketListView.findChildViewUnder(event.getX(), event.getY());
              if (viewup != null) {
                Animation animation =
                    AnimationUtils.loadAnimation(getActivity(), R.anim.scale_up_anim);
                animation.setAnimationListener(new Animation.AnimationListener() {
                  @Override public void onAnimationStart(Animation animation) {

                  }

                  @Override public void onAnimationEnd(Animation animation) {
                    currentViewingIdx = mBucketListView.getChildAdapterPosition(viewup);
                    Bucket bucket = buckets.get(currentViewingIdx);
                    Bundle bundle = new Bundle();
                    bundle.putString(FileSelectFragment.BUCKET_ID, bucket.bucketId);
                    bundle.putString(FileSelectFragment.BUCKET_NAME, bucket.bucketName);
                    bundle.putInt(FileSelectFragment.BUCKET_CONTENT_COUNT,
                        bucket.bucketContentCount);
                    bundle.putInt(FileLibUtils.FILE_TYPE_TO_CHOOSE, bucket.fileType);
                    FileSelectFragment fileSelectFragment = new FileSelectFragment();
                    fileSelectFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.files_selection_container, fileSelectFragment)
                        .addToBackStack("File selection")
                        .commit();
                  }

                  @Override public void onAnimationRepeat(Animation animation) {

                  }
                });
                viewup.startAnimation(animation);
              }
            }
            break;
          case MotionEvent.ACTION_MOVE:
            if (isOnClick && (Math.abs(mDownX - event.getX()) > SCROLL_THRESHOLD
                || Math.abs(mDownY - event.getY()) > SCROLL_THRESHOLD)) {
              View viewup = mBucketListView.findChildViewUnder(event.getX(), event.getY());
              if (viewup != null) {
                viewup.startAnimation(
                    AnimationUtils.loadAnimation(getActivity(), R.anim.scale_up_anim));
              }
              isOnClick = false;
            }
            break;
        }
        return false;
      }
    });
  }

  private void loadAnimations() {
    scaleDownAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_down_anim);
    scaleUpAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_up_anim);
  }

  private void fetchBuckets() {
    Single<ArrayList<Bucket>> bucketFetch = Single.fromCallable(new Callable<ArrayList<Bucket>>() {
      @Override public ArrayList<Bucket> call() throws Exception {
        return FileLibUtils.fetchLocalBuckets(getActivity(), mFileTypeToChoose);
      }
    });
    bucketFetch.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SingleObserver<ArrayList<Bucket>>() {
          @Override public void onSubscribe(@NonNull Disposable d) {
            mProgressBar.setVisibility(View.VISIBLE);
            mBucketListView.setVisibility(View.GONE);
          }

          @Override public void onSuccess(@NonNull ArrayList<Bucket> buckets) {
            mBucketListAdapter.addAllAndNotify(buckets);
            mProgressBar.setVisibility(View.GONE);
            mBucketListView.setVisibility(View.VISIBLE);
          }

          @Override public void onError(@NonNull Throwable e) {
            Log.e("Error loading buckets", e.getMessage());
          }
        });
  }

  private void initialiseViews() {
    mProgressBar = (ProgressBar) mRootView.findViewById(R.id.fetch_bucket_progress);
    mBucketListView = (RecyclerView) mRootView.findViewById(R.id.buckets_list);
    GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
    mBucketListView.setLayoutManager(gridLayoutManager);
    mBucketListAdapter = new BucketListAdapter(getActivity(), buckets);
    mBucketListView.setAdapter(mBucketListAdapter);
  }
}
