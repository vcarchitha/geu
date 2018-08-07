
<%@ page contentType="text/html;charset=ISO-8859-1" language="java" %>

<%@ page import="java.util.List,
         java.util.ArrayList,
         java.util.Collections,
         java.util.Comparator,
         java.util.Iterator,
         java.util.Enumeration,
         java.util.Date,
         java.security.MessageDigest,
         java.util.Map,
         java.net.URLEncoder,
         java.util.HashMap,
         java.security.*,
         java.security.spec.InvalidKeySpecException,
         javax.crypto.*,
         javax.crypto.spec.SecretKeySpec,
         org.apache.commons.codec.binary.Base64"%>

<%!   
     // Store this key in a file or database. Do not keep this key in jsp 
    //Fetch secureHash from the database
             String SECURE_SECRET = "F7E08B6D053444EDAA8C123B6A4674EF";//"284AC39C3104D7DBA702B6534A17C65B";//secureHash
String  encValue = " ";//"CCE8B1D6C85D3B44A73098014E22C53C";
   
  //String URL = "https://uat-geniusepay.in/VAS/DCC/doEnc.action";
String vpc_URL = "https://geniusepay.in/VAS/DCC/doEnc.action";//"https://uat-geniusepay.in/EMA/Axis/emaEnc.action";
   
    String hashKeys = new String();
    String hashValues = new String();
    
    String hashAllFields(Map fields) {
        
        hashKeys = "";
        hashValues = "";
        
        List fieldNames = new ArrayList(fields.keySet());
        Collections.sort(fieldNames);

        // create a buffer for the SHA-256 input and add the secure secret first
        StringBuffer buf = new StringBuffer();
        buf.append(SECURE_SECRET);

        // iterate through the list and add the remaining field values
        Iterator itr = fieldNames.iterator();
        
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) fields.get(fieldName);
            hashKeys += fieldName + ", ";
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                
                buf.append(fieldValue);
                
            }
        }
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(buf.toString().getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();
            
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
           throw new RuntimeException(ex);
        }
        
    } // end hashAllFields()


public String encrypt(String Data,String keySet) throws Exception {
        byte[] keyByte = keySet.getBytes();
        Key key = generateKey(keyByte);
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, key); //2
            byte[] encVal = c.doFinal(Data.getBytes()); //1
            byte[] encryptedByteValue = new Base64().encode(encVal); //3
            String encryptedValue = new String(encryptedByteValue); //4
            return encryptedValue;
    }

    private static Key generateKey(byte[] keyByte) throws Exception {
        Key key = new SecretKeySpec (keyByte, "AES");
        return key;
}
%><%    
    
    Map fields = new HashMap();
    Enumeration e = request.getParameterNames();
    
    while (e.hasMoreElements()) {
        String fieldName = (String) e.nextElement();
        String fieldValue = request.getParameter(fieldName);
        if ((fieldValue != null) && (fieldValue.length() > 0)) {
            fields.put(fieldName, fieldValue);
        }
    }

    // no need to send the ema url and submit button to the ema
    fields.remove("SubButL");
    
    fields.remove("inprocess");
    fields.remove(":cq_csrf_token");
    fields.put("submit", "Continue");
        //SECURE_SECRET = "DF8637CB5D7B9F2C6467C65457C5A2FA"; //Add secure secret here
String encKey= "39F6684649F0195E1D77A23AE19AC188";// "CCE8B1D6C85D3B44A73098014E22C53C"; //Add encryption key here
    if (SECURE_SECRET != null && SECURE_SECRET.length() > 0) {
        String secureHash = hashAllFields(fields);
        fields.put("vpc_SecureHash", secureHash);
    }
    List fieldNames = new ArrayList(fields.keySet());
    Iterator itr = fieldNames.iterator();
   StringBuffer sb=new StringBuffer();
     while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) { 
    sb.append(fieldName);
    sb.append("=");
    sb.append(fieldValue);
    sb.append("::");
    }
        }

     encValue=encrypt(sb.toString(),encKey);
    response.setHeader("Content-Type", "text/html; charset=ISO-8859-1");
    response.setHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT");
    response.setDateHeader("Last-Modified", new Date().getTime());
    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
    response.setHeader("Pragma", "no-cache");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

    <head><title>eComm Merchant Adapter Example</title>
        <meta http-equiv='Content-Type' content='text/html; charset=ISO-8859-1'>
        <meta http-equiv="cache-control" content="no-cache" />
        <meta http-equiv="pragma" content="no-cache" />
        <meta http-equiv="expires" content="0" />
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
        <script type="text/javascript">
            history.forward();
        </script>
    </head>

    <body>



        <form name="RedirectForm" action="<%=vpc_URL%>" method="post">
            <table width="80%" align="center" border="0" cellpadding='0' cellspacing='0'>
                   
                <tr>
                    <td><input type="hidden" name="vpc_MerchantId" value="<%=fields.get("vpc_MerchantId")%>"></td>
                </tr>
                
        <tr>
    <td><input type="hidden" name="EncData" value="<%=encValue%>"></td>                     
                </tr>
                <tr><td colspan="2">&nbsp;</td></tr>

            </table>
            <script language='javascript'>document.RedirectForm.submit();</script>
        </form>
    </body>

         <head>
            <meta http-equiv="cache-control" content="no-cache" />
            <meta http-equiv="pragma" content="no-cache" />
            <meta http-equiv="expires" content="0" />
        </head>
</html>
