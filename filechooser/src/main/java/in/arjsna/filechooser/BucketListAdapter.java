package in.arjsna.filechooser;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by arjun on 16/2/16.
 */
public class BucketListAdapter extends RecyclerView.Adapter<BucketListAdapter.ListItemViewHolder> {
  private final Context context;
  private final ArrayList<Bucket> buckets;
  private LayoutInflater layoutInflater;

  public BucketListAdapter(Context context, ArrayList<Bucket> buckets) {
    this.context = context;
    this.buckets = buckets;
    layoutInflater = LayoutInflater.from(context);
  }

  @Override
  public BucketListAdapter.ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = layoutInflater.inflate(R.layout.bucket_list_item, parent, false);
    return new ListItemViewHolder(view);
  }

  @Override
  public void onBindViewHolder(BucketListAdapter.ListItemViewHolder holder, int position) {
    final Bucket bucket = buckets.get(position);
    holder.bucketNameView.setText(bucket.bucketName);
    holder.bucketCountCountView.setText(String.valueOf(bucket.bucketContentCount));
    Glide.with(holder.bucketCoverImageView.getContext())
        .load(new File(bucket.bucketCoverImageFilePath))
        .into(holder.bucketCoverImageView);
  }

  @Override public int getItemCount() {
    return buckets.size();
  }

  public void addAllAndNotify(ArrayList<Bucket> buckets) {
    this.buckets.addAll(buckets);
    notifyDataSetChanged();
  }

  public class ListItemViewHolder extends RecyclerView.ViewHolder {
    TextView bucketNameView;
    TextView bucketCountCountView;
    ImageView bucketCoverImageView;

    public ListItemViewHolder(View itemView) {
      super(itemView);
      bucketNameView = (TextView) itemView.findViewById(R.id.bucket_name_view);
      bucketCountCountView = (TextView) itemView.findViewById(R.id.bucket_content_count_view);
      bucketCoverImageView = (ImageView) itemView.findViewById(R.id.bucket_cover_iv);
    }
  }
}
