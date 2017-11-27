package com.example.xin.fileprotector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

public class FileListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<FileInfo> fileList;
    private DBHelper dbHelper;
    private String fileType;
    private KeyStore keyStore = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        if (!dbHelper.fileTable.tableEmpty()) {
                fileList.clear();
                fileList.addAll(dbHelper.fileTable.getFileByType(FileType.valueOf(fileType)));
        }
    }

}
