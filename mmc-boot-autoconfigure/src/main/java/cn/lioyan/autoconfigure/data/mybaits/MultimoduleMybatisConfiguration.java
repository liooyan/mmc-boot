package cn.lioyan.autoconfigure.data.mybaits;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * {@link MultimoduleMybatisConfiguration}
 *
 * @author com.lioyan
 * @since 2022/1/2 22:21
 */
public class MultimoduleMybatisConfiguration extends MybatisConfiguration {


private  String jdbcType;

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    @Override
    public void addMappedStatement(MappedStatement ms) {
        if (mappedStatements.containsKey(ms.getId())) {
            String resource = ms.getResource();
           if(resource.contains(jdbcType)){
               mappedStatements.remove(ms.getId());
               mappedStatements.put(ms.getId(), ms);
           }
            return;
        }
        mappedStatements.put(ms.getId(), ms);
    }
}
