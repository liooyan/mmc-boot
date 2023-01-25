package cn.lioyan.autoconfigure.file.hdfs;

import cn.lioyan.autoconfigure.file.FileInfo;
import org.apache.hadoop.fs.FileStatus;

/**
 * {@link HDFSFileUtil}
 *
 * @author com.lioyan
 * @since 2022/4/13 15:14
 */
public class HDFSFileUtil  {




    public static FileInfo hdfs2FileInfo(FileStatus fileSystem,HDFSFileSystem hdfsFileSystem) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setPath(fileSystem.getPath().toString());
        fileInfo.setIsdir(fileSystem.isDirectory());
        fileInfo.setLength(fileSystem.getLen());
        fileInfo.setFileSystem(hdfsFileSystem);
        fileInfo.setName(fileSystem.getPath().toString());
        return fileInfo;

    }
}
