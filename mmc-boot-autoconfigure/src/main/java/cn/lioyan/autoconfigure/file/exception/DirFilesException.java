package cn.lioyan.autoconfigure.file.exception;

import cn.lioyan.core.exception.BaseException;


/**
 * ServiceException
 *
 * @author com.lioyan
 * @since 2022/4/13 15:14
 */
public class DirFilesException extends BaseException
{

    private DirFilesException(Integer code)
    {
        super(code);
    }


    public static DirFilesException newInstance()
    {
        return new DirFilesException(ExceptionKeys.DIR_FILES_ERROR);
    }

}
