package com.example.cachemanager.cache;

import android.support.annotation.Nullable;
import android.util.Log;
import java.io.File;

/**
 * Utils for file operations.
 */
public final class FileUtils {
    private static final String TAG = "FileUtils";

    private FileUtils() {}

    /** @return true if the file or directory exists. */
    public static boolean exists(@Nullable File fileOrDirectory) {
        return fileOrDirectory != null && fileOrDirectory.exists();
    }


    /**
     * Create the given directory and any missing parents.
     *
     * @param directory Directory to create
     * @return true if the directory was created or already exists.
     */
    public static boolean mkdirs(File directory) {
        if (directory.exists()) {
            return true;
        }

        if (directory.mkdirs()) {
            if (directory.setWritable(true)) {
                return true;
            }
            Log.e(TAG, "Cannot set writable " + directory);
            return false;
        }
        Log.e(TAG, "Cannot create directory" + directory);
        return false;

    }

    /**
     * Fully deletes a file or directory.
     * @param fileOrDirectory File of directory to delete
     * @return true if the file or directory fully deleted or file or directory does not exist.
     */
    public static boolean delete(@Nullable File fileOrDirectory) {
        if (fileOrDirectory == null || !fileOrDirectory.exists()) {
            return true;
        }

        if (fileOrDirectory.isDirectory()) {
            File[] children = fileOrDirectory.listFiles();
            for (int i = 0; children != null && i < children.length; i++) {
                delete(children[i]);
            }
        }
        return fileOrDirectory.delete();
    }
}

