package cn.lioyan.autoconfigure.file.local;

import cn.lioyan.autoconfigure.file.FileInfo;

import java.io.File;

/**
 * {@link LocalFileUtil}
 *
 * @author com.lioyan
 * @since 2022/4/13 15:14
 */
public class LocalFileUtil  {




    public static FileInfo file2FileInfo(File fileSystem, LocalFileSystem hdfsFileSystem) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setPath(fileSystem.getPath());
        fileInfo.setIsdir(fileSystem.isDirectory());
        fileInfo.setLength(fileSystem.length());
        fileInfo.setFileSystem(hdfsFileSystem);
        fileInfo.setName(fileSystem.getName());
        return fileInfo;

    }
}
