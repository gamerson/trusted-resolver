/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.trusted.resolver.requires.license;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Gregory Amerson
 */
@Component(service = RequiresLicense.class, immediate = true)
public class RequiresLicense {
	
	@Activate
	public void activated() {
		System.out.println("RequiresLicense: I am now activated.");
	}

	@Deactivate
	public void deactivated() {
		System.out.println("RequiresLicense: I am now de-activated.");
	}

	
}
