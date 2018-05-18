package com.gskwin.authcenter.common;

import com.alibaba.fastjson.JSONObject;

public class ResponseEntity {
	private StatusCode status;
	private String msg;
	private Object data;

	public ResponseEntity() {
	}

	public ResponseEntity(StatusCode status) {
		this.status = status;
	}

	public ResponseEntity(StatusCode status, String msg) {
		this.status = status;
		this.msg = msg;
	}

	public ResponseEntity(StatusCode status, String msg, Object data) {
		this.status = status;
		this.msg = msg;
		this.data = data;
	}

	public int getStatus() {
		return this.status.getValue();
	}

	public ResponseEntity setStatus(int code) {
		this.status = StatusCode.getEnum(code);
		return this;
	}

	public String getMsg() {
		return this.msg;
	}

	public ResponseEntity setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public Object getData() {
		return this.data;
	}

	public ResponseEntity setData(Object data) {
		this.data = data;
		return this;
	}

	public String toString() {
		return JSONObject.toJSONString(this);
	}
}