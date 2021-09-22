package com.sun.mail.smtp;

/*

*/

import android.util.Base64;

import com.sun.mail.util.MailLogger;

import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.mail.MessagingException;

import eu.faircode.email.Helper;
import eu.faircode.email.Log;

// https://github.com/javaee/javamail/blob/master/mail/src/main/java/com/sun/mail/smtp/SMTPSaslAuthenticator.java
public class SMTPSaslAuthenticator implements SaslAuthenticator {
    private SMTPTransport pr;
    private String name;
    private Properties props;
    private MailLogger logger;
    private String host;

    public SMTPSaslAuthenticator(
            SMTPTransport pr, String name,
            Properties props, MailLogger logger, String host) {
        this.pr = pr;
        this.name = name;
        this.props = props;
        this.logger = logger;
        this.host = host;
    }

    @Override
    public boolean authenticate(
            String[] mechs, final String realm,
            final String authzid, final String u,
            final String p) throws MessagingException {

        if (!pr.supportsAuthentication("CRAM-MD5"))
            throw new UnsupportedOperationException("SASL not supported");

        // https://tools.ietf.org/html/rfc4954
        int resp = simpleCommand(pr, "AUTH CRAM-MD5");
        if (resp == 530) { // Authentication is required
            pr.startTLS();
            resp = simpleCommand(pr, "AUTH CRAM-MD5");
        }

        if (resp == 235) { // Authentication Succeeded
            Log.i("SASL SMTP already authenticated");
            return true;
        }

        if (resp != 334) { // server challenge
            Log.i("SASL SMTP response=" + resp);
            throw new UnsupportedOperationException("SASL not supported");
        }

        try {
            String t = responseText(pr);
            byte[] nonce = Base64.decode(t, Base64.NO_WRAP);
            String hmac = Helper.HMAC("MD5", 64, p.getBytes(), nonce);
            String hash = Base64.encodeToString((u + " " + hmac).getBytes(), Base64.NO_WRAP);
            resp = simpleCommand(pr, hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new MessagingException("CRAM-MD5", ex);
        }

        if (resp != 235) {
            Log.i("SASL SMTP not authenticated response=" + resp);
            throw new UnsupportedOperationException("SASL not authenticated");
        }

        Log.i("SASL SMTP authenticated");
        return true;
    }

    private static int simpleCommand(SMTPTransport pr, String command) throws MessagingException {
        Log.i("SASL SMTP command=" + command);
        int resp = pr.simpleCommand(command);
        Log.i("SASL SMTP response=" + pr.getLastServerResponse());
        return resp;
    }

    private static String responseText(SMTPTransport pr) {
        String resp = pr.getLastServerResponse().trim();
        if (resp.length() > 4)
            return resp.substring(4);
        else
            return "";
    }
}
