/**
 * 
 */
package trash.jforex.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dukascopy.api.system.ITesterClient;
import com.dukascopy.api.system.TesterFactory;

/**
 * A class to manage connections demo tester to Dukascopy servers.
 * 
 * @author Miquel Sas
 */
public class ConnectorDemoTesterClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorDemoTesterClient.class);

	/**
	 * URL (demo by default)
	 */
	private String url = "https://www.dukascopy.com/client/demo/jclient/jforex.jnlp";
	/**
	 * User name
	 */
	private String userName = "msasc1EU";
	/**
	 * Password
	 */
	private String password = "C1a2r3l4a5";
	/**
	 * The ITesterClient instance.
	 */
	private ITesterClient testerClient;

	/**
	 * Default constructor.
	 */
	public ConnectorDemoTesterClient() {
		super();
	}

	/**
	 * Return the URL.
	 * 
	 * @return The url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Set the url.
	 * 
	 * @param url The url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Return the user name.
	 * 
	 * @return The user name
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Set the user name.
	 * 
	 * @param userName The user name
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Return the password.
	 * 
	 * @return The password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Set the password.
	 * 
	 * @param password The password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Connect to the tester client environment.
	 * 
	 * @return A boolean indicating if the connection has been successful.
	 * @throws Exception
	 */
	public boolean connect() throws Exception {
		if (testerClient != null) {
			if (testerClient.isConnected()) {
				LOGGER.warn("Already connected to tester client");
				return true;
			}
		}
		testerClient = TesterFactory.getDefaultInstance();
		testerClient.setSystemListener(new SystemListener(true));

		LOGGER.info("Connecting to tester client...");
		testerClient.connect(url, userName, password);

		// wait for it to connect
		int i = 10; // wait max ten seconds
		while (i > 0 && !testerClient.isConnected()) {
			LOGGER.info("i=" + i);
			Thread.sleep(1000);
			i--;
		}
		if (!testerClient.isConnected()) {
			LOGGER.error("Failed to connect Dukascopy servers");
			return false;
		}

		return true;
	}

	/**
	 * Returns the ITesterClient interface. This method should be used after connecting to the tester client
	 * environment.
	 * 
	 * @return The IClient interface.
	 */
	public ITesterClient getTesterClient() {
		return testerClient;
	}

}
