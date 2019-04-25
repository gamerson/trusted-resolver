package com.liferay.trusted.resolver.hook;

import org.eclipse.osgi.internal.hookregistry.ActivatorHookFactory;
import org.eclipse.osgi.internal.hookregistry.HookConfigurator;
import org.eclipse.osgi.internal.hookregistry.HookRegistry;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.resolver.ResolverHookFactory;

public class LMHookConfigurator implements ActivatorHookFactory, BundleActivator, HookConfigurator {

	private LMResolverHookFactory _lmResolverHookFactory;
	private ServiceRegistration<ResolverHookFactory> _sr;

	@Override
	public void addHooks(HookRegistry hookRegistry) {
		hookRegistry.addActivatorHookFactory(this);
	}

	@Override
	public void start(BundleContext context) throws Exception {
		_lmResolverHookFactory = new LMResolverHookFactory();

		_sr = context.registerService(ResolverHookFactory.class, _lmResolverHookFactory, null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (_sr != null) {
			_sr.unregister();
		}
		
		_lmResolverHookFactory.close();
	}

	@Override
	public BundleActivator createActivator() {
		return this;
	}

}
