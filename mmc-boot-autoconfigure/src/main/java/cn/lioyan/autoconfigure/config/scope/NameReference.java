package cn.lioyan.autoconfigure.config.scope;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * {@link NameReference}
 * 名称引用
 *
 * @author com.lioyan
 * @since 2023/7/11  14:33
 */
public class NameReference {

    private Map<String, String> aliasMap = new HashMap<>();


    public void addAlias(String name, String alias) {

        //如果 alias 是null，那么就是name
        if (alias == null) {
            alias = name;
        }
        String startAlias = alias;
        //校验循环
        Set<String> traverseSet = new HashSet<>();
        while (true) {
            if (traverseSet.contains(alias)) {
                throw new RuntimeException("循环引用");
            }
            traverseSet.add(alias);
            String aliasName = aliasMap.get(alias);

            if (aliasName == null) {
                break;
            }
            if (aliasName.equals(alias)) {
                break;
            }
            alias = aliasName;
        }
        aliasMap.put(name, startAlias);
    }

    public String getAlias(String name) {
        String alias = aliasMap.get(name);
        if (alias == null) {
            return name;
        }
        if (name.equals(alias)) {
            return name;
        }
        return getAlias(alias);
    }

    //递归aliasMap，返回所有真实的名称
    public String[] getAliasGroundName() {
        Set<String> groundName = new HashSet<>();
        for (Map.Entry<String, String> entry : aliasMap.entrySet()) {
            String key = entry.getKey();
            groundName.add(getAlias(key));
        }
        return groundName.toArray(new String[0]);
    }

}
