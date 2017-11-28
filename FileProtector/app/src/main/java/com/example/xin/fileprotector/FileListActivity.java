package com.example.xin.fileprotector;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

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
        dbHelper = new DBHelper(this);

        setContentView(R.layout.fragment_item_list);

        getDataFromSQLite();

        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ItemClickListener listener = (view, position) -> {
            Toast.makeText(this, "Position " + position, Toast.LENGTH_SHORT).show();
        };

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
