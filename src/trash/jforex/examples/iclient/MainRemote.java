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
package trash.jforex.examples.iclient;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.strategy.IStrategyManager;
import com.dukascopy.api.strategy.IStrategyResponse;
import com.dukascopy.api.strategy.IStrategyDescriptor;
import com.dukascopy.api.strategy.StrategyListener;
import com.dukascopy.api.strategy.remote.IRemoteStrategyDescriptor;
import com.dukascopy.api.strategy.remote.IRemoteStrategyManager;
import com.dukascopy.api.strategy.remote.RemoteStrategyListener;
import com.dukascopy.api.system.ClientFactory;
import com.dukascopy.api.system.IClient;
import com.dukascopy.api.system.ISystemListener;

/**
 * This small program demonstrates how to initialize Dukascopy client and start
 * a strategy
 */
public class MainRemote {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainRemote.class);
	private static String jnlpUrl = "https://www.dukascopy.com/client/demo/jclient/jforex.jnlp";
	private static String userName = "mzd";
	private static String password = "mzd";

	private static UUID myStrategyId;

	public static void main(String[] args) throws Exception {
		final IClient client = ClientFactory.getDefaultInstance();
		client.setSystemListener(new ISystemListener() {
			private int lightReconnects = 3;

			@Override
			public void onStart(long processId) {
				LOGGER.info("Strategy started: " + processId);
			}

			@Override
			public void onStop(long processId) {
				LOGGER.info("Strategy stopped: " + processId);
				if (client.getStartedStrategies().size() == 0) {
					System.exit(0);
				}
			}

			@Override
			public void onConnect() {
				LOGGER.info("Connected");
				lightReconnects = 3;
			}

			@Override
			public void onDisconnect() {
				LOGGER.warn("Disconnected");
				if (lightReconnects > 0) {
					LOGGER.error("TRY TO RECONNECT, reconnects left: " + lightReconnects);
					client.reconnect();
					--lightReconnects;
				} else {
					try {
						// sleep for 10 seconds before attempting to reconnect
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// ignore
					}
					try {
						client.connect(jnlpUrl, userName, password);
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
			}
		});

		LOGGER.info("Connecting...");
		// connect to the server using jnlp, user name and password
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

		Set<Instrument> instruments = new HashSet<Instrument>();
		instruments.add(Instrument.EURUSD);
		LOGGER.info("Subscribing instruments...");
		client.setSubscribedInstruments(instruments);

		File jfxFile = new File("src/singlejartest/MA_Play.jfx");
		if (!jfxFile.exists()) {
			LOGGER.error(jfxFile + " does not exist! Please compile the strategy or choose another path.");
			System.exit(1);
		}

		final IRemoteStrategyManager remoteManager = client.getRemoteStrategyManager();

		remoteManager.addStrategyListener(new RemoteStrategyListener() {

			public void onStrategyRun(IRemoteStrategyDescriptor descriptor) {
				LOGGER.info("remote startegy launched: " + descriptor);
				if (!descriptor.getId().equals(myStrategyId)) {
					LOGGER.info("Not our designated remote strategy - stop it!");
					remoteManager.stopStrategy(descriptor.getId());
				}
			};

			public void onStrategyStop(IRemoteStrategyDescriptor descriptor) {
				LOGGER.info("remote startegy stopped: " + descriptor);
			};
		});

		// start a strategy without fetching the id
		remoteManager.startStrategy(jfxFile);

		// start and fetch the id
		IStrategyResponse<UUID> startResponse = remoteManager.startStrategy(jfxFile).get();
		if (startResponse.isError()) {
			LOGGER.error("Remote strategy failed to start: " + startResponse.getErrorMessage());
		} else {
			myStrategyId = startResponse.getResult();
			LOGGER.info("Remote strategy successfully started: " + myStrategyId);
		}

		Set<IRemoteStrategyDescriptor> strategyDescriptors = remoteManager.getStartedStrategies().get().getResult();
		LOGGER.info("Remotely started " + strategyDescriptors.size() + " strategies: ");
		for (IStrategyDescriptor strategyDescriptor : strategyDescriptors) {
			LOGGER.info(strategyDescriptor.toString());
		}

	}
}
