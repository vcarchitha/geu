<%@include file="/libs/foundation/global.jsp"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0"%>
<sling:defineObjects />
<%@ page contentType="text/html;charset=ISO-8859-1" language="java" %>
<%@ page import="java.util.List,
                 java.util.ArrayList,
                 java.util.Collections,
                 java.util.Iterator,
                 java.util.Enumeration,
                 java.util.Date,
                 java.security.MessageDigest,
                 java.util.Map,
                 java.net.URLEncoder,
                 java.util.HashMap,
                  java.sql.Connection,
         java.security.*,
         java.security.spec.InvalidKeySpecException,
         javax.crypto.*,
         javax.crypto.spec.SecretKeySpec,
         org.apache.commons.codec.binary.Base64,
         com.geu.aem.web.service.PaymentFormService"%>

<%! // Define Constants
    // ****************
    /* Note:
       ----
       In a proper production environment, only the retrieving of all the input
       parameters and the HTML output would be in this file. The following
       constants and all other methods would be contained in a separate helper
       class so that users could not gain access to these values. */

    // This is secret for encoding the SHA-256 hash
    // This secret will vary from merchant to merchant
    
    static final String SECURE_SECRET = "284AC39C3104D7DBA702B6534A17C65B"; // Add secure secret here

    // This is an array for creating hex chars
    static final char[] HEX_TABLE = new char[] {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

//  ----------------------------------------------------------------------------

   /**
    * This method is for sorting the fields and creating an SHA256 secure hash.
    *
    * @param fields is a map of all the incoming hey-value pairs from the VPC
    * @return is the hash being returned for comparison to the incoming hash
    */
    String hashAllFields(Map fields) {
List fieldNames = new ArrayList(fields.keySet());
        Collections.sort(fieldNames);

     
        StringBuffer buf = new StringBuffer();
        buf.append(SECURE_SECRET);

       
        Iterator itr = fieldNames.iterator();

        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) fields.get(fieldName);
            
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                buf.append(fieldValue);
            }
        }

        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(buf.toString().getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();
            //System.out.println("input data="+buf);
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
    Key generateKey(byte[] keyByte) {
        Key key = new SecretKeySpec (keyByte, "AES");
        return key;
    }

String decrypt(String encryptedData, String strMerchantId) {

            String decryptedValue = null;
            Connection con=null;
            String keySet = "39F6684649F0195E1D77A23AE19AC188";// "CCE8B1D6C85D3B44A73098014E22C53C"; //add encryption key here
      
            try {
            byte[] keyByte = keySet.getBytes();
            Key key = generateKey(keyByte);
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, key);
            byte decryptedByteValue[] = new Base64().decode(encryptedData.getBytes());
            byte decValue[] = c.doFinal(decryptedByteValue);
            decryptedValue = new String(decValue);
       
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            finally {
                return decryptedValue;
            }
        }

//  ----------------------------------------------------------------------------

    /*
    * This method takes a byte array and returns a string of its contents
    *
    * @param input - byte array containing the input data
    * @return String containing the output String
    */
    static String hex(byte[] input) {
        // create a StringBuffer 2x the size of the hash array
        StringBuffer sb = new StringBuffer(input.length * 2);

        // retrieve the byte array data, convert it to hex
        // and add it to the StringBuffer
        for (int i = 0; i < input.length; i++) {
            sb.append(HEX_TABLE[(input[i] >> 4) & 0xf]);
            sb.append(HEX_TABLE[input[i] & 0xf]);
        }
        return sb.toString();
    }

//  ----------------------------------------------------------------------------

   /*
    * This method takes a data String and returns a predefined value if empty
    * If data Sting is null, returns string "No Value Returned", else returns input
    *
    * @param in String containing the data String
    * @return String containing the output String
    */
    private static String null2unknown(String in) {
        if (in == null || in.length() == 0) {
            return "No Value Returned";
        } else {
            return in;
        }
    } // null2unknown()

//  ----------------------------------------------------------------------------

   /*
    * This function uses the returned status code retrieved from the Digital
    * Response and returns an appropriate description for the code
    *
    * @param vResponseCode String containing the vpc_TxnResponseCode
    * @return description String containing the appropriate description
    */
    String getResponseDescription(String vResponseCode) {

        String result = "";

        // check if a single digit response code
        if (vResponseCode.length() != 1) {

            // Java cannot switch on a string so turn everything to a char
            char input = vResponseCode.charAt(0);

            switch (input){
                case '0' : result = "Transaction Successful"; break;
                case '1' : result = "Unknown Error"; break;
                case '2' : result = "Bank Declined Transaction"; break;
                case '3' : result = "No Reply from Bank"; break;
                case '4' : result = "Expired Card"; break;
                case '5' : result = "Insufficient Funds"; break;
                case '6' : result = "Error Communicating with Bank"; break;
                case '7' : result = "Payment Server System Error"; break;
                case '8' : result = "Transaction Type Not Supported"; break;
                case '9' : result = "Bank declined transaction (Do not contact Bank)"; break;
                case 'A' : result = "Transaction Aborted"; break;
                case 'C' : result = "Transaction Cancelled"; break;
                case 'D' : result = "Deferred transaction has been received and is awaiting processing"; break;
                case 'F' : result = "3D Secure Authentication failed"; break;
                case 'I' : result = "Card Security Code verification failed"; break;
                case 'L' : result = "Shopping Transaction Locked (Please try the transaction again later)"; break;
                case 'N' : result = "Cardholder is not enrolled in Authentication Scheme"; break;
                case 'P' : result = "Transaction has been received by the Payment Adaptor and is being processed"; break;
                case 'R' : result = "Transaction was not processed - Reached limit of retry attempts allowed"; break;
                case 'S' : result = "Duplicate SessionID (OrderInfo)"; break;
                case 'T' : result = "Address Verification Failed"; break;
                case 'U' : result = "Card Security Code Failed"; break;
                case 'V' : result = "Address Verification and Card Security Code Failed"; break;
                case '?' : result = "Transaction status is unknown"; break;
                default  : result = "Unable to be determined";
            }

            return result;
        } else {
            return "No Value Returned";
        }
    } // getResponseDescription()

//  ----------------------------------------------------------------------------

   /**
    * This function uses the QSI AVS Result Code retrieved from the Digital
    * Receipt and returns an appropriate description for this code.
    *
    * @param vAVSResultCode String containing the vpc_AVSResultCode
    * @return description String containing the appropriate description
    */
    private String displayAVSResponse(String vAVSResultCode) {

        String result = "";
        if (vAVSResultCode != null || vAVSResultCode.length() == 0) {

            if (vAVSResultCode.equalsIgnoreCase("Unsupported") || vAVSResultCode.equalsIgnoreCase("No Value Returned")) {
                result = "AVS not supported or there was no AVS data provided";
            } else {
                // Java cannot switch on a string so turn everything to a char
                char input = vAVSResultCode.charAt(0);

                switch (input){
                    case 'X' : result = "Exact match - address and 9 digit ZIP/postal code"; break;
                    case 'Y' : result = "Exact match - address and 5 digit ZIP/postal code"; break;
                    case 'S' : result = "Service not supported or address not verified (international transaction)"; break;
                    case 'G' : result = "Issuer does not participate in AVS (international transaction)"; break;
                    case 'A' : result = "Address match only"; break;
                    case 'W' : result = "9 digit ZIP/postal code matched, Address not Matched"; break;
                    case 'Z' : result = "5 digit ZIP/postal code matched, Address not Matched"; break;
                    case 'R' : result = "Issuer system is unavailable"; break;
                    case 'U' : result = "Address unavailable or not verified"; break;
                    case 'E' : result = "Address and ZIP/postal code not provided"; break;
                    case 'N' : result = "Address and ZIP/postal code not matched"; break;
                    case '0' : result = "AVS not requested"; break;
                    default  : result = "Unable to be determined";
                }
            }
        } else {
            result = "null response";
        }
        return result;
    }

//  ----------------------------------------------------------------------------

   /**
    * This function uses the QSI CSC Result Code retrieved from the Digital
    * Receipt and returns an appropriate description for this code.
    *
    * @param vCSCResultCode String containing the vpc_CSCResultCode
    * @return description String containing the appropriate description
    */
    private String displayCSCResponse(String vCSCResultCode) {

        String result = "";
        if (vCSCResultCode != null || vCSCResultCode.length() == 0) {

            if (vCSCResultCode.equalsIgnoreCase("Unsupported")  || vCSCResultCode.equalsIgnoreCase("No Value Returned")) {
                result = "CSC not supported or there was no CSC data provided";
            } else {
                // Java cannot switch on a string so turn everything to a char
                char input = vCSCResultCode.charAt(0);

                switch (input){
                    case 'M' : result = "Exact code match"; break;
                    case 'S' : result = "Merchant has indicated that CSC is not present on the card (MOTO situation)"; break;
                    case 'P' : result = "Code not processed"; break;
                    case 'U' : result = "Card issuer is not registered and/or certified"; break;
                    case 'N' : result = "Code invalid or not matched"; break;
                    default  : result = "Unable to be determined";
                }
            }

        } else {
            result = "null response";
        }
        return result;
    }

//  ----------------------------------------------------------------------------

   /**
    * This method uses the 3DS verStatus retrieved from the
    * Response and returns an appropriate description for this code.
    *
    * @param vpc_VerStatus String containing the status code
    * @return description String containing the appropriate description
    */
    private String getStatusDescription(String vStatus) {

        String result = "";
        if (vStatus != null && !vStatus.equals("")) {

            if (vStatus.equalsIgnoreCase("Unsupported")  || vStatus.equals("No Value Returned")) {
                result = "3DS not supported or there was no 3DS data provided";
            } else {

                // Java cannot switch on a string so turn everything to a character
                char input = vStatus.charAt(0);

                switch (input){
                    case 'Y'  : result = "The cardholder was successfully authenticated."; break;
                    case 'E'  : result = "The cardholder is not enrolled."; break;
                    case 'N'  : result = "The cardholder was not verified."; break;
                    case 'U'  : result = "The cardholder's Issuer was unable to authenticate due to some system error at the Issuer."; break;
                    case 'F'  : result = "There was an error in the format of the request from the merchant."; break;
                    case 'A'  : result = "Authentication of your Merchant ID and Password to the ACS Directory Failed."; break;
                    case 'D'  : result = "Error communicating with the Directory Server."; break;
                    case 'C'  : result = "The card type is not supported for authentication."; break;
                    case 'S'  : result = "The signature on the response received from the Issuer could not be validated."; break;
                    case 'P'  : result = "Error parsing input from Issuer."; break;
                    case 'I'  : result = "Internal Payment Server system error."; break;
                    default   : result = "Unable to be determined"; break;
                }
            }
        } else {
            result = "null response";
        }
        return result;
    }

//  ----------------------------------------------------------------------------
%><%

    // *******************************************
    // START OF MAIN PROGRAM
    // *******************************************

    // The Page does a display to a browser

    // retrieve all the incoming parameters into a hash map
    Map fields = new HashMap();
    for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
        String fieldName = (String) e.nextElement();
        String fieldValue = request.getParameter(fieldName);
  String merchantId= request.getParameter("MerchantId").toString();
   String encReq = request.getParameter("EncDataResp").toString();
        Map<String, String> encMap = new HashMap<String, String>(); 
            String encData = decrypt(encReq, merchantId);
            
        String pipeSplit[] = encData.toString().split("::");
                        for (int i = 0; i < pipeSplit.length; i++) {
                            String pareValues[] = pipeSplit[i].split("\\|\\|");
                            if (pareValues[1] != null && pareValues[1].toString().length() > 0 && 
                                    !pareValues[1].toString().equalsIgnoreCase("null")) {
                                encMap.put(pareValues[0], pareValues[1]);
                            }
                        }
 fields = encMap;
        
    }

