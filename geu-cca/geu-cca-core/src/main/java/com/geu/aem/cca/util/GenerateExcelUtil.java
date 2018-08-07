package com.geu.aem.cca.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geu.aem.cca.beans.ExportFields;
import com.geu.aem.cca.beans.LeadBean;

public class GenerateExcelUtil {

	private final static Logger LOGGER = LoggerFactory.getLogger(GenerateExcelUtil.class);
	
	public static void generateExcel(ArrayList<LeadBean> list, String directory) {
		
		try{
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Records");
			Iterator<LeadBean> itrLeadValue = list.iterator();
			HSSFRow row;
			
			createHeader(sheet);		
			
			int rowCounter = 1;
			sheet.setDefaultRowHeightInPoints(5000);

			while (itrLeadValue.hasNext()) {
				LeadBean exportLeadBean = (LeadBean) itrLeadValue.next();

				row = sheet.createRow(rowCounter);
				row.setHeightInPoints((float) 38.25);
				int cellCounter = 0;
				HSSFCell cell;

				// column1
				//cellCounter++;
				cell = row.createCell((short) cellCounter);	
				if(exportLeadBean.getAdmissionId()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getAdmissionId()))));
				}else{
					cell.setCellValue("");
				}				

				// column2
				cellCounter++;
				cell = row.createCell((short) cellCounter);
				if(exportLeadBean.getFirstName()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getFirstName()))));
				}else{
					cell.setCellValue("");
				}
				
				// column3
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getLastName()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getLastName()))));
				}else{
					cell.setCellValue("");
				}
				
				// column4
				cellCounter++;
				cell = row.createCell((short) cellCounter);			
				if(exportLeadBean.getContactNum()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getContactNum()))));
				}else{
					cell.setCellValue("");
				}
				
				//column5			
				cellCounter++;
				cell = row.createCell((short) cellCounter);			
				if(exportLeadBean.getEmail()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getEmail()))));
				}else{
					cell.setCellValue("");
				}
				
				//column6
				cellCounter++;
				cell = row.createCell((short) cellCounter);			
				if(exportLeadBean.getInterest()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getInterest()))));
				}else{
					cell.setCellValue("");
				}
				
				//column7
				cellCounter++;
				cell = row.createCell((short) cellCounter);			
				if(exportLeadBean.getLevel()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getLevel()))));
				}else{
					cell.setCellValue("");
				}
				
				//column8
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getCampusPreference1()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getCampusPreference1()))));
				}else{
					cell.setCellValue("");
				}
				
				//column9
				cellCounter++;
				cell = row.createCell((short) cellCounter);			
				if(exportLeadBean.getCoursePreference1()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getCoursePreference1()))));
				}else{
					cell.setCellValue("");
				}
				
				//column10
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getCampusPreference2()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getCampusPreference2()))));
				}else{
					cell.setCellValue("");
				}
				
				//column11
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getCoursePreference2()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getCoursePreference2()))));
				}else{
					cell.setCellValue("");
				}
				
				//column12
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getGender()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getGender()))));
				}else{
					cell.setCellValue("");
				}
				
				//column13
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getCategory()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getCategory()))));
				}else{
					cell.setCellValue("");
				}
				
				//column14
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getCorrespondenceAddress()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getCorrespondenceAddress()))));
				}else{
					cell.setCellValue("");
				}
				
				//column15
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getPermanentAddress()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getPermanentAddress()))));
				}else{
					cell.setCellValue("");
				}
				
				//column16
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getCity()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getCity()))));
				}else{
					cell.setCellValue("");
				}
				
				//column17
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getState()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getState()))));
				}else{
					cell.setCellValue("");
				}
				
				//column18
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getCountry()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getCountry()))));
				}else{
					cell.setCellValue("");
				}
				
				//column19
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getProvisions()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getProvisions()))));
				}else{
					cell.setCellValue("");
				}
				
				//column20
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getFatherEmail()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getFatherEmail()))));
				}else{
					cell.setCellValue("");
				}
				
				//column21
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getMotherName()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getMotherName()))));
				}else{
					cell.setCellValue("");
				}
				
				//column22
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getMotherContact()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getMotherContact()))));
				}else{
					cell.setCellValue("");
				}
				
				//column23
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getMotherEmail()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getMotherEmail()))));
				}else{
					cell.setCellValue("");
				}
				
				//column24
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getSchoolName10th()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getSchoolName10th()))));
				}else{
					cell.setCellValue("");
				}
				
				//column25
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getAggregateMarks10th()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getAggregateMarks10th()))));
				}else{
					cell.setCellValue("");
				}
				
				//column26
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getBoard10th()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getBoard10th()))));
				}else{
					cell.setCellValue("");
				}
				
				//column27
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getYearOfPassing10th()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getYearOfPassing10th()))));
				}else{
					cell.setCellValue("");
				}
				
				//column28
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getIntermediateSenior()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getIntermediateSenior()))));
				}else{
					cell.setCellValue("");
				}
				
				//column29
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getProjectedMarks12th()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getProjectedMarks12th()))));
				}else{
					cell.setCellValue("");
				}
				
				//column30
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getPreboardYear()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getPreboardYear()))));
				}else{
					cell.setCellValue("");
				}
				
				//column31
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getMarks12th()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getMarks12th()))));
				}else{
					cell.setCellValue("");
				}
				
				//column32
				cellCounter++;
				cell = row.createCell((short) cellCounter);			
				if(exportLeadBean.getBoard12th()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getBoard12th()))));
				}else{
					cell.setCellValue("");
				}
				
				//column33
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getYearOfPassing12th()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getYearOfPassing12th()))));
				}else{
					cell.setCellValue("");
				}
				
				//column34
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getHigherEducationInstitute()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getHigherEducationInstitute()))));
				}else{
					cell.setCellValue("");
				}
				
				//column35
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getCgpaPercentage()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getCgpaPercentage()))));
				}else{
					cell.setCellValue("");
				}
				
				//column36
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getYearOfGraduation()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getYearOfGraduation()))));
				}else{
					cell.setCellValue("");
				}
				
				//column37
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getDateOfBirth()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getDateOfBirth()))));
				}else{
					cell.setCellValue("");
				}
				
				//column38
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getLeadGenerationDate()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getLeadGenerationDate()))));
				}else{
					cell.setCellValue("");
				}
				
				//column39
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getAge()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getAge()))));
				}else{
					cell.setCellValue("");
				}
				
				//column40
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getPriorAssociation()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getPriorAssociation()))));
				}else{
					cell.setCellValue("");
				}
				
				//column41
				cellCounter++;
				cell = row.createCell((short) cellCounter);
				
				if(exportLeadBean.getDateOfAdmission()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getDateOfAdmission()))));
				}else{
					cell.setCellValue("");
				}
				
				//column42
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getPaymentMode()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getPaymentMode()))));
				}else{
					cell.setCellValue("");
				}
				
				//column43
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getLeadSource()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getLeadSource()))));
				}else{
					cell.setCellValue("");
				}
				
				//column44
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getLeadScore()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getLeadScore()))));
				}else{
					cell.setCellValue("");
				}
				
				//column45
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getLeadAllocated()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getLeadAllocated()))));
				}else{
					cell.setCellValue("");
				}
				
				//column46
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getLeadAllocatedTo()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getLeadAllocatedTo()))));
				}else{
					cell.setCellValue("");
				}
				
				//column47
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getLeadStatus()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getLeadStatus()))));
				}else{
					cell.setCellValue("");
				}
				
				//column48
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getFollowUp()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getFollowUp()))));
				}else{
					cell.setCellValue("");
				}
				
				//column49
				cellCounter++;
				cell = row.createCell((short) cellCounter);			
				if(exportLeadBean.getLastFollowUpDate()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getLastFollowUpDate()))));
				}else{
					cell.setCellValue("");
				}
				
				//column50
				cellCounter++;
				cell = row.createCell((short) cellCounter);				
				if(exportLeadBean.getLastComment()!=null){
					cell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(exportLeadBean.getLastComment()))));
				}else{
					cell.setCellValue("");
				}

				rowCounter++;
			}
			writeExcel(workbook, directory);
		} catch(Exception e){
			LOGGER.error("Exception occurred in generateExcel method: "+e.getMessage());
		}
		
	}

	private static void createHeader(HSSFSheet sheet) {
		try{
			int rowHeader = 0;
			sheet.setDefaultRowHeightInPoints(5000);
			HSSFRow row = sheet.createRow(rowHeader);
			row.setHeightInPoints((float) 38.25);
			int cellHeader = 0;
			HSSFCell headerCell;
			for(ExportFields field: ExportFields.values()){
				headerCell = row.createCell((short) cellHeader);
				String lastModified = ExportFields.lastModified.toString();
				if(String.valueOf(field)!=lastModified){
					headerCell.setCellValue(String.valueOf(checkForNull(String
							.valueOf(field))));
				}				
				cellHeader++;
			}			
			
		} catch(Exception e){
			LOGGER.error("Exception occurred in createHeader method of GenerateExcelUtil class:: "+e.getMessage());
		}
	}

	private static Object checkForNull(Object obj) {
		if (obj == null) {
			return "";
		} else {
			return obj;
		}
	}

	private static void writeExcel(HSSFWorkbook workbook, String directory) {
		try {
			FileOutputStream fileOut = new FileOutputStream(directory);
			workbook.write(fileOut);
			fileOut.close();
		} catch (FileNotFoundException e) {
			LOGGER.error("FileNotFoundException occurred in writeExcel class of GenerateExcelUtil class "
					+ e.getMessage());
		} catch (IOException e) {
			LOGGER.error("IOException occurred in writeExcel class of GenerateExcelUtil class "
					+ e.getMessage());
		}
	}

}
