package com.example.xin.fileprotector.activity.filelist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xin.fileprotector.R;
import com.example.xin.fileprotector.db.FileInfo;

import java.util.List;

public class FileListRecyclerViewAdapter extends RecyclerView.Adapter<FileListRecyclerViewAdapter.ViewHolder> {

    private final List<FileInfo> files;
    private ItemClickListener clickListener;

    public FileListRecyclerViewAdapter(final List<FileInfo> files, final ItemClickListener clickListener) {
        this.files = files;
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);

        return new ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final FileInfo fileInfo = files.get(position);
        holder.item = fileInfo;
        holder.contentView.setText(fileInfo.getOriginalPath());
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView contentView;
        private FileInfo item;

        public ViewHolder(final View view, final ItemClickListener listener) {
            super(view);
            contentView = view.findViewById(R.id.content);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    listener.onItemClick(view, item);
                }
            });
        }
    }
}
