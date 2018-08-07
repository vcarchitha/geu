<%@include file="/libs/foundation/global.jsp"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0"%>
<sling:defineObjects />
<%@page import="java.net.URLDecoder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import = "java.io.*,java.util.*,com.geu.aem.web.util.AesCryptUtil,com.geu.aem.web.service.PaymentFormService" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Response Handler</title>
</head>
<body>
	<%
		String workingKey = "07F3E9AF239A82192FD9D412919D8F72";		
		String encResp= request.getParameter("encResp");
		AesCryptUtil aesUtil=new AesCryptUtil(workingKey);
		String decResp = aesUtil.decrypt(encResp);
		StringTokenizer tokenizer = new StringTokenizer(decResp, "&");
		Hashtable hs=new Hashtable();
		String pair=null, pname=null, pvalue=null;
		while (tokenizer.hasMoreTokens()) {
			pair = (String)tokenizer.nextToken();
			if(pair!=null) {
				StringTokenizer strTok=new StringTokenizer(pair, "=");
				pname=""; pvalue="";
				if(strTok.hasMoreTokens()) {
					pname=(String)strTok.nextToken();
					if(strTok.hasMoreTokens())
						pvalue=(String)strTok.nextToken();
					hs.put(pname, URLDecoder.decode(pvalue));
				}
			}
		}
	%>
	<center>
		<font size="4" color="blue"><b>Response Page</b></font>
		<table border="1">
			<%
			PaymentFormService paymentFormService = sling.getService(PaymentFormService.class);
			if (hs.get("order_status").toString().equalsIgnoreCase("Success")) {
				paymentFormService.saveRegistrationPaymentStatus(slingRequest, hs.get("amount").toString(), hs.get("order_id").toString(), "Successful", "GEHU");
				slingResponse.sendRedirect("/content/gehu/en/online-success.html");
			} else {
				paymentFormService.saveRegistrationPaymentStatus(slingRequest, hs.get("amount").toString(), hs.get("order_id").toString(), hs.get("order_status").toString(), "GEHU");
				slingResponse.sendRedirect("/content/gehu/en/online-failure.html");
			}
				Enumeration enumeration = hs.keys();
				while(enumeration.hasMoreElements()) {
					pname=""+enumeration.nextElement();
					pvalue=""+ hs.get(pname);
					
			%>
				<tr>
					<td><%= pname %> </td>
					<td> <%= pvalue %> </td>
				</tr>
			<%
				}
		%>
		</table>
	</center>
</body>
</html>