package cn.lioyan.core.util;

import java.io.*;
import java.util.Properties;
import java.util.UUID;

public class PropertiesUtils {

    public static Properties loadProperties(String fileName){
        return loadProperties(fileName,PropertiesUtils.class.getClassLoader());
    }

    /**
     * 从系统环境或者系统属性获取指定key的值，前者优先级高，然后解析(如果是/结尾，则自动拼上fileName)
     * @param fileName 文件名
     * @param key 文件路径key
     * @return Properties
     *
     */
    public static Properties loadProperties(String fileName,String key){
        String value = getValueFromSystemEnvOrProp(key, fileName);
        File file = new File(value);
        if(file.isDirectory()){
            file = new File(value,fileName);
        }
        return loadProperties(file.getAbsolutePath());
    }

    private static InputStream getInputStream(String fileName, ClassLoader cls){
        InputStream in;
        try {
            in = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            File file = new File(fileName);
            in = cls.getResourceAsStream(file.getName());
        }
        return in;
    }

    private static Reader getReader(String fileName, ClassLoader cls){
        InputStreamReader in;
        try {
            in = new InputStreamReader(getInputStream(fileName,cls),"UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
        return in;
    }

    private static Properties loadProperties(String fileName,ClassLoader cls){
        Properties properties = new Properties();
        try(Reader in = getReader(fileName,cls)){
            properties.load(in);
        }catch (Exception e) {
            //ignore not found file

        }
        return properties;
    }

    public static String randomUUID(){
        return UUID.randomUUID().toString();
    }

    public static String simpleUUID(){
        return randomUUID().replace("-","");
    }

    public static String getValueFromSystemEnvOrProp(String key){
        String value = System.getenv(key);
        if(NullUtil.isNull(value)){
            value = System.getProperty(key);
        }
        return value;
    }

    public static String getValueFromSystemEnvOrProp(String key,String defaultValue){
        String value = getValueFromSystemEnvOrProp(key);
        return NullUtil.isNull(value) ? defaultValue : value;
    }

    public static boolean isDigital(String value){
        if(NullUtil.isNull(value)){
            return false;
        }
        for (char c : value.toCharArray()) {
            if(c < 48 || c > 57){
                return false;
            }
        }
        return true;
    }
}
