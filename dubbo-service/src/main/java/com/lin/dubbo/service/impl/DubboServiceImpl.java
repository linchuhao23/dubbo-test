package com.lin.dubbo.service.impl;

import org.springframework.stereotype.Component;

import com.lin.dubbo.api.DubboService;

@com.alibaba.dubbo.config.annotation.Service(version = "1.0", group = "DubboService")
@Component
public class DubboServiceImpl implements  DubboService {

	@Override
	public String getDubboServiceName() {
		return "dubbo-service";
	}

}
