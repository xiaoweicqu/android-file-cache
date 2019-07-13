package com.example.cachemanager.cache;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**

 * A file manager that manages files under specific cache folder. Each disk cache is identified by
 * name and folderName, which will result in different folders under app cache dir.
 *
 * <p>This class is not thread safe.
 */
public final class FileManager {
    private static final String TAG = "FileManager";

    /** Parent folder for data: -${cache_dir}/${name}/ */
    private final File parentFolder;

    private final File validWorkingFolder;

    /**
     * Constructor for file cache.
     *
     * @param context application context
     * @param name name for the cache
     * @param subFolderName folder name under ${app-cache-dir}/{name} to storage valid files
     */
    public FileManager(Context context, String name, String subFolderName) {
        Context deviceProtectedStorageContext = ContextCompat.isDeviceProtectedStorage(context) ?
                context : ContextCompat.createDeviceProtectedStorageContext(context);
        parentFolder = new File(deviceProtectedStorageContext.getCacheDir(), name);
        validWorkingFolder = new File(parentFolder, subFolderName);
    }


    /**
     * Returns {@link File} instance representing the given key for reading.
     *
     * @param key the given key.
     * @return the file for key if exists, otherwise, return null
     */
    @Nullable
    public File getFileToRead(String key) {
        File file = new File(validWorkingFolder, key);
        return FileUtils.exists(file) ? file : null;
    }

    /**
     * Returns {@link File} instance representing the given key for writing. This will create folder
     * if necessary.
     *
     * @param key the given key.
     * @return the file instance to write if success, otherwise return null
     */
    @Nullable
    public File getFileToWrite(String key) {
        File file = new File(validWorkingFolder, key);
        if (!FileUtils.exists(validWorkingFolder) && FileUtils.mkdirs(validWorkingFolder)) {
            Log.e(TAG, "Failed to make directory: " + validWorkingFolder);
            return null;
        }
        return file;
    }

    /**
     * Clears all files on disk under folder -${cache_dir}/${name}/.
     *
     * @return true if success
     */
    public boolean clearAll() {
        return !FileUtils.exists(parentFolder) || FileUtils.delete(parentFolder);
    }
}
