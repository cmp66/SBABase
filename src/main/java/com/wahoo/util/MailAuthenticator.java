/*
 * MailAuthenticator.java
 *
 * Created on March 9, 2003, 2:09 AM
 */

package com.wahoo.util;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

 /**
 * A mail authenticator for password verification
 *
 * @author  cphillips
 */
public class MailAuthenticator extends Authenticator
{
    public PasswordAuthentication getPasswordAuthentication()
    {
        return new PasswordAuthentication("cphillips", "apba--99");
    }
}
