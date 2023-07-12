package cn.lioyan.autoconfigure.file;

import cn.lioyan.autoconfigure.file.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


/**
 * {@link BaseFileSystem}
 *
 * @author com.lioyan
 * @since 2022/4/13 15:14
 */
public interface BaseFileSystem
{

    void deleteFile(String file)
        throws IOException;

    void mkdirs(String path)
        throws IOException;

    void create(String path)
        throws IOException;

    /**
     * 获取输入流
     *
     * @param file 文件路径
     * @return 输入流
     * @throws IOException 异常
     */
    InputStream getInputStream(String file)
        throws IOException;

    /**
     * 获取输入流
     *
     * @param file 文件路径
     * @return 输出流
     * @throws IOException 异常
     */
    OutputStream getOutputStream(String file)
        throws IOException;

    /**
     * 上传文件
     *
     * @param localPath 本地文件路径
     * @param file      上传至hdfs文件路径
     */
    default void uploadFile(FileInfo localPath, String file)
        throws IOException
    {
        try (OutputStream out = localPath.getOutputStream(); InputStream in = this.getInputStream(file);)
        {
            IOUtils.copyBytes(in, out, 1024);
        }
    }

    /**
     * 下载文件
     *
     * @param file      hdfs文件路径
     * @param localPath 下载至本地路径
     */
    default void downloadFile(String file, FileInfo localPath)
        throws IOException
    {
        try (InputStream in = localPath.getInputStream(); OutputStream out = this.getOutputStream(file);)
        {
            IOUtils.copyBytes(in, out, 1024);
        }
    }

    /**
     * 文件是否存在
     *
     * @param hdfsPath hdfs文件路径
     */
    boolean isFileExist(String hdfsPath)
        throws IOException;

    /**
     * 查看目录下文件
     *
     * @param file 目录路径
     * @return 文件列表
     */
    List<FileInfo> getDirFiles(String file)
        throws IOException;

    /**
     * 查看当前文件信息
     *
     * @param file 目录路径
     * @return 文件信息
     */
    FileInfo getFiles(String file)
        throws IOException;

    String getSchema();
}
