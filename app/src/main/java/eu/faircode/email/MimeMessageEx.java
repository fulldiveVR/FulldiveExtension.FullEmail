package eu.faircode.email;

/*

*/

import java.io.InputStream;

import javax.mail.Flags;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class MimeMessageEx extends MimeMessage {
    private String msgid;
    private MimeMessage original;

    MimeMessageEx(Session session, String msgid) {
        super(session);
        this.msgid = msgid;
    }

    MimeMessageEx(Session session, InputStream is, String msgid) throws MessagingException {
        super(session, is);
        this.msgid = msgid;
    }

    MimeMessageEx(Session session, InputStream is, MimeMessage original) throws MessagingException {
        super(session, is);
        this.original = original;
    }

    @Override
    public String getMessageID() throws MessagingException {
        if (this.msgid == null)
            return super.getMessageID();
        else
            return this.msgid;
    }

    @Override
    protected void updateMessageID() throws MessagingException {
        if (this.msgid == null)
            super.updateMessageID();
        else {
            setHeader("Message-ID", msgid);
            Log.i("Override Message-ID=" + msgid);
        }
    }

    @Override
    public synchronized Flags getFlags() throws MessagingException {
        if (original == null)
            return super.getFlags();
        else
            return original.getFlags();
    }

    @Override
    public synchronized boolean isSet(Flags.Flag flag) throws MessagingException {
        if (original == null)
            return super.isSet(flag);
        else
            return original.isSet(flag);
    }

    @Override
    public void setFlag(Flags.Flag flag, boolean set) throws MessagingException {
        if (original == null)
            super.setFlag(flag, set);
        else
            original.setFlag(flag, set);
    }

    @Override
    public synchronized void setFlags(Flags flag, boolean set) throws MessagingException {
        if (original == null)
            super.setFlags(flag, set);
        else
            original.setFlags(flag, set);
    }
}
