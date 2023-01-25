package cn.lioyan.core.exception;

/**
 *
 *  异常状态码
 *
 * {@link ExceptionKeys}
 *
 * @author com.lioyan
 * @since 2022/4/13 15:14
 */
public interface ExceptionKeys {


    int JSON_PARSE_ERROR = 300001;
    int PARAM_PARSE_ERROR = 300002;


    int UPDATE_DATA_MISS_ERROR = 400001;
    int UPDATE_DATA_EXISTS_ERROR = 400002;

    int HTTP_ERROR_401 = 800401;
    int HTTP_ERROR_402 = 800402;
    int HTTP_ERROR_403 = 800403;
    int HTTP_ERROR_404 = 800404;
    int HTTP_ERROR_405 = 800405;
    int HTTP_ERROR_406 = 800406;
    int HTTP_ERROR_408 = 800408;
    int HTTP_ERROR_409 = 800409;
    int HTTP_ERROR_500 = 800500;
    int HTTP_ERROR_503 = 800503;



    int DEFAULT_ERROR_CODE = 999999;

}
