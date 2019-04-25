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

package com.liferay.trusted.resolver.hook;

import org.eclipse.osgi.internal.hookregistry.ActivatorHookFactory;
import org.eclipse.osgi.internal.hookregistry.HookConfigurator;
import org.eclipse.osgi.internal.hookregistry.HookRegistry;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.resolver.ResolverHookFactory;

/**
 * @author Gregory Amerson
 */
public class TrustedResolverHookConfigurator implements ActivatorHookFactory, BundleActivator, HookConfigurator {

	@Override
	public void addHooks(HookRegistry hookRegistry) {
		hookRegistry.addActivatorHookFactory(this);
	}

	@Override
	public BundleActivator createActivator() {
		return this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		_lmResolverHookFactory = new TrustedResolverHookFactory();

		_sr = context.registerService(ResolverHookFactory.class, _lmResolverHookFactory, null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (_sr != null) {
			_sr.unregister();
		}
		
		_lmResolverHookFactory.close();
	}

	private TrustedResolverHookFactory _lmResolverHookFactory;
	private ServiceRegistration<ResolverHookFactory> _sr;

}
