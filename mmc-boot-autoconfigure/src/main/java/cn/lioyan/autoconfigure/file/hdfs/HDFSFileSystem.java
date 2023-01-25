package cn.lioyan.autoconfigure.file.hdfs;

import cn.lioyan.autoconfigure.file.AbstractFileSystem;
import cn.lioyan.autoconfigure.file.FileInfo;
import cn.lioyan.autoconfigure.file.exception.HDFSInitException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link HDFSFileSystem}
 *
 * @author com.lioyan
 * @since 2022/4/13 15:14
 */
public class HDFSFileSystem extends AbstractFileSystem {


    public FileSystem fileSystem;

    public HDFSFileSystem(String bashPath) {
        super(bashPath);
        try {
            this.fileSystem = FileSystem.get(new Configuration());
        } catch (IOException e) {
            throw HDFSInitException.newInstance();
        }
        initBasePath();
    }

    @Override
    public void deleteFile(String file) throws IOException {
        FileSystem fs = null;
        fs = getFileSystem();
        Path path = new Path(setDefaultBashPath(file));
        fs.delete(path, true);
    }

    @Override
    public void mkdirs(String file) throws IOException {
        FileSystem fs = null;
        fs = getFileSystem();

        boolean fileExist = isFileExist(setDefaultBashPath(file));
        if (!fileExist) {
            fs.mkdirs(new Path(setDefaultBashPath(file)));
        }
    }

    @Override
    public void create(String file) throws IOException {
        FileSystem fs = null;
        fs = getFileSystem();
        boolean fileExist = isFileExist(setDefaultBashPath(file));
        if (!fileExist) {
            fs.create(new Path(setDefaultBashPath(file)));
        }
    }

    @Override
    public InputStream getInputStream(String file) throws IOException {
        Path path = new Path(setDefaultBashPath(file));
        return getFileSystem().open(path);
    }

    @Override
    public OutputStream getOutputStream(String file) throws IOException {
        Path path = new Path(setDefaultBashPath(file));
        return getFileSystem().create(path);
    }

    @Override
    public boolean isFileExist(String file) throws IOException {
        FileSystem fs = null;
        fs = getFileSystem();
        Path pathSrc = new Path(setDefaultBashPath(file));
        boolean result = fs.exists(pathSrc);
        return result;
    }

    @Override
    public List<FileInfo> getDirFiles(String file) throws IOException {
        FileStatus[] result = null;
        FileSystem fs = null;
        fs = getFileSystem();
        Path pathSrc = new Path(setDefaultBashPath(file));
        result = fs.listStatus(pathSrc);
        if (result == null || result.length == 0) {
            return new ArrayList<>();
        }
        return Stream.of(result).map(s -> HDFSFileUtil.hdfs2FileInfo(s, this)).collect(Collectors.toList());
    }

    @Override
    public FileInfo getFiles(String file) throws IOException {
        FileSystem fs = null;
        fs = getFileSystem();
        Path pathSrc = new Path(setDefaultBashPath(file));
        FileStatus result = fs.getFileStatus(pathSrc);
        return HDFSFileUtil.hdfs2FileInfo(result, this);
    }

    @Override
    public String getSchema() {
        return "hdfs";
    }

    private FileSystem getFileSystem() {
        return this.fileSystem;

    }
}
