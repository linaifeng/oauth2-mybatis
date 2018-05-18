package com.gskwin.authcenter.domain;

import java.io.Serializable;
import java.util.Date;
import com.gskwin.authcenter.infrastructure.DateUtils;

public abstract class AbstractDomain implements Serializable {

	private static final long serialVersionUID = 2393580092249600781L;

	/**
	 * The domain create time.
	 */
	protected Date createTime = DateUtils.now();

	public AbstractDomain() {
	}

	public Date createTime() {
		return createTime;
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractDomain> T createTime(Date createTime) {
		this.createTime = createTime;
		return (T) this;
	}

}