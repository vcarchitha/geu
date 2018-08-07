package com.geu.aem.web.core.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.geu.aem.web.constants.ResourceConstants;
import com.geu.aem.web.core.models.bean.SearchResultsBean;
import com.geu.aem.web.util.GEUWebUtils;

/**
 * @author nitin.jangir
 *
 */
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SearchResultsModel {

	@Inject
    private QueryBuilder queryBuilder;
	
	Logger logger = LoggerFactory.getLogger(SearchResultsModel.class);
	
	@Inject
	SlingHttpServletRequest request;
	
	private String queryString;
	
	@Inject
	private String searchResultsUrl;
	
	public String getSearchResultsUrl() {
		searchResultsUrl = GEUWebUtils.linkTransformer(this.searchResultsUrl);
		return searchResultsUrl;
	}
	
	public String getQueryString() {
		queryString = request.getParameter("q");
		return queryString;
	}

	private List<SearchResultsBean> searchResultsList;
	
	public List<SearchResultsBean> getSearchResultsList() {
		final Map<String, String> map = new HashMap<String, String>();
		
		searchResultsList = new ArrayList<SearchResultsBean>();
		String queryString = request.getParameter(ResourceConstants.QUERY_PARAM);
		
		if (StringUtils.isNotBlank(queryString)) {
			map.put(ResourceConstants.TYPE, ResourceConstants.CQ_PAGE);
			if (request.getPathInfo().contains(ResourceConstants.CONTENT_GEU_PATH)) {
				map.put(ResourceConstants.PATH, ResourceConstants.CONTENT_GEU_PATH);
			} else if (request.getPathInfo().contains(ResourceConstants.CONTENT_GEHU_PATH)) {
				map.put(ResourceConstants.PATH, ResourceConstants.CONTENT_GEHU_PATH);
			}
			map.put(ResourceConstants.GROUP_P_OR, ResourceConstants.TRUE);
		    map.put(ResourceConstants.GROUP_1_FULLTEXT, queryString);
		    map.put(ResourceConstants.GROUP_1_FULLTEXT_RELPATH, ResourceConstants.JCR_CONTENT + ResourceConstants.FORWARD_SLASH + ResourceConstants.AT + ResourceConstants.JCR_TITLE);
		    map.put(ResourceConstants.GROUP_2_FULLTEXT, queryString);
		    map.put(ResourceConstants.GROUP_2_FULLTEXT_RELPATH, ResourceConstants.JCR_CONTENT + ResourceConstants.FORWARD_SLASH + ResourceConstants.AT + ResourceConstants.JCR_DESCRIPTION);
	        map.put(PredicateGroup.ORDER_BY, ResourceConstants.AT + ResourceConstants.JCR_CONTENT + ResourceConstants.FORWARD_SLASH + ResourceConstants.JCR_LASTMODIFIED);
	        map.put(ResourceConstants.ORDERBY_SORT, PredicateGroup.SORT_DESCENDING);
	        map.put(ResourceConstants.P_LIMIT, ResourceConstants.MINUS_ONE);
		}
        ResourceResolver resourceResolver = request.getResourceResolver();
        Query query = queryBuilder.createQuery(PredicateGroup.create(map), resourceResolver.adaptTo(Session.class));
        
        SearchResult result = query.getResult();
        try {
        	if (!result.getHits().isEmpty()) {
		        for (final Hit hit : result.getHits()) {
		        	SearchResultsBean searchResultsBean = new SearchResultsBean(); 
		        	searchResultsBean.setPagePath(hit.getPath() + ResourceConstants.HTML_EXTENSION);
		        	searchResultsBean.setPageTitle(hit.getTitle());
		        	searchResultsBean.setPageDescription(hit.getProperties().get(ResourceConstants.JCR_DESCRIPTION, ResourceConstants.EMPTY_STRING));
		        	searchResultsList.add(searchResultsBean);
		        }
        	} else {
        		SearchResultsBean searchResultsBean = new SearchResultsBean();
        		searchResultsBean.setPageTitle(ResourceConstants.NO_RESULTS_FOUND);
        		searchResultsList.add(searchResultsBean);
        	}
        } catch (RepositoryException e) {
			logger.error("Repository exception in SearchResultsServlet" + e);
		}
		return searchResultsList;
	}
}
