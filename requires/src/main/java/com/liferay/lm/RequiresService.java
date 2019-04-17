package com.liferay.lm;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

@Component(service = RequiresService.class, immediate = true)
public class RequiresService {
	
	@Reference(service = LoggerFactory.class)
	private Logger _logger;
	
	@Activate
	public void activated() {
		_logger.debug("I am now activated.");
	}
	
	@Deactivate
	public void deactivated() {
		_logger.debug("I am now de-activated.");
	}
	
}
