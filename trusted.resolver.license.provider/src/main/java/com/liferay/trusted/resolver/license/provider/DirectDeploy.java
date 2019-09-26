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

package com.liferay.trusted.resolver.license.provider;

import aQute.bnd.header.Attrs;
import aQute.bnd.osgi.Domain;

import java.io.FileInputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Map.Entry;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gregory Amerson
 */
@Component(
	immediate = true,
	service = DirectDeploy.class, 
	property = {
		"osgi.command.scope=dd",
		"osgi.command.function=decode",
		"osgi.command.function=encode",
		"osgi.command.function=directDeploy",
	}
)
public class DirectDeploy {
	
	public DirectDeploy() {
		_decoder = Base64.getDecoder();
		_encoder = Base64.getEncoder();
	}

	public void decode(Path outputPath, String input) {
		try {
			Files.write(outputPath, _decoder.decode(input));
		}
		catch (Exception e) {
			_logger.error("Decode failed.", e);
		}
	}

	public String directDeploy(String base64BundleInput) {
		try {
			Path bundlesPath = Paths.get("/tmp/bundles");

			Files.createDirectories(bundlesPath);
			
			Path bundlePath = Files.createTempFile("bundle", "jar");

			
			Files.write(bundlePath, _decoder.decode(base64BundleInput));
			
			Domain domain = Domain.domain(bundlePath.toFile());

			
			Entry<String, Attrs> attrs = domain.getBundleSymbolicName();
			

			if (attrs != null) {
				String bsn = attrs.getKey();

				
				Path installedBundlePath = bundlesPath.resolve(bsn + ".jar");

				Files.copy(bundlePath, installedBundlePath, StandardCopyOption.REPLACE_EXISTING);
				
				Bundle bundle = FrameworkUtil.getBundle(DirectDeploy.class);

				
				BundleContext bundleContext = bundle.getBundleContext();
				

				try (FileInputStream bundleInputStream = new FileInputStream(installedBundlePath.toFile())) {
					Bundle installedBundle = bundleContext.installBundle(installedBundlePath.toString(), bundleInputStream);

					
					_startBundle(installedBundle);
					
					return "Bundle ID: " + installedBundle.getBundleId();
				}
			}
			else {
				_logger.error("Input is not a bundle. Nothing to deploy");
			}
		}
		catch (Exception e) {
			_logger.error("Direct deploy failed.", e);
		}
		
		return null;
	}

	public String encode(Path inputPath) {
		try {
			return _encoder.encodeToString(Files.readAllBytes(inputPath));
		}
		catch (Exception e) {
			_logger.error("Encode failed.", e);
		}
		
		return null;
	}

	

	private void _startBundle(Bundle installedBundle) throws BundleException {
		installedBundle.start(Bundle.START_ACTIVATION_POLICY);
	}

	

	private static final Logger _logger = LoggerFactory.getLogger(DirectDeploy.class);

	

	private Decoder _decoder;
	private Encoder _encoder;

}