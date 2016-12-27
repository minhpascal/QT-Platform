package trash.jforex.examples.livemode;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dukascopy.api.system.ClientFactory;
import com.dukascopy.api.system.IClient;

/**
 * This small program demonstrates how to initialize Dukascopy client and start
 * a strategy
 */
public class MainPin {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MainPin.class);

	// url of the DEMO jnlp
	private static String jnlpUrl = "https://www.dukascopy.com/client/live/jclient/jforex.jnlp";
	// user name
	private static String userName = "msasc1EU";
	// password
	private static String password = "C1a2r3l4a5";
	
	private static IClient client;

	public static void main(String[] args) throws Exception {
		// get the instance of the IClient interface
		client = ClientFactory.getDefaultInstance();
		
		LOGGER.info("Connecting...");
		// connect to the server using jnlp, user name and password
		client.connect(jnlpUrl, userName, password, PinDialog.showAndGetPin());

		//add here waiting on client.isConnected()
		
		//add here subscription to instruments and strategy launching
	}
	
	@SuppressWarnings("serial")
	private static class PinDialog extends JDialog {
		
		private final JTextField pinfield = new JTextField();
		private final static JFrame noParentFrame = null;
		
		static String showAndGetPin() throws Exception{
			return new PinDialog().pinfield.getText();
		}

		public PinDialog() throws Exception {			
			super(noParentFrame, "PIN Dialog", true);
			
			JPanel captchaPanel = new JPanel();
			captchaPanel.setLayout(new BoxLayout(captchaPanel, BoxLayout.Y_AXIS));
			
			final JLabel captchaImage = new JLabel();
			captchaImage.setIcon(new ImageIcon(client.getCaptchaImage(jnlpUrl)));
			captchaPanel.add(captchaImage);
			
			
			captchaPanel.add(pinfield);
			getContentPane().add(captchaPanel);
			
			JPanel buttonPane = new JPanel();
			
			JButton btnLogin = new JButton("Login");
			buttonPane.add(btnLogin);
			btnLogin.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
					dispose();
				}
			});
			
			JButton btnReload = new JButton("Reload");
			buttonPane.add(btnReload);
			btnReload.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						captchaImage.setIcon(new ImageIcon(client.getCaptchaImage(jnlpUrl)));
					} catch (Exception ex) {
						LOGGER.info(ex.getMessage(), ex);
					}
				}
			});
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			pack();
			setVisible(true);
		}		
	}
}