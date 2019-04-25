package com.liferay.lm.hook;

import java.util.Collection;

import org.osgi.framework.hooks.resolver.ResolverHook;
import org.osgi.framework.hooks.resolver.ResolverHookFactory;
import org.osgi.framework.wiring.BundleRevision;

public class LMResolverHookFactory implements ResolverHookFactory {

	public LMResolverHookFactory() {
	}

	@Override
	public ResolverHook begin(Collection<BundleRevision> triggers) {
		return new LMResolverHook();
	}

	public void close() {
		
	}

}