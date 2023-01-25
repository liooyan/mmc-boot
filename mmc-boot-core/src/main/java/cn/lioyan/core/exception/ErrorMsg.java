package cn.lioyan.core.exception;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * ErrorMsg
 *
 * @author com.lioyan
 * @since 2022/4/13 15:14
 */
public abstract class ErrorMsg {

    private static final List<Properties> errMsgProp;

    static {
        errMsgProp = new ArrayList<>(4);
    }

    public static String getErrorMsg(Integer code){
        for (Properties prop : errMsgProp) {
            String propertyValue = prop.getProperty(code.toString());
            if(propertyValue != null){
                return propertyValue;
            }
        }
        return "";
    }

}
