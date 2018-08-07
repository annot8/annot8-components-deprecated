package io.annot8.components.base.components;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

import io.annot8.components.resources.Logging;
import io.annot8.core.components.Annot8Component;
import io.annot8.core.context.Context;
import io.annot8.core.exceptions.BadConfigurationException;
import io.annot8.core.exceptions.MissingResourceException;

public class AbstractComponent implements Annot8Component {

	private Logger logger;

	@Override
	public void configure(Context context) throws BadConfigurationException, MissingResourceException {
		Annot8Component.super.configure(context);

		// Look if we have a logging resource and crate a logger from it is possible
		Optional<Logging> logging = context.getResource(Logging.class);
		if (logging.isPresent()) {
			logger = logging.get().getLogger(getClass());
		} else {
			createNopLogger();
		}
	}

	protected Logger log() {
		// if confiure has not been called we might not have a logger, so check and create is necessary
		if (logger == null) {
			createNopLogger();
		}

		return logger;
	}

	private void createNopLogger() {
		logger = NOPLogger.NOP_LOGGER;
	}
}
