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

import java.security.Principal;
import java.security.cert.X509Certificate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.hooks.resolver.ResolverHook;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.resource.Capability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gregory Amerson
 */
public class TrustedResolverHook implements ResolverHook {

	@Override
	public void end() {
	}

	@Override
	public void filterMatches(BundleRequirement requirement, Collection<BundleCapability> candidates) {
	}

	@Override
	public void filterResolvable(Collection<BundleRevision> candidates) {
		candidates.removeAll(
			candidates.stream().filter(this::_requiresTrust).filter(this::_notTrusted).collect(Collectors.toSet()));
	}

	@Override
	public void filterSingletonCollisions(BundleCapability singleton,
			Collection<BundleCapability> collisionCandidates) {
	}

	private boolean _notTrusted(BundleRevision bundleRevision) {
		Bundle bundle = bundleRevision.getBundle();

		Map<X509Certificate, List<X509Certificate>> certificates = bundle.getSignerCertificates(Bundle.SIGNERS_ALL);

		if (certificates.size() == 0) {
			_logger.info("Bundle {} must be signed to be trusted.", bundle.getSymbolicName());

			return true;
		}
		
		Collection<List<X509Certificate>> signers = certificates.values();
		
		List<String> subjects = signers.stream().flatMap(Collection::stream).map(X509Certificate::getSubjectDN).map(Principal::getName).collect(Collectors.toList());
		
		boolean trusted = FrameworkUtil.matchDistinguishedNameChain("cn=*, ou=*, o=Liferay, l=*, st=*, c=*", subjects);
		

		if (!trusted) {
			_logger.info("Bundle {} signature is invalid.", bundle.getSymbolicName());
		}
		
		return !trusted;
	}

	

	private boolean _requiresTrust(BundleRevision bundleRevision) {
		String bsn = bundleRevision.getSymbolicName();
		

		if (bsn.contains("requires.license")) {
			return true;
		}
		
		List<Capability> capabilities = bundleRevision.getCapabilities("dxp-license");
		

		if (capabilities.size() > 0) {
			return true;
		}
		
		return false;
	}

	

	private static final Logger _logger = LoggerFactory.getLogger(TrustedResolverHook.class);

}