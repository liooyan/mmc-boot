package cn.lioyan.autoconfigure.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * {@link FileInfo}
 *
 * @author com.lioyan
 * @since 2022/4/13 15:14
 */
public class FileInfo {

    private String path;
    private String name;
    private long length;
    private Boolean isdir;

    private BaseFileSystem fileSystem;


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public BaseFileSystem getFileSystem() {
        return fileSystem;
    }

    public void setFileSystem(BaseFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public Boolean getIsdir() {
        return isdir;
    }

    public void setIsdir(Boolean isdir) {
        this.isdir = isdir;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取输入流
     *
     * @return 输入流
     * @throws IOException 异常
     */
    public InputStream getInputStream() throws IOException {
        return fileSystem.getInputStream(this.path);
    }



    /**
     * 获取输入流
     *
     * @return 输出流
     * @throws IOException 异常
     */
    public OutputStream getOutputStream() throws IOException {
        return fileSystem.getOutputStream(this.path);
    }
}
