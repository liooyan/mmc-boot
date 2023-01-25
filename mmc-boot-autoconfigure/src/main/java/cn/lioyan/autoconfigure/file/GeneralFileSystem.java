package cn.lioyan.autoconfigure.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link GeneralFileSystem}
 *
 * @author cn.lioyan
 * @since 2022/4/27 17:24
 */
public class GeneralFileSystem implements BaseFileSystem {

    private final Map<String, AbstractFileSystem> fileSystemMap;

    private AbstractFileSystem defaultFileSystem;

    public GeneralFileSystem(List<AbstractFileSystem> systems, String defaultType) {
        fileSystemMap = new HashMap<>();
        for (AbstractFileSystem system : systems) {
            String schema = system.getSchema();
            fileSystemMap.put(schema, system);
            if (schema.equals(defaultType)) {
                defaultFileSystem = system;
            }
        }

    }

    @Override
    public void deleteFile(String file) throws IOException {
        getSysFileBySchema(file).deleteFile(file);
    }

    @Override
    public void mkdirs(String path) throws IOException {
        getSysFileBySchema(path).mkdirs(path);
    }

    @Override
    public void create(String path) throws IOException {
        getSysFileBySchema(path).create(path);
    }

    @Override
    public InputStream getInputStream(String path) throws IOException {
        return getSysFileBySchema(path).getInputStream(path);
    }

    @Override
    public OutputStream getOutputStream(String path) throws IOException {
        return getSysFileBySchema(path).getOutputStream(path);
    }

    @Override
    public boolean isFileExist(String path) throws IOException {
        return getSysFileBySchema(path).isFileExist(path);
    }

    @Override
    public List<FileInfo> getDirFiles(String path) throws IOException {
        return getSysFileBySchema(path).getDirFiles(path);
    }

    @Override
    public FileInfo getFiles(String path) throws IOException {
        return getSysFileBySchema(path).getFiles(path);
    }

    @Override
    public String getSchema() {
        return null;
    }

    private AbstractFileSystem getSysFileBySchema(String file) {
        try {
            URI uri = new URI(file);
            String scheme = uri.getScheme();
            AbstractFileSystem abstractFileSystem = fileSystemMap.get(scheme);
            if (abstractFileSystem == null) {
                return defaultFileSystem;
            } else {
                return abstractFileSystem;
            }
        } catch (URISyntaxException e) {
            return defaultFileSystem;
        }
    }

    public static void main(String[] args) {

        String a = "file://asd";
        try {
            URI uri = new URI(a);
            System.out.println(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }
}