/*  If there has been a merchant secret set then sort and loop through all the
    data in the Virtual Payment Client response. while we have the data, we can
    append all the fields that contain values (except the secure hash) so that
    we can create a hash and validate it against the secure hash in the Virtual
    Payment Client response.

    NOTE: If the vpc_TxnResponseCode in not a single character then
    there was a Virtual Payment Client error and we cannot accurately validate
    the incoming data from the secure hash. */

    // remove the vpc_TxnResponseCode code from the response fields as we do not
    // want to include this field in the hash calculation
    String vpc_Txn_Secure_Hash = null2unknown((String) fields.remove("vpc_SecureHash"));
    String hashValidated = null;

    // defines if error message should be output
    boolean errorExists = false;

    if (SECURE_SECRET != null && SECURE_SECRET.length() > 0 &&
        (fields.get("vpc_TxnResponseCode") != null || fields.get("vpc_TxnResponseCode") != "No Value Returned")) {

        // create secure hash and append it to the hash map if it was created
        // remember if SECURE_SECRET = "" it wil not be created
        String secureHash = hashAllFields(fields);

        // Validate the Secure Hash (remember SHA-256 hashes are not case sensitive)
        if (vpc_Txn_Secure_Hash.equalsIgnoreCase(secureHash)) {
            // Secure Hash validation succeeded, add a data field to be
            // displayed later.
            hashValidated = "<font color='#00AA00'><strong>CORRECT</strong></font>";
        } else {
            // Secure Hash validation failed, add a data field to be
            // displayed later.
            errorExists = true;
            hashValidated = "<font color='#FF0066'><strong>INVALID HASH</strong></font>";
        }
    } else {
        // Secure Hash was not validated,
        hashValidated = "<font color='orange'><strong>Not Calculated - No 'SECURE_SECRET' present.</strong></font>";
    }

    // Extract the available receipt fields from the VPC Response
    // If not present then let the value be equal to 'Unknown'
    // Standard Receipt Data
    String amount          = null2unknown((String)fields.get("vpc_Amount"));
    String locale          = null2unknown((String)fields.get("vpc_Locale"));
    String batchNo         = null2unknown((String)fields.get("vpc_BatchNo"));
    String command         = null2unknown((String)fields.get("vpc_Command"));
    String message         = null2unknown((String)fields.get("vpc_Message"));
    String version         = null2unknown((String)fields.get("vpc_Version"));
    String cardType        = null2unknown((String)fields.get("vpc_Card"));
    String orderInfo       = null2unknown((String)fields.get("vpc_OrderInfo"));
    String receiptNo       = null2unknown((String)fields.get("vpc_ReceiptNo"));
    String merchantID      = null2unknown((String)fields.get("vpc_Merchant"));
    String merchTxnRef     = null2unknown((String)fields.get("vpc_MerchTxnRef"));
    String authorizeID     = null2unknown((String)fields.get("vpc_AuthorizeId"));
    String transactionNo   = null2unknown((String)fields.get("vpc_TransactionNo"));
    String acqResponseCode = null2unknown((String)fields.get("vpc_AcqResponseCode"));
    String txnResponseCode = null2unknown((String)fields.get("vpc_TxnResponseCode"));

    // CSC Receipt Data
    String vCSCResultCode  = null2unknown((String)fields.get("vpc_CSCResultCode"));
    String vCSCRequestCode = null2unknown((String)fields.get("vpc_CSCRequestCode"));
    String vACQCSCRespCode = null2unknown((String)fields.get("vpc_AcqCSCRespCode"));

    // 3-D Secure Data
    String transType3DS       = null2unknown((String)fields.get("vpc_VerType"));
    String verStatus3DS       = null2unknown((String)fields.get("vpc_VerStatus"));
    String token3DS           = null2unknown((String)fields.get("vpc_VerToken"));
    String secureLevel3DS  = null2unknown((String)fields.get("vpc_VerSecurityLevel"));
    String enrolled3DS       = null2unknown((String)fields.get("vpc_3DSenrolled"));
    String xid3DS           = null2unknown((String)fields.get("vpc_3DSXID"));
    String eci3DS           = null2unknown((String)fields.get("vpc_3DSECI"));
    String status3DS       = null2unknown((String)fields.get("vpc_3DSstatus"));

    String error = "";
    // Show this page as an error page if error condition
    if (txnResponseCode.equals("7") || txnResponseCode.equals("No Value Returned") || errorExists) {
        error = "Error ";
    }

    // FINISH TRANSACTION - Process the VPC Response Data
    // =====================================================
    // For the purposes of demonstration, we simply display the Result fields on a
    // web page.

    response.setHeader("Content-Type","text/html; charset=ISO-8859-1");
    response.setHeader("Expires","Mon, 26 Jul 1997 05:00:00 GMT");
    response.setDateHeader("Last-Modified", new Date().getTime());
    response.setHeader("Cache-Control","no-store, no-cache, must-revalidate");
    response.setHeader("Pragma","no-cache");
    String transactionAmount = amount.substring(0, amount.length() - 2);

