package cn.lioyan.autoconfigure.exception;

import cn.lioyan.core.exception.BaseException;
import cn.lioyan.core.exception.ExceptionKeys;


/**
 * ServiceException
 *
 * @author com.lioyan
 * @since 2022/4/13 15:14
 */
public class DataExistsException extends BaseException
{

    private DataExistsException(Integer code)
    {
        super(code);
    }


    public static DataExistsException newInstance()
    {
        return new DataExistsException(ExceptionKeys.UPDATE_DATA_MISS_ERROR);
    }

}
