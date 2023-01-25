package cn.lioyan.autoconfigure.exception;

import cn.lioyan.core.exception.BaseException;
import cn.lioyan.core.exception.ExceptionKeys;


/**
 * ServiceException
 *
 * @author com.lioyan
 * @since 2022/4/13 15:14
 */
public class DataMissException extends BaseException
{

    private DataMissException(Integer code)
    {
        super(code);
    }


    public static DataMissException newInstance()
    {
        return new DataMissException(ExceptionKeys.UPDATE_DATA_MISS_ERROR);
    }

}
