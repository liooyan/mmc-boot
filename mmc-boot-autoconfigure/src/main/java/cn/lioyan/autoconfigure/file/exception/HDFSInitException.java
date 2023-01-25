package cn.lioyan.autoconfigure.file.exception;

import cn.lioyan.core.exception.BaseException;

/**
 * @author top.lioyan
 * @version 1.0
 * @since  2022/5/2 9:03 下午
 */
public class HDFSInitException extends BaseException {
    private HDFSInitException(Integer code)
    {
        super(code);
    }


    public static HDFSInitException newInstance()
    {
        return new HDFSInitException(ExceptionKeys.DIR_FILES_ERROR);
    }

}
