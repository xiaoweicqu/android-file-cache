package com.example.cachemanager;

import android.support.annotation.IntDef;
import android.util.Base64;
import android.util.Log;

import com.example.cachemanager.cache.FileCache;

import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * Thread to random write or read from the file cache, or clear the cache.
 */
public class FileOperationThread extends Thread {
    private static final String TAG = "Cache";
    private static final int MAX_LENGTH = 100;

    @IntDef({
            OPERATION_READ,
            OPERATION_WRITE,
            OPERATION_CLEAR,
    })
    public @interface Operation {}

    public static final int OPERATION_READ = 0;
    public static final int OPERATION_WRITE = 1;
    public static final int OPERATION_CLEAR = 2;

    private final String name;
    private final FileCache fileCache;
    private final String fileName;
    private final int operation;

    public FileOperationThread(String name, FileCache fileCache, String fileName, @Operation int op) {
        this.name = name;
        this.fileCache = fileCache;
        this.fileName = fileName;
        this.operation = op;
    }

    @Override
    public void run() {
        if (operation == OPERATION_READ) {
            byte[] data = fileCache.get(fileName);
            String dataToPrint = data == null ? "null" : Base64.encodeToString(data, Base64.DEFAULT);
            Log.i(TAG, "Read data on " + name + ": " + dataToPrint);
        } else if (operation == OPERATION_WRITE) {
            Random generator = new Random();
            StringBuilder randomStringBuilder = new StringBuilder();
            int randomLength = generator.nextInt(MAX_LENGTH);
            char tempChar;
            for (int i = 0; i < randomLength; i++){
                tempChar = (char) (generator.nextInt(96) + 32);
                randomStringBuilder.append(tempChar);
            }
            String randomStringToWrite = randomStringBuilder.toString();
            if (fileCache.put(fileName, randomStringToWrite.getBytes(StandardCharsets.UTF_8))) {
                Log.i(TAG, "Write data on " + name + ": " + randomStringToWrite);
            } else {
                Log.i(TAG, "Failed to write data on " + name);
            }
        } else {
            if (fileCache.clearAll()) {
                Log.i(TAG, "Clear all on " + name);
            } else {
                Log.e(TAG, "Failed to clear all.");
            }
        }
    }
}
