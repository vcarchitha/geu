package com.geu.aem.web.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

/**
 * @author nitin.jangir
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class StatisticsCounterModel {
	
	@Inject
	private String statsImage1;
	
	@Inject
	private String statsCount1;
	
	@Inject
	private String statsTitle1;
	
	@Inject
	private String statsImage2;
	
	@Inject
	private String statsCount2;
	
	@Inject
	private String statsTitle2;
	
	@Inject
	private String statsImage3;
	
	@Inject
	private String statsCount3;
	
	@Inject
	private String statsTitle3;
	
	@Inject
	private String statsImage4;
	
	@Inject
	private String statsCount4;
	
	@Inject
	private String statsTitle4;
	
	@Inject
	private String statsImage5;
	
	@Inject
	private String statsCount5;
	
	@Inject
	private String statsTitle5;

	public String getStatsImage1() {
		return statsImage1;
	}

	public String getStatsCount1() {
		return statsCount1;
	}

	public String getStatsTitle1() {
		return statsTitle1;
	}

	public String getStatsImage2() {
		return statsImage2;
	}

	public String getStatsCount2() {
		return statsCount2;
	}

	public String getStatsTitle2() {
		return statsTitle2;
	}

	public String getStatsImage3() {
		return statsImage3;
	}

	public String getStatsCount3() {
		return statsCount3;
	}

	public String getStatsTitle3() {
		return statsTitle3;
	}

	public String getStatsImage4() {
		return statsImage4;
	}

	public String getStatsCount4() {
		return statsCount4;
	}

	public String getStatsTitle4() {
		return statsTitle4;
	}

	public String getStatsImage5() {
		return statsImage5;
	}

	public String getStatsCount5() {
		return statsCount5;
	}

	public String getStatsTitle5() {
		return statsTitle5;
	}	

}
