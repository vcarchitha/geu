package com.geu.aem.web.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = { SlingHttpServletRequest.class }, adapters = { Title.class }, resourceType = { "geu-web/components/content/title" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", extensions = { "json" })
public class TitleImpl implements Title {
	protected static final String RESOURCE_TYPE = "geu-web/components/content/title";

	@ScriptVariable
	private Page currentPage;

	@ScriptVariable
	private Style currentStyle;

	@ValueMapValue(name = "jcr:title")
	private String title;

	@ValueMapValue
	private String type;
	private Heading heading;

	@PostConstruct
	private void initModel() {
		if (StringUtils.isBlank(this.title)) {
			this.title = StringUtils.defaultIfEmpty(
					this.currentPage.getPageTitle(),
					this.currentPage.getTitle());
		}

		if (this.heading == null) {
			this.heading = Heading.getHeading(this.type);
			if (this.heading == null)
				this.heading = Heading.getHeading((String) this.currentStyle
						.get("type", String.class));
		}
	}

	public String getText() {
		return this.title;
	}

	public String getType() {
		if (this.heading != null) {
			return this.heading.getElement();
		}
		return null;
	}

	private static enum Heading {
		H1("h1"), H2("h2"), H3("h3"), H4("h4"), H5("h5"), H6("h6");

		private String element;

		private Heading(String element) {
			this.element = element;
		}

		private static Heading getHeading(String value) {
			for (Heading heading : values()) {
				if (StringUtils.equalsIgnoreCase(heading.element, value)) {
					return heading;
				}
			}
			return null;
		}

		public String getElement() {
			return this.element;
		}
	}
}