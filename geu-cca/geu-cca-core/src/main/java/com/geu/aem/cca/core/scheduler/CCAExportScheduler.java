package com.geu.aem.cca.core.scheduler;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.cca.core.service.ExportLeadsConnectionService;

@Component(immediate = true, configurationPid = "com.geu.aem.cca.core.scheduler.ImportExportScheduler")
@Designate(ocd = CCAExportScheduler.Configuration.class)
public class CCAExportScheduler implements Runnable {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Reference
	ExportLeadsConnectionService connectionService;

	public void run() {
		LOGGER.info("ExportLeadsConnectionService is running!!");
		if (connectionService != null) {
			connectionService.processExportLeads();
		} else {
			LOGGER.info("mySQLConnectionService is null in run method ");
		}
	}

	@ObjectClassDefinition(name = "CCA Export Scheduler")
	public @interface Configuration {

		@AttributeDefinition(name = "Expression", description = "Cron-job expression. Default: run everyday at 6 AM", type = AttributeType.STRING)
		String scheduler_expression() default "0 6 * * *";

	}
}
