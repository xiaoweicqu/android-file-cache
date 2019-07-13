package com.example.cachemanager.cache;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * A disk cache that manages files under specific cache folder. It will gets and puts byte array on
 * disk.
 */
public final class FileCache implements Cache<byte[]> {
    private static final String TAG = "FileCache";

    private final FileManager fileManager;

    /**
     * Number of files that is in use (read or write). Negative number indicates the cache is in
     * state of clearing.
     */
    private final AtomicInteger filesInUsage = new AtomicInteger(0);

    /**
     * Constructor for file cache.
     *
     * @param context application context
     * @param name name for the cache
     * @param subFolderName folder name under ${app-cache-dir}/{name} to storage valid files
     */
    public FileCache(Context context, String name, String subFolderName) {
        fileManager = new FileManager(context, name, subFolderName);
    }

    @Override
    @Nullable
    public byte[] get(String key) {
        if (!tryToUse()) {
            // Cache is clearing, unable to use.
            return null;
        }

        byte[] data = getDataFromFile(fileManager.getFileToRead(key));
        // Sets state as no operation.
        filesInUsage.decrementAndGet();
        return data;
    }

    @Nullable
    private byte[] getDataFromFile(@Nullable File fileToRead) {
        if (fileToRead == null) {
            return null;
        }
        try (FileInputStream fileInputStream = new FileInputStream(fileToRead);
             FileChannel fileChannel = fileInputStream.getChannel();
             DataInputStream dataInputStream = new DataInputStream(fileInputStream)){
            // Acquires a shared lock for reading.
            fileChannel.lock(0L, Long.MAX_VALUE, true);
            int length = (int) fileChannel.size();
            byte[] data = new byte[length];
            int readLength = dataInputStream.read(data);
            if (length != readLength) {
                Log.e(TAG, "Error when reading file: " + fileToRead.getName()
                        + "Expected length: " + length + ", but got: " + readLength);
                return null;
            }
            return data;
        } catch (IOException | IllegalStateException e) {
            Log.e(TAG, "Failed to read data from file: " + fileToRead.getName(), e);
        }
        return null;
    }

    @Override
    public boolean put(String key, byte[] value) {
        if (!tryToUse()) {
            // Cache is clearing, unable to use.
            return false;
        }
        boolean success = putDataToFile(fileManager.getFileToWrite(key), value);
        // Sets state as no operation.
        filesInUsage.decrementAndGet();
        return success;
    }

    private boolean putDataToFile(@Nullable File fileToWrite, byte[] data) {
        if (fileToWrite == null) {
            return false;
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(fileToWrite);
             FileChannel fileChannel = fileOutputStream.getChannel();
             DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream)) {
            // Tries to acquire an exclusive lock for writing.
            fileChannel.tryLock(0, Long.MAX_VALUE, false);
            dataOutputStream.write(data);
            return true;
        } catch (IOException | IllegalStateException e) {
            Log.e(TAG, "Failed to write data to file: "+ fileToWrite.getName(), e);
        }
        return false;
    }

    @Override
    public boolean clearAll() {
        if (!tryToClear()) {
            Log.e(TAG, "Failed to clear since file is in use: " + filesInUsage.get());
            return false;
        }
        fileManager.clearAll();
        filesInUsage.set(0);
        return true;
    }

    private boolean tryToUse() {
        return filesInUsage.getAndIncrement() >= 0;
    }

    private boolean tryToClear() {
        // Sets a large value.
       return filesInUsage.compareAndSet(0, -1000);
    }
}
