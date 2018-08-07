<%@page import="java.net.URLEncoder"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0"%>
<sling:defineObjects />
<%
   /*
      This is the sample Checkout Page JSP script. It can be directly used for integration with CCAvenue if your application is developed in JSP. You need to simply change the variables to match your variables as well as insert routines (if any) for handling a successful or unsuccessful transaction.
   */
   %>
<%@ page import = "java.io.*,java.util.*,com.geu.aem.web.util.AesCryptUtil,
   com.geu.aem.web.core.service.HDFCPaymentService"
   %>
<html>
   <body>
      <%
         String accessCode= "AVTX01FE78CK81XTKC";		
         String workingKey = "07F3E9AF239A82192FD9D412919D8F72";
               String formId = request.getParameter("formId");
         String selectionMode = request.getParameter("selectionMode");
		 String feeType = request.getParameter("feeType");
		 String amount = request.getParameter("amount"); 
               Map <String,String> hdfcConfigMap = new HashMap<String,String>();
         HDFCPaymentService hdfcService = sling.getService(HDFCPaymentService.class);
         hdfcConfigMap = hdfcService.fetchHDFCConfigFromFormID(formId, selectionMode, feeType, amount);
         String ccaRequest="", pname="", pvalue="";
         
         for(Map.Entry<String, String> e : hdfcConfigMap.entrySet()){
         pname = ""+e.getKey();
         pvalue = e.getValue();
         ccaRequest = ccaRequest + pname + "=" + URLEncoder.encode(pvalue,"UTF-8") + "&";
         }
         AesCryptUtil aesUtil=new AesCryptUtil(workingKey);
         String encRequest = aesUtil.encrypt(ccaRequest);
         %>
      <form id="nonseamless" method="post" name="redirect" action="https://test.ccavenue.com/transaction/transaction.do?command=initiateTransaction"/>
         <input type="hidden" id="encRequest" name="encRequest" value="<%= encRequest %>">
         <input type="hidden" name="access_code" id="access_code" value="<%= accessCode %>">
         <script language='javascript'>document.redirect.submit();</script>
      </form>
   </body>
</html>