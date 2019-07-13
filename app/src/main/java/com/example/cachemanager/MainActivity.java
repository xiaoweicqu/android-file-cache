package com.example.cachemanager;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.cachemanager.cache.FileCache;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    @Nullable
    private FileCache fileCache;
    private int threadCount = 0;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (fileCache == null) {
            fileCache = new FileCache(getApplicationContext(),"test", "test1");
            random = new Random();
        }

        Button read = findViewById(R.id.bt_read);
        read.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startThread(FileOperationThread.OPERATION_READ);
                    }
                }
        );
        Button write = findViewById(R.id.bt_write);
        write.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startThread(FileOperationThread.OPERATION_WRITE);
                    }
                }
        );
        Button clear = findViewById(R.id.bt_clear);
        clear.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startThread(FileOperationThread.OPERATION_CLEAR);
                    }
                }
        );
    }

    private void startThread(int operation) {
        int threadId = ++threadCount;
        String threadName = "#t"+threadId;
        String fileName = "file" + random.nextInt(5);
        FileOperationThread thread = new FileOperationThread(threadName, fileCache, fileName, operation);
        thread.start();
    }
}
