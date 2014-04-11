/*
 * Email.java
 *
 * Created on March 9, 2003, 1:42 AM
 */

package com.wahoo.util;

import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

 /**
 * This class implements emails. Separate static methods are provided for sending emails
 * with plain text and those with mail attachments.
 *
 * * @author  cphillips
 */
public class Email
{
	private static final String EMAIL_SEP_CHARS = ",;:";
    
    /** Creates a new instance of Email */
    public Email ()
    {
    }


	/**
	* Sends an email to the specified address/addresses.
	* @param inFrom the sender of the mail
	* @param inTo the recipient(s) of the email
	* @param inSubject the subject of the email
	* @param inBody the body of the email
	*/
	@SuppressWarnings("static-access")
	public static void sendMail(String inFrom, String inTo, String inSubject, String inBody)
				throws Exception
	{
		String wkFromAddr = inFrom;
		//String wkToAddr = inTo;

		Vector<String> wkAddressVector = parseAddressList(inTo);
		if(wkAddressVector.size() > 0)
		{
			InternetAddress wkEmailRecipients[] = createEmailRecipientList(wkAddressVector);
			StringBuffer wkStr = new StringBuffer(80);
			for(int i = 0; i < wkEmailRecipients.length; i++)
			{
				wkStr.append("<").append(wkEmailRecipients[i]).append("> ");
			}

			Properties	wkProps = new Properties();
			wkProps.put("mail.smtp.host", "smtp.gmail.com");
			wkProps.put("mail.smtp.port", "465");
			wkProps.put("mail.smtp.auth", "true");
			wkProps.put("mail.smtp.socketFactory.port", "465");
			wkProps.put("mail.smtp.socketFactory.class",
					"javax.net.ssl.SSLSocketFactory");
            //wkProps.put("mail.user", "cphillips");
            //wkProps.put("mail.password", "buckeye");

			Session wkSession = Session.getInstance(wkProps, new EmailAuthenticator("cmp1166@gmail.com", "rulg qtrl gjta kfdm"));
			// create a message
			MimeMessage wkMsg = new MimeMessage(wkSession);
			wkMsg.setFrom(new InternetAddress(wkFromAddr));
			//InternetAddress wkToAddress = new InternetAddress(wkToAddr);
			wkMsg.setRecipients(Message.RecipientType.TO, wkEmailRecipients);
			wkMsg.setSubject(inSubject);
			wkMsg.setDataHandler(new DataHandler(new
			        ByteArrayDataSource(inBody, "text/plain")));
			//Transport.send(message);

	    	//wkMsg.setText(inBody);
			// set the Date: header
			wkMsg.setSentDate(new Date());
			// send the message
			wkSession.getTransport("smtp").send(wkMsg);
			//Transport.send(wkMsg);
			System.out.println("Sent email");
		}
		else
		{
			System.out.println("Email.sendMail(): No addresses to send email to!");
		}
	}


	/**
	* Sends an email with an attachment consisting of multiple files to the specified address/addresses.
	* @param inFrom the sender of the mail
	* @param inTo the recipient(s) of the email
	* @param inSubject the subject of the email
	* @param inBody the body of the email
	* @param inFileNames the vector containing file names of the files to be attached to the email
	*/
	@SuppressWarnings("unchecked")
	public static void sendMailWithAttachment(String inFrom, String inTo, String inSubject, 
			String inBody, Vector<String> inFileNames)
				throws Exception
	{
		String wkFromAddr = inFrom;
		//String wkToAddr = inTo;
		Vector wkAddressVector = parseAddressList(inTo);

		if(wkAddressVector.size() > 0)
		{
			InternetAddress wkEmailRecipients[] = createEmailRecipientList(wkAddressVector);
			StringBuffer wkStr = new StringBuffer(80);
			for(int i = 0; i < wkEmailRecipients.length; i++)
			{
				wkStr.append("<").append(wkEmailRecipients[i]).append("> ");
			}


			Properties	wkProps = new Properties();
			wkProps.put("mail.smtp.host", "mail.wahoosoftware.com");
			wkProps.put("mail.smtp.auth", "true");

			// create and fill the first message part
			MimeBodyPart wkMbp1 = new MimeBodyPart();
			wkMbp1.setText(inBody);

			// create the Multipart and its parts to it
			Multipart wkMp = new MimeMultipart();
			wkMp.addBodyPart(wkMbp1);

			for(int i = 0; i < inFileNames.size(); i++)
			{
				String wkFileName = (String) inFileNames.get(i);

				// create the second message part for the attachment
				MimeBodyPart wkMbp2 = new MimeBodyPart();

				// attach the file to the message
				FileDataSource wkFds = new FileDataSource(wkFileName);
				wkMbp2.setDataHandler(new DataHandler(wkFds));
				wkMbp2.setFileName(wkFds.getName());

				wkMp.addBodyPart(wkMbp2);
			}

			Session wkSession = Session.getInstance(wkProps, new EmailAuthenticator("cphillips@wahoosoftware.com", "apba--99"));

			// create a message
			MimeMessage wkMsg = new MimeMessage(wkSession);
			wkMsg.setFrom(new InternetAddress(wkFromAddr));
			wkMsg.setRecipients(Message.RecipientType.TO, wkEmailRecipients);
			wkMsg.setSubject(inSubject);

			// add the Multipart to the message
			wkMsg.setContent(wkMp);

			// set the Date: header
			wkMsg.setSentDate(new Date());

			// send the message
			Transport.send(wkMsg);
			System.out.println("Sent email with attachment");
		}
		else
		{
			System.out.println("Email.sendMailWithAttachment(): No addresses to send email to!");
		}
	}

