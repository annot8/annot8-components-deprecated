package io.annot8.components.resources;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLoggerFactory;

import io.annot8.core.components.Resource;

public final class Logging implements Resource {

	private final ILoggerFactory iFactory;

	private final boolean useLoggerFactory;


	protected Logging(boolean useLoggerFactory, ILoggerFactory iLoggerFactory) {
		// Ensure that we have at least something to create a logger with
		
		if(useLoggerFactory) {
			this.useLoggerFactory = useLoggerFactory;
			this.iFactory = null;
		} else if(iLoggerFactory != null) {
			this.useLoggerFactory  = false;
			this.iFactory = iLoggerFactory;
		} else {
			this.useLoggerFactory  = false;
			this.iFactory = new NOPLoggerFactory();
		}
	}

	public Logger getLogger(Class<?> clazz) {
		return getLogger(clazz.getName());
	}
	
	public Logger getLogger(String name) {
		if(useLoggerFactory) {
			return LoggerFactory.getLogger(name);
		} else {
			return iFactory.getLogger(name);
		} 
	}
	
	public static Logging useLoggerFactory() {
		return new Logging(true, null);
	}
	
	public static Logging useILoggerFactory(ILoggerFactory iLoggerFactory) {
		return new Logging(false, iLoggerFactory);
	}
	
	public static Logging notAvailable() {
		return new Logging(false, null);
	}
}
