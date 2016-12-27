package trash.jforex.examples.strategy.practices;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import com.dukascopy.api.Configurable;
import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IConsole;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.Library;
import com.dukascopy.api.Period;
import com.dukascopy.api.RequiresFullAccess;

@RequiresFullAccess
@Library("C:/temp/mail.jar")
public class TestMail implements IStrategy {

    @Configurable("Recipient mail")
    public String mailAddress = "name.surname@server.com";
    @Configurable("SMTP Server")
    public String smtpServer = "mailserver.company.com";
    private IConsole console;
    
    public void onStart(IContext context) throws JFException {
        this.console = context.getConsole();
        try {
            sendMail("somemail@address.com", mailAddress, "Hello 2", "Test mail \n Bye.");
        } catch (Exception e) {
            console.getErr().println("Failed to send email: " + e);
            e.printStackTrace(console.getErr());
            context.stop();
        }
        console.getOut().println("No exception -> check the e-mail!");
        context.stop();
    }
    
    public boolean sendMail(String from, String to, String subj, String text) throws Exception {
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", smtpServer);
        Session session = Session.getInstance(props);
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipient(RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subj);
        msg.setText(text);
        Transport.send(msg);
        return true;
    }

    public void onAccount(IAccount account) throws JFException {}

    public void onMessage(IMessage message) throws JFException {}

    public void onStop() throws JFException {}

    public void onTick(Instrument instrument, ITick tick) throws JFException {}

    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}

}
