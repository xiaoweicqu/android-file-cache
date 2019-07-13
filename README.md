# Android File Cache

🚀 A fast file cache manager as a convenience for caching data that can be represented in key-value pairs on disk. 


## Usage
All sample code can be found in the [MainActivity](https://github.com/xiaoweicqu/android-file-cache/blob/master/app/src/main/java/com/example/cachemanager/MainActivity.java) file. 

##### Create FileCahce
```Java
// This will ends in caching files under ${cache_dir}/test/f on DE storage. 
FileCahe fileCache = new FileCache(getApplicationContext(),"test", "f");

```

##### Read and Write
```Java
// Write data.
fileCache.put("key", "String to write".getBytes(StandardCharsets.UTF_8))))

// Read data.
byte[] data = fileCache.get("key");
```

## Concurrency

The FileCahe provides capacity for high concurrency:

+ It acqures shared lock for reading on each file so mutiple threads are allowed to read same file at the same time.
+ It request exclusive lock per file for writing to mutiple threads are allowed to write different files at the same time.
+ It uses AtomicInteger to indicate the state, and try to clear when there's no file is in use (reading or writing). The call of clear() maybe file, but we can avoid global instric lock for supporting clearing. 

# License

    Copyright 2019 xiaowei

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
