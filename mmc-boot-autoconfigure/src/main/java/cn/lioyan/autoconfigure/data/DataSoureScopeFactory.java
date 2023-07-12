package cn.lioyan.autoconfigure.data;

import cn.lioyan.autoconfigure.config.scope.ScopeFactory;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import javax.sql.DataSource;

/**
 * {@link DataSoureScopeFactory}
 *
 * @author com.lioyan
 * @since 2023/7/12  19:43
 */
public class DataSoureScopeFactory implements ScopeFactory<DataSource, DataSourceProperties> {
    @Override
    public String getConfigBasePath() {
        return "datasource";
    }

    @Override
    public Class<DataSourceProperties> getConfigClass() {
        return DataSourceProperties.class;
    }

    @Override
    public Class<DataSource> getBeanClass() {
        return DataSource.class;
    }

    @Override
    public DataSource getBean(DataSourceProperties dataSourceProperties) {
        HikariDataSource hikariDataSource = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        return new HikariDataSource(hikariDataSource);
    }
}
