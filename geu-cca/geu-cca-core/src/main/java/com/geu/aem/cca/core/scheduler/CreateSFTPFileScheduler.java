package com.geu.aem.cca.core.scheduler;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.cca.constants.ResourceConstants;
import com.geu.aem.cca.core.service.MySQLConnectionService;
import com.geu.aem.cca.core.service.SFTPConnectionService;

@Component(
	    immediate = true,
	    configurationPid = "com.geu.aem.cca.core.scheduler.CreateSFTPFileScheduler"
)
@Designate(ocd = CreateSFTPFileScheduler.Configuration.class)
public class CreateSFTPFileScheduler implements Runnable {
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@Reference
	MySQLConnectionService mySQLConnectionService;
	
	@Reference
	SFTPConnectionService sftpConnService;

	public void run() {
		LOGGER.info("CreateSFTPFileScheduler is running!!");
		if(mySQLConnectionService!=null){
			mySQLConnectionService.writeCSV();
			sftpConnService.readFile();			
			String exportPath = ResourceConstants.EXPORT_CSV_PATH+"/"+ResourceConstants.EXPORT_CSV_FILENAME;
			LOGGER.info("export Path :::"+exportPath);
			mySQLConnectionService.updateTableFromCSV(exportPath);
		}else{
			LOGGER.info("mySQLConnectionService is null in run method ");
		}
	} 

    @ObjectClassDefinition(name="CreateSFTPFile Scheduler")
    public @interface Configuration {
       
        @AttributeDefinition(
            name = "Expression",
            description = "Cron-job expression. Default: run everyday at 6 AM",
            type = AttributeType.STRING
        )
        String scheduler_expression() default "0 6 * * *";
                 
    }

}
