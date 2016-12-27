/*
 * Copyright (c) 2009 Dukascopy (Suisse) SA. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Dukascopy (Suisse) SA or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. DUKASCOPY (SUISSE) SA ("DUKASCOPY")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL DUKASCOPY OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF DUKASCOPY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */
package trash.jforex.examples.itesterclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.LoadingProgressListener;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.RequiresFullAccess;
import com.dukascopy.api.system.ISystemListener;
import com.dukascopy.api.system.ITesterClient;
import com.dukascopy.api.system.TesterFactory;

import trash.jforex.examples.singlejartest.MA_Play;
import trash.jforex.examples.singlejartest.Main;

/**
 * This small program demonstrates how to initialize Dukascopy tester and start a strategy
 * with a customization of strategy's report
 */
@RequiresFullAccess
public class TesterMainCustomReport {
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	// url of the DEMO jnlp
	private static String jnlpUrl = "https://www.dukascopy.com/client/demo/jclient/jforex.jnlp";
	// user name
	private static String userName = "msasc1EU";
	// password
	private static String password = "C1a2r3l4a5";
	
	public static void insertStringInFile(File inFile, int lineno, String lineToBeInserted) throws Exception {
		// temp file
		File outFile = new File("$$$$$$$$.tmp");

		// input
		FileInputStream fis = new FileInputStream(inFile);
		BufferedReader in = new BufferedReader(new InputStreamReader(fis));

		// output
		FileOutputStream fos = new FileOutputStream(outFile);
		PrintWriter out = new PrintWriter(fos);

		String thisLine = "";
		int i = 1;
		while ((thisLine = in.readLine()) != null) {
			if (i == lineno)
				out.println(lineToBeInserted);
			out.println(thisLine);
			i++;
		}
		out.flush();
		out.close();
		in.close();

		inFile.delete();
		outFile.renameTo(inFile);
	}

	public static void main(String[] args) throws Exception {
		// get the instance of the IClient interface
		final ITesterClient client = TesterFactory.getDefaultInstance();
		// set the listener that will receive system events
		client.setSystemListener(new ISystemListener() {
			@Override
			public void onStart(long processId) {
				LOGGER.info("Strategy started: " + processId);
			}

			@Override
			public void onStop(long processId) {
				LOGGER.info("Strategy stopped: " + processId);
				File reportFile = new File("C:\\temp\\report2.html");
				try {
					client.createReport(processId, reportFile);
					insertStringInFile(reportFile, 90, "<tr><th>__________INSERTED LINE__________</th><td>1.2345</td></tr>");
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
				if (client.getStartedStrategies().size() == 0) {
					System.exit(0);
				}
			}

			@Override
			public void onConnect() {
				LOGGER.info("Connected");
			}

			@Override
			public void onDisconnect() {
				// tester doesn't disconnect
			}
		});

		LOGGER.info("Connecting...");
		// connect to the server using jnlp, user name and password
		// connection is needed for data downloading
		client.connect(jnlpUrl, userName, password);

		// wait for it to connect
		int i = 10; // wait max ten seconds
		while (i > 0 && !client.isConnected()) {
			Thread.sleep(1000);
			i--;
		}
		if (!client.isConnected()) {
			LOGGER.error("Failed to connect Dukascopy servers");
			System.exit(1);
		}

		// custom historical data
		String dateFromStr = "05/25/2011 00:00:00";
		String dateToStr = "05/26/2011 00:00:00";

		final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		Date dateFrom = dateFormat.parse(dateFromStr);
		Date dateTo = dateFormat.parse(dateToStr);

		client.setDataInterval(Period.THIRTY_MINS, OfferSide.BID, ITesterClient.InterpolationMethod.CLOSE_TICK, dateFrom.getTime(),
				dateTo.getTime());

		// set instruments that will be used in testing
		Set<Instrument> instruments = new HashSet<Instrument>();
		instruments.add(Instrument.EURUSD);
		LOGGER.info("Subscribing instruments...");
		client.setSubscribedInstruments(instruments);
		// setting initial deposit
		client.setInitialDeposit(Instrument.EURUSD.getSecondaryCurrency(), 50000);
		// load data
		LOGGER.info("Downloading data");
		Future<?> future = client.downloadData(null);
		// wait for downloading to complete
		future.get();
		// start the strategy
		LOGGER.info("Starting strategy");

		client.startStrategy(new MA_Play(), new LoadingProgressListener() {

			@Override
			public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
				LOGGER.info(information);
			}

			@Override
			public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime) {
			}

			@Override
			public boolean stopJob() {
				return false;
			}
		});
		// now it's running

	}
}
