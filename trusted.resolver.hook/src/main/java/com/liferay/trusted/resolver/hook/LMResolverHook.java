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

import java.security.cert.X509Certificate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.osgi.framework.Bundle;
import org.osgi.framework.hooks.resolver.ResolverHook;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;

/**
 * @author Gregory Amerson
 */
public class LMResolverHook implements ResolverHook {

	@Override
	public void end() {
		System.out.println("end");
	}

	@Override
	public void filterMatches(BundleRequirement requirement, Collection<BundleCapability> candidates) {
		System.out.println(requirement);
	}

	@Override
	public void filterResolvable(Collection<BundleRevision> candidates) {
		Collection<BundleRevision> toRemove =
			candidates.stream().filter(this::_shouldFilter).collect(Collectors.toSet());

		candidates.removeAll(toRemove);

		Collection<BundleRevision> notSigned =
			candidates.stream().filter(this::_requiresLiferaySignature).filter(this::_missingLiferaySignature).collect(Collectors.toSet());

		candidates.removeAll(notSigned);
	}

	@Override
	public void filterSingletonCollisions(BundleCapability singleton,
			Collection<BundleCapability> collisionCandidates) {

		System.out.println(singleton);
	}

	private boolean _missingLiferaySignature(BundleRevision bundleRevision) {
		Bundle bundle = bundleRevision.getBundle();

		
		Map<X509Certificate, List<X509Certificate>> certs = bundle.getSignerCertificates(Bundle.SIGNERS_TRUSTED);
		

		if (certs.size() == 0) {
			return true;
		}
		
		return false;
	}

	private boolean _requiresLiferaySignature(BundleRevision bundleRevision) {
		String bsn = bundleRevision.getSymbolicName();
		

		if (bsn.startsWith("com.liferay.lm.requires")) {
			return true;
		}
		
		return false;
	}

	private boolean _shouldFilter(BundleRevision bundleRevision) {
		return false;
	}

}