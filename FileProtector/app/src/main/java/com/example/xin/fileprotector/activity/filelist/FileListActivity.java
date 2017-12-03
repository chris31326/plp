package com.example.xin.fileprotector.activity.filelist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.xin.fileprotector.R;
import com.example.xin.fileprotector.db.DBHelper;
import com.example.xin.fileprotector.db.FileInfo;
import com.example.xin.fileprotector.util.FileType;

import java.util.Collections;
import java.util.List;

public class FileListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private DBHelper dbHelper;
    private String fileType;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fileType = getIntent().getExtras().getString("FileType");
        dbHelper = DBHelper.getInstance(this);

        setContentView(R.layout.fragment_item_list);

        final List<FileInfo> fileList;
        final ItemClickListener listener;
        if (fileType.equals("Decrypted")) {
            fileList = dbHelper.fileTable.getDecryptedFiles();
            listener = new ReEncryptOnClick(this, dbHelper);
        } else {
            fileList = getFilesByTypeFromDB();
            listener = new DecryptAndView(this, dbHelper);
        }

        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FileListRecyclerViewAdapter(fileList, listener);
        recyclerView.setAdapter(adapter);
    }

    private List<FileInfo> getFilesByTypeFromDB() {
        final FileType type;
        try {
            type = FileType.valueOf(fileType);
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Unknown file type '" + fileType + "'", Toast.LENGTH_LONG).show();
            return Collections.emptyList();
        }
        return dbHelper.fileTable.getFileByType(type);
    }
}
