package in.arjsna.filechooserdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by arjun on 12/6/17.
 */

public class FileListAdaptper extends RecyclerView.Adapter<FileListAdaptper.ListVH> {

  private final Context context;
  private final ArrayList<String> filePaths;

  public FileListAdaptper(Context context, ArrayList<String> files) {
    this.context = context;
    this.filePaths = files;
  }

  @Override public FileListAdaptper.ListVH onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ListVH(LayoutInflater.from(context).inflate(R.layout.file_item, parent, false));
  }

  @Override public void onBindViewHolder(FileListAdaptper.ListVH holder, int position) {
    holder.textView.setText(filePaths.get(position));
  }

  @Override public int getItemCount() {
    return filePaths.size();
  }

  static class ListVH extends RecyclerView.ViewHolder {
    public TextView textView;

    public ListVH(View itemView) {
      super(itemView);
      textView = (TextView) itemView.findViewById(R.id.file_item_text);
    }
  }
}
