package test.com.msasc.library.util;

import java.text.MessageFormat;
import java.util.Locale;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Condition;
import com.qtplaf.library.swing.MessageBox;
import com.qtplaf.library.util.TextServer;

public class TestTextServer {

	public static void main(String[] args) {
		Session session = new Session(Locale.UK);
		TextServer.addBaseResource("SysString.xml");
		
		String msg = session.getString("messageStopIrreducibleError");
		MessageBox.warning(session, MessageFormat.format(msg, 0.5, 0.2));
		
		msg = session.getString("messageStopMaxIterations");
		MessageBox.warning(session, MessageFormat.format(msg, 101, 100));
		
		msg = session.getString("exceptionRightOperandExpected");
		MessageBox.warning(session, MessageFormat.format(msg, Condition.Operator.FIELD_EQ));
		System.exit(0);
	}

}
