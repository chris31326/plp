package com.example.xin.fileprotector;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);

        return new ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = files.get(position);
        holder.contentView.setText(files.get(position).getEncryptedFileName());
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView contentView;
        public FileInfo item;

        public ViewHolder(final View view, final ItemClickListener listener) {
            super(view);
            contentView = view.findViewById(R.id.content);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.onItemClick(view, getAdapterPosition());
                    }
                }
            });
        }
    }
}
