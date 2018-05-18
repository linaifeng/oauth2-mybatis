package com.gskwin.authcenter.common;

import java.util.HashMap;

public enum StatusCode {
	SUCCESS(0),  ERROR(1),  INVALID_REQUEST(2),  INPUT_DATA_TOO_LARGE(3),  NO_RESULT_DATA(4),  AUTHCENTER_ERROR(5),  AUTH_INVALID_REQUEST(1001),  AUTH_UNAUTHORIZED_CLIENT(1002),  AUTH_ACCESS_DENIED(1003),  AUTH_UNSUPPORTED_RESPONSE_TYPE(1004),  AUTH_INVALID_SCOPE(1005),  AUTH_SERVER_ERROR(1006),  AUTH_TEMPORARILY_UNAVAILABLE(1007),  AUTH_INVALID_CLIENT(1008),  AUTH_INVALID_GRANT(1009),  AUTH_UNSUPPORTED_GRANT_TYPE(1010),  AUTH_EXPIRED_TOKEN(1011),  AUTH_INSUFFICIENT_SCOPE(1012),  AUTH_INVALID_TOKEN(1013),  FORMAT_ERROR(9001),  PID_NOT_EXIST(9002),  BLACK_LIST(9003),  SUBJECT_NOT_EXIST(9004),  WRONG_TIME(9005),  NO_ENOUGH_MONEY(9009),  MONEY_FORMAT_WRONG(9010),  POSITION_NOT_ENOUGH(9011),  NO_THIS_ORDER(9012),  CANCEL_ORDER_NOT_SUCCESS(9013),  ORDER_NUM_WRONG(9014),  ORDER_PRICE_WRONG(9015),  DATE_WRONG(9016),  TIME_WRONG(9017),  RISK_CONTROL_NOT_SUCCESS(9018),  PIDIS_DISABLE(9019),  DATABASE_FAIL(9020),  READ_QUOTATION_FAIL(9021),  REQUIRED_CHECK_FAIL(9022),  CALL_TRADE_SERVER_FAIL(9024),  RESULT_FORMAT_ERROR(9025),  CALL_BROKER_AGENT_FAIL(9026),  BROKER_AGENT_NO_RETURN(9028),  PID_NO_POSITION(9029),  CANCEL_ORDER_NO_CONTRACT_NUMBER(9030),  SUBJECT_IN_BLACK_LIST(9031),  SUBJECT_NOT_IN_WHITE_LIST(9032),  SUBJECT_IS_SUSPENDED(9033),  WARNING_STOP(9034);

	private final int value;
	private static HashMap<Integer, StatusCode> codeMap;
	public static HashMap<String, StatusCode> codeDescriptionMap;

	static {
		codeMap = new HashMap();
		for (StatusCode statusCode : values()) {
			codeMap.put(Integer.valueOf(statusCode.value), statusCode);
		}
		codeDescriptionMap = new HashMap();
		codeDescriptionMap.put("invalid_request", AUTH_INVALID_REQUEST);
		codeDescriptionMap.put("unauthorized_client", AUTH_UNAUTHORIZED_CLIENT);
		codeDescriptionMap.put("access_denied", AUTH_ACCESS_DENIED);
		codeDescriptionMap.put("unsupported_response_type", AUTH_UNSUPPORTED_RESPONSE_TYPE);
		codeDescriptionMap.put("invalid_scope", AUTH_INVALID_SCOPE);
		codeDescriptionMap.put("server_error", AUTH_SERVER_ERROR);
		codeDescriptionMap.put("temporarily_unavailable", AUTH_TEMPORARILY_UNAVAILABLE);
		codeDescriptionMap.put("invalid_client", AUTH_INVALID_CLIENT);
		codeDescriptionMap.put("invalid_grant", AUTH_INVALID_GRANT);
		codeDescriptionMap.put("unsupported_grant_type", AUTH_UNSUPPORTED_GRANT_TYPE);
		codeDescriptionMap.put("expired_token", AUTH_EXPIRED_TOKEN);
		codeDescriptionMap.put("insufficient_scope", AUTH_INSUFFICIENT_SCOPE);
		codeDescriptionMap.put("invalid_token", AUTH_INVALID_TOKEN);
	}

	private StatusCode(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public static StatusCode getEnum(int value) {
		if (!codeMap.containsKey(Integer.valueOf(value))) {
			throw new IllegalArgumentException("Unknown Value: " + value);
		}
		return (StatusCode) codeMap.get(Integer.valueOf(value));
	}

	public String toString() {
		return Integer.toString(this.value);
	}

}