%>  <!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN'>
    <HTML>
    <HEAD><TITLE>Axis PG Response <%=error%>Page</TITLE>
        <meta http-equiv='Content-Type' content='text/html; charset=ISO-8859-1'>
        <meta http-equiv="cache-control" content="no-cache" />
        <meta http-equiv="pragma" content="no-cache" />
        <meta http-equiv="expires" content="0" />
        <STYLE type='text/css'>
            <!--
            H1       { font-family:Arial,sans-serif; font-size:24pt; color:#08185A; font-weight:100}
            H2.co    { font-family:Arial,sans-serif; font-size:24pt; color:#08185A; margin-top:0.1em; margin-bottom:0.1em; font-weight:100}
            H3.co    { font-family:Arial,sans-serif; font-size:16pt; color:#000000; margin-top:0.1em; margin-bottom:0.1em; font-weight:100}
            body     { font-family:Verdana,Arial,sans-serif; font-size:10pt; color:#08185A background-color:#FFFFFF }
            P        { font-family:Verdana,Arial,sans-serif; font-size:8pt; color:#FFFFFF }
            A:link   { font-family:Verdana,Arial,sans-serif; font-size:8pt; color:#08185A }
            A:visited{ font-family:Verdana,Arial,sans-serif; font-size:8pt; color:#08185A }
            A:hover  { font-family:Verdana,Arial,sans-serif; font-size:8pt; color:#FF0000 }
            A:active { font-family:Verdana,Arial,sans-serif; font-size:8pt; color:#FF0000 }
            TD       { font-family:Verdana,Arial,sans-serif; font-size:8pt; color:#08185A }
            TD.red   { font-family:Verdana,Arial,sans-serif; font-size:8pt; color:#FF0066 }
            TD.green { font-family:Verdana,Arial,sans-serif; font-size:8pt; color:#00AA00 }
            TH       { font-family:Verdana,Arial,sans-serif; font-size:10pt; color:#08185A; font-weight:bold; background-color:#E1E1E1; padding-top:0.5em; padding-bottom:0.5em}
            input    { font-family:Verdana,Arial,sans-serif; font-size:8pt; color:#08185A; background-color:#E1E1E1; font-weight:bold }
            select   { font-family:Verdana,Arial,sans-serif; font-size:8pt; color:#08185A; background-color:#E1E1E1; font-weight:bold; width:463 }
            textarea { font-family:Verdana,Arial,sans-serif; font-size:8pt; color:#08185A; background-color:#E1E1E1; font-weight:normal; scrollbar-arrow-color:#08185A; scrollbar-base-color:#E1E1E1 }
            -->
        </STYLE>
    </HEAD>
    <BODY>

    <TABLE width="85%" align='center' cellpadding='5' border='0'>

        <tr bgcolor="#C1C1C1">
            <td colspan="2" height="25"><p><strong>&nbsp;Standard Transaction Fields</strong></p></td>
        </tr>
        <tr>
            <td align='right' width='50%'><strong><i>VPC API Version: </i></strong></td>
            <td width='50%'><%=version%></td>
        </tr>
        <tr bgcolor='#E1E1E1'>
            <td align='right'><strong><i>Command: </i></strong></td>
            <td><%=command%></td>
        </tr>
        <tr>
            <td align='right'><strong><i>Merchant Transaction Reference: </i></strong></td>
            <td><%=merchTxnRef%></td>
        </tr>
        <tr bgcolor='#E1E1E1'>
            <td align='right'><strong><i>Merchant ID: </i></strong></td>
            <td><%=merchantID%></td>
        </tr>
        <tr>
            <td align='right'><strong><i>Order Information: </i></strong></td>
            <td><%=orderInfo%></td>
        </tr>
        <tr bgcolor='#E1E1E1'>
            <td align='right'><strong><i>Transaction Amount: </i></strong></td>
            <td><%=transactionAmount%></td>
        </tr>
        <tr>
            <td align='right'><strong><i>Locale: </i></strong></td>
            <td><%=locale%></td>
        </tr>

        <tr>
            <td colspan='2' align='center'><font color='#C1C1C1'>Fields above are the request values returned.<br></font><HR>
            </td>
        </tr>

        <tr bgcolor='#E1E1E1'>
            <td align='right'><strong><i>VPC Transaction Response Code: </i></strong></td>
            <td><%=txnResponseCode%></td>
        </tr>
        <tr>
            <td align='right'><strong><i>Transaction Response Code Description: </i></strong></td>
            <td><%=getResponseDescription(txnResponseCode)%></td>
        </tr>
        <tr bgcolor='#E1E1E1'>
            <td align='right'><strong><i>Message: </i></strong></td>
            <td><%=message%></td>
        </tr>
<%
// only display the following fields if not an error condition
if (!txnResponseCode.equals("7") && !txnResponseCode.equals("No Value Returned")) {
    PaymentFormService paymentFormService = sling.getService(PaymentFormService.class);
    if (txnResponseCode.equals("0")) {
        paymentFormService.savePaymentStatus(slingRequest, amount, merchTxnRef, "Successful", "GEU");
        slingResponse.sendRedirect("/content/geu/en/admission-aid/payment/thank-you.html");
    } else {
        paymentFormService.savePaymentStatus(slingRequest, amount, merchTxnRef, getResponseDescription(txnResponseCode), "GEU");
        slingResponse.sendRedirect("/content/geu/en/admission-aid/payment/payment-failed.html");
    }
    
%>
        <tr>
            <td align='right'><strong><i>Receipt Number: </i></strong></td>
            <td><%=receiptNo%></td>
        </tr>
        <tr bgcolor='#E1E1E1'>
            <td align='right'><strong><i>Transaction Number: </i></strong></td>
            <td><%=transactionNo%></td>
        </tr>
        <tr>
            <td align='right'><strong><i>Acquirer Response Code: </i></strong></td>
            <td><%=acqResponseCode%></td>
        </tr>
        <tr bgcolor='#E1E1E1'>
            <td align='right'><strong><i>Bank Authorization ID: </i></strong></td>
            <td><%=authorizeID%></td>
        </tr>
        <tr>
            <td align='right'><strong><i>Batch Number: </i></strong></td>
            <td><%=batchNo%></td>
        </tr>
        <tr bgcolor='#E1E1E1'>
            <td align='right'><strong><i>Card Type: </i></strong></td>
            <td><%=cardType%></td>
        </tr>
<%
}%></TABLE><br>

    <CENTER><P><A HREF='/content/geu/en.html'>Go To Home Page</A></P></CENTER>

    </BODY>
    <head>
        <meta http-equiv="cache-control" content="no-cache" />
        <meta http-equiv="pragma" content="no-cache" />
        <meta http-equiv="expires" content="0" />
    </head>
    </HTML>

