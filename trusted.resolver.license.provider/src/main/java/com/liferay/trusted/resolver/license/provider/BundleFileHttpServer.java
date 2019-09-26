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

import fi.iki.elonen.NanoHTTPD;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.nio.file.Path;

import org.osgi.service.component.annotations.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gregory Amerson
 */
@Component(
	immediate = true,
	service = BundleFileHttpServer.class, 
	property = {
		"osgi.command.scope=dd",
		"osgi.command.function=serveFile",
	}
)
public class BundleFileHttpServer {
	
	
	public String serveFile(Path filePath) throws IOException {
		new NanoHTTPD("localhost", 33133) {

			@Override
			public Response serve(IHTTPSession session) {
				Method method = session.getMethod();
				

				if (Method.GET.equals(method) ) {
					String uri = session.getUri();
					

					if (uri.endsWith(filePath.getFileName().toString())) {
						try {
							return new Response(Response.Status.OK, "application/octet-stream", new FileInputStream(filePath.toFile()), filePath.toFile().length()) {

								@Override
								public void close() throws IOException {
									try {
										super.close();
									}
									finally {
										stop();
									}
								}

							};
						}
						catch (FileNotFoundException fnfe) {
							_logger.error("Unable to serve file.", fnfe);
						}
					}
				}

				return null;
			}

		}.start();
		
		return "http://localhost:33133/" + filePath.getFileName();
	}

	

	private static final Logger _logger = LoggerFactory.getLogger(BundleFileHttpServer.class);

}