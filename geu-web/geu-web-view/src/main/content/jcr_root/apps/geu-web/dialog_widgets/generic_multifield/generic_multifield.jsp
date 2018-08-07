<%@ page import="com.adobe.granite.ui.components.Config" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="com.adobe.granite.ui.components.Value" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@include file="/libs/granite/ui/global.jsp" %>
 
<%--include ootb multifield--%>
<sling:include resourceType="/libs/granite/ui/components/foundation/form/multifield"/>
 
<%!
    private final Logger mLog = LoggerFactory.getLogger(this.getClass());
%>
 
<%
    Config mCfg = cmp.getConfig();
 	Resource mField = mCfg.getChild("field");

    if (mField == null) {
        mLog.warn("Coral : Generic Multi Field : Field node doesn't exist");
        return;
    }
 
    ValueMap mVM = mField.adaptTo(ValueMap.class);
    String mName = mVM.get("name", "");

    if ("".equals(mName)) {
        mLog.warn("Coral : Generic Multi Field : Name property doesn't exist on field node");
        return;
    }

    Value mValue = ((ComponentHelper) cmp).getValue();

%>
 
<script>
    $(document).ready(function () {
            coralGMF.addDataInFields([<%= StringUtils.join(mValue.get(mName, String[].class), ",") %>], "<%=mName%>");
            coralGMF.collectDataFromFields("<%=mName%>");
    });
</script>