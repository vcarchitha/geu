package com.geu.aem.web.workflow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.json.simple.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourcePool;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.service.AWSEmailService;
import com.geu.aem.web.util.GEUWebUtils;
import com.geu.aem.web.util.SQLConnectionUtil;

/**
 * @author 
 *
 */
@Component(service = WorkflowProcess.class, property = { "process.label=GEU Offline Payment Update Workflow" })
public class OfflinePaymentUpdateWorkflow implements WorkflowProcess {
	
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Reference
	ResourceResolverFactory resolverFactory;

	@Reference
	AWSEmailService awsEmailService;

	@Reference
	private DataSourcePool source;

	/* (non-Javadoc)
	 * @see com.day.cq.workflow.exec.WorkflowProcess#execute(com.day.cq.workflow.exec.WorkItem, com.day.cq.workflow.WorkflowSession, com.day.cq.workflow.metadata.MetaDataMap)
	 */
	//@Override
	public void execute(WorkItem item, WorkflowSession workflowSession,
			MetaDataMap args) throws WorkflowException {
		
		Connection connection=null;
		try {
			List<String> successList=new ArrayList<String>();
			List<String> errorList=new ArrayList<String>();
			WorkflowData workflowData = item.getWorkflowData();
			String excelPath = workflowData.getPayload().toString();
		    connection=SQLConnectionUtil.getConnection(source);
			String paymentStatusQuery= "UPDATE "+ ResourceConstants.ADMISSION_TABLE_NAME 
					+" SET paymentStatus= ? "
					+"WHERE "+ResourceConstants.FORM_ID+" = ?";
			
			Iterator<Row> excelRowiterator = GEUWebUtils.getExcelData(resolverFactory, excelPath);
			if((null != connection) && (null !=excelRowiterator )){
			PreparedStatement offlinePaymentPreparedStmt = connection
					.prepareStatement(paymentStatusQuery);
			while (excelRowiterator.hasNext()) {
				Row currentRow = excelRowiterator.next();
					String formID=	currentRow.getCell(0).toString();
					if(StringUtils.isNotBlank(formID)){
						offlinePaymentPreparedStmt.setString(1, "COMPLETED");
						offlinePaymentPreparedStmt.setString(2, formID);
						int	rowAffected = offlinePaymentPreparedStmt.executeUpdate();
						if(rowAffected==1)successList.add(formID);
						else errorList.add(formID);
					}
				
			}
			sendMailToAdmin(successList, errorList);
			}
			

		} catch (SQLException e) {
			LOGGER.error("Error in GEU Offline Payment Update Workflow" + e);
		}finally{
			try {
				if(connection!=null){
					connection.close();
				}
			} catch (SQLException e) {
				LOGGER.error("SQLException occurred in GEU Offline Payment Update Workflow"
						+ e);
			}
		}

	}


	/**
	 * @param successList
	 * @param errorList
	 */
	private void sendMailToAdmin(List<String> successList, List<String> errorList ){
		StringBuilder stringBuilder=new StringBuilder();
		try {
        String jsonPath=ResourceConstants.EMAIL_CONFIG+"offlinePayment.json";
        JSONObject emailObject=	GEUWebUtils.getJSONData(resolverFactory, jsonPath);
        
        if(!successList.isEmpty()){
		stringBuilder.append(emailObject.get("successText").toString());
		for (String successId : successList) {
			stringBuilder.append("<br/>"+successId);
			
		}}
        if(!errorList.isEmpty()){
		stringBuilder.append(emailObject.get("errorText").toString());
		for (String errorId : errorList) {
			stringBuilder.append("<br/>"+errorId);
		}
        }

		String subject=emailObject.get("subject").toString();
		String body=String.join(
				System.getProperty("line.separator"),
				emailObject.get("body").toString(),
				stringBuilder.toString()
				);

		String toAddress=emailObject.get("toAddress").toString();

		Multipart multipart = new MimeMultipart();
		
			BodyPart bodyPart = new MimeBodyPart();
			bodyPart.setContent(body, "text/html");
			multipart.addBodyPart(bodyPart);
			awsEmailService.sendEmail(subject, multipart, toAddress);
		}catch (MessagingException e) {
			LOGGER.error("Messaging Exception in  GEU Offline Payment Update Workflow"+e);
		}catch (NullPointerException e) {
			 LOGGER.error("NullPointerException Exception in  GEU Offline Payment Update Workflow"+e); 
		}

	}

}