package com.liferay.trusted.resolver.license.provider;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionUpdate;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Processor;

@Component(
	immediate = true,
	service = LicenseManager.class, 
	property = {
		"osgi.command.scope=lm",
		"osgi.command.function=addLicense",
		"osgi.command.function=removeLicense",
	}
)
public class LicenseManager {
	
	private static long _licenseBundleId = -1;
	
	@Reference(service = LoggerFactory.class)
	private Logger _logger;

	public void addLicense() {		
		Properties properties = new Properties();
		
		properties.put("Bundle-SymbolicName", "licenseXXXXadrdX");

		try (Builder builder = new Builder(new Processor(properties, false))) {
			Parameters provideCapabilityHeaders = new Parameters();

			Attrs attrs = new Attrs();

			attrs.put("foo", "bar");
			attrs.put("fizz", "buzz");
			
			provideCapabilityHeaders.add("lm", attrs);

			builder.setProperty(Constants.PROVIDE_CAPABILITY, provideCapabilityHeaders.toString());
			
			Jar licenseJar = builder.build();

			File licenseFile = new File("/tmp/license.jar");
			
			licenseJar.write(licenseFile);
			
			_logger.debug("licenseJar " + licenseJar);
			
			Bundle bundle = FrameworkUtil.getBundle(LicenseManager.class);
			
			BundleContext bundleContext = bundle.getBundleContext();
			
			FileInputStream fileInputStream = new FileInputStream(licenseFile);

			System.out.println("Installing liense bundle.");
			
			Bundle licenseBundle = bundleContext.installBundle("/tmp/license.jar", fileInputStream);
			
			_licenseBundleId = licenseBundle.getBundleId();

			_logger.debug("Installed license bundle id {}", _licenseBundleId);
			
			fileInputStream.close();
			
			licenseBundle.start();

			_logger.debug("Started bundle id {}", _licenseBundleId);
			
			System.out.println("Started bundle id " + _licenseBundleId);
			System.out.println("Started bundle id " + _licenseBundleId);
			
			ConditionalPermissionAdmin cpa = bundleContext.getService(bundleContext.getServiceReference(ConditionalPermissionAdmin.class));
			ConditionalPermissionUpdate update = cpa.newConditionalPermissionUpdate();
			List<ConditionalPermissionInfo> infos = update.getConditionalPermissionInfos();
			System.out.println(infos);
		}
		catch (Exception e) {
			_logger.error(e.getMessage());

			e.printStackTrace();
		}
	}

	public void removeLicense() {
		_logger.debug("Removing bundle id {}", _licenseBundleId);

		Bundle bundle = FrameworkUtil.getBundle(LicenseManager.class);
		
		BundleContext bundleContext = bundle.getBundleContext();
		
		Bundle licenseBundle = bundleContext.getBundle(_licenseBundleId);
		
		BundleWiring bundleWiring = licenseBundle.adapt(BundleWiring.class);
		
		List<BundleWire> lmProvidedWires = bundleWiring.getProvidedWires("lm");
		
		Set<Long> requiringBundleIds = lmProvidedWires.stream(
		).map(
			BundleWire::getRequirer
		).map(
			BundleRevision::getBundle
		).map(
			Bundle::getBundleId
		).collect(
			Collectors.toSet()
		);

		try {
			licenseBundle.uninstall();
			_logger.debug("Uninstalled bundle id ", _licenseBundleId);
			_licenseBundleId = -1;

			requiringBundleIds.stream(
			).map(
				bundleContext::getBundle
			).forEach(
				b -> {
					try {
						b.update();
						_logger.debug("Updated requiring bundle id {}", b.getBundleId());
					} 
					catch (BundleException e) {
						_logger.error(e.getMessage());

						e.printStackTrace();
					}
				}
			);
		} 
		catch (BundleException e) {
			_logger.error(e.getMessage());
			
			e.printStackTrace();
		}
	}
}
