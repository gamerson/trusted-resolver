package com.liferay.trusted.resolver.license.provider;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.felix.service.command.Converter;

import org.osgi.service.component.annotations.Component;

@Component
public class PathConverter implements Converter {

	@Override
	public Object convert(Class<?> desiredType, Object in) throws Exception {
		if (Path.class.equals(desiredType) && in instanceof String) {
			return Paths.get((String)in);
		}

		return null;
	}

	@Override
	public CharSequence format(Object target, int level, Converter escape) throws Exception {
		return null;
	}

}
