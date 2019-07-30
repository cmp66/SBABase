package com.wahoo.util;

public class EmailAuthenticator extends javax.mail.Authenticator
{
    private javax.mail.PasswordAuthentication auth;
    
    public javax.mail.PasswordAuthentication getPasswordAuthentication()
    {
        return auth;
    }
    
    public EmailAuthenticator(String user, String password)
    {
        auth = new javax.mail.PasswordAuthentication(user, password);
    }
    
    public EmailAuthenticator(javax.mail.PasswordAuthentication auth)
    {
        this.auth = auth;
    }
}
