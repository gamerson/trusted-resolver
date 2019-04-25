package com.liferay.lm.hook;

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

public class LMResolverHook implements ResolverHook {

	@Override
	public void filterResolvable(Collection<BundleRevision> candidates) {
		Collection<BundleRevision> toRemove =
			candidates.stream().filter(this::_shouldFilter).collect(Collectors.toSet());

		candidates.removeAll(toRemove);

		Collection<BundleRevision> notSigned =
			candidates.stream().filter(this::_requiresLiferaySignature).filter(this::_missingLiferaySignature).collect(Collectors.toSet());

		candidates.removeAll(notSigned);
	}
	
	private boolean _requiresLiferaySignature(BundleRevision bundleRevision) {
		String bsn = bundleRevision.getSymbolicName();
		
		if (bsn.startsWith("com.liferay.lm.requires")) {
			return true;
		}
		
		return false;
	}
	
	private boolean _missingLiferaySignature(BundleRevision bundleRevision) {
		Bundle bundle = bundleRevision.getBundle();
		
		Map<X509Certificate, List<X509Certificate>> certs = bundle.getSignerCertificates(Bundle.SIGNERS_TRUSTED);
		
		if (certs.size() == 0) {
			return true;
		}
		
		return false;
	}

	private boolean _shouldFilter(BundleRevision bundleRevision) {
		String bsn = bundleRevision.getSymbolicName();
		
		//if (bsn.equals("license")) {
		//	return true;
		//}
		
		return false;
	}

	@Override
	public void filterSingletonCollisions(BundleCapability singleton,
			Collection<BundleCapability> collisionCandidates) {
		System.out.println(singleton);
	}

	@Override
	public void filterMatches(BundleRequirement requirement, Collection<BundleCapability> candidates) {
		System.out.println(requirement);
	}

	@Override
	public void end() {
		System.out.println("end");
	}
}