package cn.lioyan.core.exception;

/**
 * ServiceException
 *
 * @author com.lioyan
 * @since 2022/4/13 15:14
 */
public class ServiceException extends BaseException{
	
	protected ServiceException(Integer code) {
		super(code);
	}

	protected ServiceException(String msg) {
		super(msg);
	}

	protected ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	protected ServiceException(Throwable cause) {
		super(cause);
	}

	public static ServiceException newInstance(Throwable cause){
		return new ServiceException(cause);
	}

	public static ServiceException newInstance(String message, Throwable cause){
		return new ServiceException(message,cause);
	}

	public static ServiceException newInstance(Integer code){
		return new ServiceException(code);
	}

	public static ServiceException newInstance(String msg){
		return new ServiceException(msg);
	}

}
