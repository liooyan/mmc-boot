package cn.lioyan.autoconfigure.file;

import cn.lioyan.autoconfigure.file.exception.DirFilesException;

import java.io.IOException;

/**
 * {@link AbstractFileSystem}
 *
 * @author com.lioyan
 * @since 2022/4/13 15:14
 */
public abstract class AbstractFileSystem implements BaseFileSystem {

    private final String basePath;

    public AbstractFileSystem(String basePath) {
        this.basePath = basePath;
    }


    protected void initBasePath() {
        try {
            if (!isFileExist(basePath)) {
                mkdirs(basePath);
            }
        } catch (IOException e) {
            throw DirFilesException.newInstance();
        }

    }

    protected String setDefaultBashPath(String file) {
        if (file == null || file.startsWith("/") || file.contains(":")) {
            return file;
        } else {
            return basePath + file;
        }

    }

}
