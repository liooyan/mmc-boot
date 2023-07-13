package cn.lioyan.autoconfigure.data.mybaits;

/**
 * {@link MultipleMybaitsProperties}
 *
 * @author com.lioyan
 * @since 2023/7/13  10:00
 */
public class MultipleMybaitsProperties {

    /**
     * 对应的数据连接模块名称
      */
    private String dataSourceName;

    /**
     * 当前mybaits的基础类路径
     */
    private  String basePackage;

    /**
     * dao 层的 包路径、如果为空，则使用 basePackage + daoName
     */
    private  String daoPackage;

    private  String daoName = "dao";


    /**
     * bean 层的 包路径、如果为空，则使用 basePackage + beanName
     */
    private  String beanPackage;

    private  String beanName = "bean";

    /**
     * dao 层的handler 包路径、如果为空，则使用 basePackage + daoHandlerName
     */
    private  String daoHandlerPackage;

    private  String daoHandlerName = "dao.handler";

    /**
     * mapper xml 文件的路径,指定到具体的包路径。
     */
    private String xmlBasePath = "classpath:mapper";

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getDaoPackage() {
        return daoPackage;
    }

    public void setDaoPackage(String daoPackage) {
        this.daoPackage = daoPackage;
    }

    public String getDaoName() {
        return daoName;
    }

    public void setDaoName(String daoName) {
        this.daoName = daoName;
    }

    public String getBeanPackage() {
        return beanPackage;
    }

    public void setBeanPackage(String beanPackage) {
        this.beanPackage = beanPackage;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getDaoHandlerPackage() {
        return daoHandlerPackage;
    }

    public void setDaoHandlerPackage(String daoHandlerPackage) {
        this.daoHandlerPackage = daoHandlerPackage;
    }

    public String getDaoHandlerName() {
        return daoHandlerName;
    }

    public void setDaoHandlerName(String daoHandlerName) {
        this.daoHandlerName = daoHandlerName;
    }

    public String getXmlBasePath() {
        return xmlBasePath;
    }

    public void setXmlBasePath(String xmlBasePath) {
        this.xmlBasePath = xmlBasePath;
    }
}