	/**
	* Creates an array of InternetAddress objects given a Vector containing
	* email addresses as strings.
	*/
	private static InternetAddress[] createEmailRecipientList(Vector<String> inAddrVector)
	{
		InternetAddress wkEmailRecipients[] = new InternetAddress[inAddrVector.size()];
		// create a list of internet addresses
		int wkGoodAddresses = 0;
		for(int i = 0; i < inAddrVector.size(); i++)
		{
			// if there is an illegal email address we try to send to the rest at least
			try
			{
				String wkStr = inAddrVector.get(i);
				wkEmailRecipients[i] = new InternetAddress(wkStr);
				wkGoodAddresses++;
			}
			catch(AddressException ae)
			{
				wkEmailRecipients[i] = null;
				ae.printStackTrace();
			}
		}
		if(wkGoodAddresses < inAddrVector.size())
		{
			InternetAddress wkGoodEmailAddresses[] = new InternetAddress[wkGoodAddresses];
			for(int i = 0, j = 0; i < inAddrVector.size(); i++)
			{
				if(wkEmailRecipients[i] != null)
				{
					wkGoodEmailAddresses[j] = wkEmailRecipients[i];
					j++;
				}
			}
			return wkGoodEmailAddresses;
		}
		return wkEmailRecipients;
	}

	/**
	* Parses a string containing a list of email addresses into separate
	* email addresses and returns the list as a vector. A "," or ":" or ";"
	* can be used to separate addresses in the string
	*/
	private static Vector<String> parseAddressList(String inStr)
	{
		Vector<String> wkAddressVect = new Vector<String>();

		StringTokenizer wkStringTokenizer = new StringTokenizer(inStr, EMAIL_SEP_CHARS);

		while(wkStringTokenizer.hasMoreTokens())
		{
			String wkTempStr = wkStringTokenizer.nextToken();
			String wkAddrStr = removeWhiteSpace(wkTempStr.trim());
			wkAddressVect.add(wkAddrStr);
			//ServerGlobal.LOG.info.putLog("Email.parseAddressList(): Address = <" + lclAddrStr + ">");
		}
		return wkAddressVect;
	}

	/**
	* Removes white space from an email address by putting quotes "" around
	* the prefix portion of the email address if it contains any whitespace
	*/
	private static String removeWhiteSpace(String inStr)
	{
		String wkRetStr = inStr;

		StringTokenizer wkST1 = new StringTokenizer(inStr, "@");
		// there can be only two parts separated by the "@" sign
		if(wkST1.countTokens() != 2)
		{
			return wkRetStr;
		}

		int wkPos = inStr.indexOf("@");
		// separate the email address into a prefix and suffix
		String wkPrefix = inStr.substring(0, wkPos);
		wkPrefix = wkPrefix.trim();
		String wkSuffix = inStr.substring(wkPos, inStr.length());
		wkSuffix = wkSuffix.trim();

		// check for white space in the prefix only. assume that there won't be whitespace in the suffix
		StringTokenizer wkST2 = new StringTokenizer(wkPrefix);	// use the default set of delimiters
		// if there is whitespace in the prefix of the email address
		if(wkST2.countTokens() > 1)
		{
			wkRetStr = "\"" + wkPrefix + "\"" + wkSuffix;
		}

		return wkRetStr;
	}

	@SuppressWarnings("unchecked")
    public static boolean isValidEmailAddressList(String inAddressList)
    {
    	// convert delimitted email list to a vector
 		
 		Vector wkAddressVector = parseAddressList(inAddressList);
		if (wkAddressVector.size() <= 0)
		{
			return false;
		}
    	
    	// check each email address in the vector
		for(int i = 0; i < wkAddressVector.size(); i++)
		{
			String wkStr = (String) wkAddressVector.get(i);			    	
	        try
	        {
	        	@SuppressWarnings("unused")
	            InternetAddress wkInetAddress = new InternetAddress(wkStr);
	        	wkInetAddress.validate();
	        }
	        catch (AddressException wkEx)
	        {
	            return false;
	        }
	    }
	    
        return true;  // all addresses checked out Ok.
    }
    
    public static void emailException(Exception e)
    {
        try
        {
            //sendMail("cphillips@wahoosoftware.com", "cphillips@wahoosoftware.com", "Exception", ExceptionFormatter.getStackTrace(e));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        
    }
    
    public static void emailException(SQLException e)
    {
        try
        {
            //sendMail("cphillips@wahoosoftware.com", "cphillips@wahoosoftware.com", "Exception", ExceptionFormatter.getStackTrace(e));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        
    }
    
    public static void emailExceptionWithAttachments(Exception e, Vector<String> filenames)
    {
        try
        {
            //sendMailWithAttachment("cphillips@wahoosoftware.com", "cphillips@wahoosoftware.com", "Exception", ExceptionFormatter.getStackTrace(e),  filenames);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        
    }
        
}
