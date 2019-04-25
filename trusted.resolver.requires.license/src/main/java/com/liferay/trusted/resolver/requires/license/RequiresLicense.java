package com.liferay.trusted.resolver.requires.license;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(service = RequiresLicense.class, immediate = true)
public class RequiresLicense {
	
	@Activate
	public void activated() {
		System.out.println("I am now activated.");
	}
	
	@Deactivate
	public void deactivated() {
		System.out.println("I am now de-activated.");
	}
	
}
