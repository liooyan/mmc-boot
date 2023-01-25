package cn.lioyan.autoconfigure.file.local;

import cn.lioyan.autoconfigure.file.AbstractFileSystem;
import cn.lioyan.autoconfigure.file.FileInfo;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link LocalFileSystem}
 *
 * @author com.lioyan
 * @since 2022/4/13 15:14
 */
public class LocalFileSystem extends AbstractFileSystem {


    public LocalFileSystem(String basePath) {
        super(basePath);
        initBasePath();
    }

    @Override
    public void deleteFile(String file) throws IOException {
        new File(setDefaultBashPath(file)).delete();
    }

    @Override
    public void mkdirs(String file) throws IOException {
        new File(setDefaultBashPath(file)).mkdirs();
    }

    @Override
    public void create(String file) throws IOException {
        new File(setDefaultBashPath(file)).createNewFile();
    }

    @Override
    public InputStream getInputStream(String file) throws IOException {
        return new FileInputStream(setDefaultBashPath(file));
    }

    @Override
    public OutputStream getOutputStream(String file) throws IOException {
        return new FileOutputStream(setDefaultBashPath(file));
    }

    @Override
    public boolean isFileExist(String file) throws IOException {
        return new File(setDefaultBashPath(file)).isFile();
    }

    @Override
    public List<FileInfo> getDirFiles(String file) throws IOException {
        File[] files = new File(setDefaultBashPath(file)).listFiles();
        return Stream.of(files).map(s -> LocalFileUtil.file2FileInfo(s, this)).collect(Collectors.toList());
    }

    @Override
    public FileInfo getFiles(String file) throws IOException {
        return LocalFileUtil.file2FileInfo(new File(setDefaultBashPath(file)), this);
    }

    @Override
    public String getSchema() {
        return "file";
    }


}
