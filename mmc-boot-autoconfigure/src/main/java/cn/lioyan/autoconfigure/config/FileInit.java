package cn.lioyan.autoconfigure.config;

import cn.lioyan.autoconfigure.file.AbstractFileSystem;
import cn.lioyan.autoconfigure.file.BaseFileSystem;
import cn.lioyan.autoconfigure.file.GeneralFileSystem;
import cn.lioyan.autoconfigure.file.hdfs.HDFSFileSystem;
import cn.lioyan.autoconfigure.file.local.LocalFileSystem;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author cn.lioyan
 * @since 2022/4/21 14:00
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({BaseFileSystem.class})
@ConfigurationProperties("sec.file")
public class FileInit {


    private String bashPath;
    private String type;


    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({FileSystem.class})
    @ConfigurationProperties("sec.file.hdf")
    protected class HDFSFileInit {

        private String corePath;
        private String hdfsPath;

        @Bean
        public HDFSFileSystem hdfsFileSystem() {
            return new HDFSFileSystem(getBashPath());
        }

    }


    @Bean
    public LocalFileSystem localFileSystem() {
        return new LocalFileSystem(getBashPath());
    }


    @Bean
    public GeneralFileSystem generalFileSystem(List<AbstractFileSystem> abstractFileSystems) {
        return new GeneralFileSystem(abstractFileSystems, getType());
    }


    public String getBashPath() {
        return bashPath;
    }

    public void setBashPath(String bashPath) {
        this.bashPath = bashPath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}