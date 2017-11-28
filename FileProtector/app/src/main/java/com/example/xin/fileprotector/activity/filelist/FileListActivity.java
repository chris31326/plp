package com.example.xin.fileprotector.activity.filelist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.xin.fileprotector.util.FileType;
import com.example.xin.fileprotector.R;
import com.example.xin.fileprotector.db.DBHelper;
import com.example.xin.fileprotector.db.FileInfo;

import java.util.ArrayList;
import java.util.List;

public class FileListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<FileInfo> fileList;
    private DBHelper dbHelper;
    private String fileType;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fileType = getIntent().getExtras().getString("FileType");
        fileList = new ArrayList<>();
        dbHelper = DBHelper.getInstance(this);

        setContentView(R.layout.fragment_item_list);

        getDataFromSQLite();

        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        final ItemClickListener listener = new DecryptAndView(this);

        adapter = new FileListRecyclerViewAdapter(fileList, listener);
        recyclerView.setAdapter(adapter);
    }

    private void getDataFromSQLite() {
        final FileType type;
        try {
            type = FileType.valueOf(fileType);
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Unknown file type '" + fileType + "'", Toast.LENGTH_LONG).show();
            return;
        }

        if (!dbHelper.fileTable.tableEmpty()) {
            fileList.clear();
            fileList.addAll(dbHelper.fileTable.getFileByType(type));
        }
    }
}
