package com.geu.aem.web.core.models;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import com.geu.aem.web.constants.ResourceConstants;

@Component(service = WorkflowProcess.class, property = { "process.label=GEHU Program Update Workflow" })
public class GEHUUpdateProgramWorkflow implements WorkflowProcess {
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Reference
	ResourceResolverFactory resolverFactory;

	@Override
	public void execute(WorkItem item, WorkflowSession workflowSession,
			MetaDataMap args) throws WorkflowException {

		WorkflowData workflowData = item.getWorkflowData();
		String excelPath = workflowData.getPayload()
				+ "/jcr:content/renditions/original";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(ResourceResolverFactory.SUBSERVICE, "getGEUUser");
		ResourceResolver resolver = null;
		Session systemUserSession;

		try {
			resolver = resolverFactory.getServiceResourceResolver(param);
			systemUserSession = resolver.adaptTo(Session.class);
			Node root = systemUserSession.getNode(excelPath);
			Iterator<Row> iterator = readExcelData(root);
			Node node = createParentNode(systemUserSession);
			deleteChildNode(node);
			systemUserSession.save();
			addExcelDataToNode(node, systemUserSession, iterator);
		} catch (LoginException e) {
			log.error("Error in GEHU ExcelWorkflowModel" + e.getMessage());
		} catch (PathNotFoundException e) {
			log.error("Error in GEHU ExcelWorkflowModel" + e.getMessage());
		} catch (RepositoryException e) {
			log.error("Error in GEHU ExcelWorkflowModel" + e.getMessage());
		}
	}

	/**
	 * @param session
	 * @return
	 */
	private Node createParentNode(Session session) {
		Node node = null;

		try {
			node = JcrUtil.createPath(ResourceConstants.LEVEL_PROGRAM_GEHU_PATH,
					"nt:unstructured", session);
			session.save();
		} catch (RepositoryException e) {
			log.error("Error in GEHU ExcelWorkflowModel" + e.getMessage());
		}
		return node;
	}

	/**
	 * @param node
	 */
	private void deleteChildNode(Node node) {
		NodeIterator nodeIterator;
		try {
			nodeIterator = node.getNodes();
			while (nodeIterator.hasNext()) {
				Node existnode = nodeIterator.nextNode();
				existnode.remove();
			}
		} catch (RepositoryException e) {
			log.error("Error in GEHU ExcelWorkflowModel" + e.getMessage());
		}
	}

	/**
	 * @param node
	 * @return
	 */
	private Iterator<Row> readExcelData(Node node) {
		Iterator<Row> iterator = null;
		Node resourceNode;
		try {
			resourceNode = node.getNode("jcr:content");
			InputStream inputStream = resourceNode.getProperty("jcr:data")
					.getBinary().getStream();
			Workbook workbook;
			workbook = new XSSFWorkbook(inputStream);
			Sheet sheet = workbook.getSheetAt(0);
			iterator = sheet.iterator();
		} catch (PathNotFoundException e) {
			log.error("Error in GEHU ExcelWorkflowModel" + e.getMessage());
		} catch (RepositoryException e) {
			log.error("Error in GEHU ExcelWorkflowModel" + e.getMessage());
		} catch (IOException e) {
			log.error("Error in GEHU ExcelWorkflowModel" + e.getMessage());
		}
		return iterator;
	}

	/**
	 * @param node
	 * @param systemUserSession
	 * @param iterator
	 */
	private void addExcelDataToNode(Node node, Session systemUserSession,
			Iterator<Row> iterator) {
		try {
			int row = 1;
			while (iterator.hasNext()) {
				Row currentRow = iterator.next();
				if (row >= 2) {
					Node child = JcrUtil.createPath(
							node.getPath()
									+ "/"
									+ currentRow.getCell(0).toString()
											.replaceAll("\\s+", "")
											.toLowerCase(), "nt:unstructured",
							systemUserSession);

					createProgramNode(child, currentRow, systemUserSession);
					systemUserSession.save();
				}
				row++;
			}
		} catch (RepositoryException e) {
			log.error("Error in GEHU ExcelWorkflowModel" + e.getMessage());
		}
	}

	/**
	 * @param existnode
	 * @param currentRow
	 * @param systemUserSession
	 */
	private void createProgramNode(Node existnode, Row currentRow,
			Session systemUserSession) {

		Node levelnode;
		try {
			levelnode = JcrUtil.createPath(existnode.getPath()
					+ "/"
					+ currentRow.getCell(1).toString().replaceAll("\\s+", "")
							.toLowerCase(), "nt:unstructured",
					systemUserSession);

			levelnode.setProperty("levelTitle", currentRow.getCell(1)
					.toString());

			Node prochild = JcrUtil.createPath(levelnode.getPath()
					+ "/"
					+ currentRow.getCell(2).toString().replaceAll("\\s+", "")
							.toLowerCase(), "nt:unstructured",
					systemUserSession);
			prochild.setProperty("programTitle", currentRow.getCell(2)
					.toString());
			prochild.setProperty("eligibilityText", currentRow.getCell(3)
					.toString());
			prochild.setProperty("fees", currentRow.getCell(4).toString());
			prochild.setProperty("discount%", currentRow.getCell(5).toString());
			prochild.setProperty("duration", currentRow.getCell(6).toString());
		} catch (RepositoryException e) {
			log.error("Error in GEHU ExcelWorkflowModel" + e.getMessage());
		}
	}

}