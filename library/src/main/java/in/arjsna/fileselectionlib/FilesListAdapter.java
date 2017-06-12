package in.arjsna.fileselectionlib;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arjun on 16/2/16.
 */
public class FilesListAdapter extends RecyclerView.Adapter<FilesListAdapter.ListItemViewHolder> {
  private final Context context;
  private final ArrayList<FileItem> fileItems;
  private final LayoutInflater layoutInflater;
  private final SelectedItemChangeListener selectedItemChangeListener;

  public FilesListAdapter(Context context, ArrayList<FileItem> files,
      SelectedItemChangeListener selectedItemChangeListener) {
    this.context = context;
    this.fileItems = files;
    this.selectedItemChangeListener = selectedItemChangeListener;
    layoutInflater = LayoutInflater.from(context);
  }

  @Override public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = layoutInflater.inflate(R.layout.file_item_view, parent, false);
    return new ListItemViewHolder(view);
  }

  @Override public void onBindViewHolder(final ListItemViewHolder holder, final int position) {
    FileItem fileItem = fileItems.get(position);
    Glide.with(holder.fileImageView.getContext())
        .load(new File(fileItem.filePath))
        .into(holder.fileImageView);
    if (selectedItemsArray.get(position, false)) {
      holder.activeStateLayout.setVisibility(View.VISIBLE);
    } else {
      holder.activeStateLayout.setVisibility(View.GONE);
    }
  }

  @Override public int getItemCount() {
    return fileItems.size();
  }

  public class ListItemViewHolder extends RecyclerView.ViewHolder {
    ImageView fileImageView;
    RelativeLayout activeStateLayout;

    public ListItemViewHolder(View itemView) {
      super(itemView);
      fileImageView = (ImageView) itemView.findViewById(R.id.file_image_view);
      activeStateLayout = (RelativeLayout) itemView.findViewById(R.id.item_active_layout);
    }
  }

  private SparseBooleanArray selectedItemsArray = new SparseBooleanArray();

  public void toggleSelection(int position) {
    if (selectedItemsArray.get(position, false)) {
      selectedItemsArray.delete(position);
    } else {
      selectedItemsArray.put(position, true);
    }
    selectedItemChangeListener.onSelectedItemsCountChanged();
    notifyItemChanged(position);
  }

  public void clearSelections() {
    selectedItemsArray.clear();
    notifyDataSetChanged();
    selectedItemChangeListener.onSelectedItemsCountChanged();
  }

  public List<Integer> getSelectedItems() {
    List<Integer> items = new ArrayList<>(selectedItemsArray.size());
    for (int i = 0; i < selectedItemsArray.size(); i++) {
      items.add(selectedItemsArray.keyAt(i));
    }
    return items;
  }

  public int getSelectedItemCount() {
    return selectedItemsArray.size();
  }

  public interface SelectedItemChangeListener {
    void onSelectedItemsCountChanged();
  }
}
