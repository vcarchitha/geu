package com.geu.aem.web.core.models;

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

@Component(service = WorkflowProcess.class, property = { "process.label=GEU Program Update Workflow" })
public class GEUUpdateProgramWorkflow implements WorkflowProcess {
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
			log.error("Error in ExcelWorkflowModel" + e.getMessage());
		} catch (PathNotFoundException e) {
			log.error("Error in ExcelWorkflowModel" + e.getMessage());
		} catch (RepositoryException e) {
			log.error("Error in ExcelWorkflowModel" + e.getMessage());
		}

	}

	/**
	 * @param session
	 * @return
	 */
	private Node createParentNode(Session session) {
		Node node = null;

		try {
			node = JcrUtil.createPath(ResourceConstants.LEVEL_PROGRAM_GEU_PATH,
					"nt:unstructured", session);
			session.save();
		} catch (RepositoryException e) {
			log.error("Error in ExcelWorkflowModel" + e.getMessage());
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
			if (nodeIterator.getSize() >= 1) {
				while (nodeIterator.hasNext()) {
					Node existnode = nodeIterator.nextNode();
					existnode.remove();
				}
			}
		} catch (RepositoryException e) {
			log.error("Error in ExcelWorkflowModel" + e.getMessage());
		}
	}

	/**
	 * @param node
	 * @return
	 */
	private Iterator<Row> readExcelData(Node node) {
		Iterator<Row> iterator = null;
		try {
			Node resourceNode = node.getNode("jcr:content");
			InputStream inputStream = resourceNode.getProperty("jcr:data")
					.getBinary().getStream();

			Workbook workbook = new XSSFWorkbook(inputStream);
			Sheet sheet = workbook.getSheetAt(0);
			iterator = sheet.iterator();
		} catch (Exception e) {
			log.error("Error in ExcelWorkflowModel" + e.getMessage());
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

					boolean flag = createChildtNode(node, systemUserSession,
							currentRow);

					if (!flag) {
						Node child = JcrUtil.createPath(
								node.getPath()
										+ "/"
										+ currentRow.getCell(0).toString()
												.replaceAll("\\s+", "")
												.toLowerCase(),
								"nt:unstructured", systemUserSession);
						child.setProperty("levelTitle", currentRow.getCell(1)
								.toString());
						createProgramNode(child, currentRow, systemUserSession);

						systemUserSession.save();
					}
				}
				row++;
			}

		} catch (Exception e) {
			log.error("Error in ExcelWorkflowModel" + e.getMessage());
		}
	}

	/**
	 * @param node
	 * @param systemUserSession
	 * @param currentRow
	 * @return
	 */
	private boolean createChildtNode(Node node, Session systemUserSession,
			Row currentRow) {
		boolean flag = false;
		try {
			NodeIterator childIterator = node.getNodes();
			while (childIterator.hasNext()) {

				Node existnode = childIterator.nextNode();
				if (existnode.getName().equals(
						currentRow.getCell(0).toString().replaceAll("\\s+", "")
								.toLowerCase())) {
					createProgramNode(existnode, currentRow, systemUserSession);

					systemUserSession.save();
					flag = true;
					break;

				}

			}
		} catch (Exception e) {
			log.error("Error in ExcelWorkflowModel" + e.getMessage());
		}

		return flag;
	}

	/**
	 * @param existnode
	 * @param currentRow
	 * @param systemUserSession
	 */
	private void createProgramNode(Node existnode, Row currentRow,
			Session systemUserSession) {
		try {
			Node prochild = JcrUtil.createPath(existnode.getPath()
					+ "/"
					+ currentRow.getCell(2).toString().replaceAll("\\s+", "")
							.toLowerCase(), "nt:unstructured",
					systemUserSession);
			prochild.setProperty("programTitle", currentRow.getCell(3)
					.toString());
			prochild.setProperty("eligibilityText", currentRow.getCell(4)
					.toString());
			prochild.setProperty("fees", currentRow.getCell(5).toString());
			prochild.setProperty("discount%", currentRow.getCell(6).toString());
			prochild.setProperty("duration", currentRow.getCell(7).toString());
		} catch (Exception e) {
			log.error("Error in ExcelWorkflowModel" + e.getMessage());
		}
	}
}