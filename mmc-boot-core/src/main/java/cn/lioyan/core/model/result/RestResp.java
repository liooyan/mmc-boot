package cn.lioyan.core.model.result;

import cn.lioyan.core.exception.ErrorMsg;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestResp<T> {

	public enum ActionStatusMethod {
		OK,
		FAIL
	}

	@JsonProperty("count")
	private Long count;

	@JsonProperty("ActionStatus")
	private ActionStatusMethod ActionStatus = ActionStatusMethod.OK;

	@JsonProperty("ErrorCode")
	private Integer ErrorCode = 0;

	@JsonProperty("ErrorInfo")
	private String ErrorInfo = "";

	@JsonProperty("Duration")
	private Long duration;

	private T data;

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public RestResp() {
	}

	private RestResp(Integer code, String msg) {
		this.ActionStatus = ActionStatusMethod.FAIL;
		this.ErrorCode = code;
		this.ErrorInfo = msg;
	}

	public RestResp(T data) {
		this.data = data;
	}

	public RestResp(T data, Long count) {
		this.data = data;
		this.count = count;

	}

	@JsonIgnore
	public ActionStatusMethod getActionStatus() {
		return ActionStatus;
	}

	public void setActionStatus(ActionStatusMethod actionStatus) {
		ActionStatus = actionStatus;
	}
	
	@JsonIgnore
	public Integer getErrorCode() {
		return ErrorCode;
	}

	public void setErrorCode(Integer errorCode) {
		ErrorCode = errorCode;
	}
	
	@JsonIgnore
	public String getErrorInfo() {
		return ErrorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		ErrorInfo = errorInfo;
	}

	@JsonIgnore
	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


	public static RestResp<Object> error(Integer code,String msg){
		return new RestResp<>(code, msg);
	}

	public static RestResp<Object> error(Integer code){
		return error(code, ErrorMsg.getErrorMsg(code));
	}
}
