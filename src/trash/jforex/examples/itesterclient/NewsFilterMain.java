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

import com.dukascopy.api.system.ISystemListener;
import com.dukascopy.api.system.IClient;
import com.dukascopy.api.system.ClientFactory;
import com.dukascopy.api.CalendarFilter;
import com.dukascopy.api.INewsFilter;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.NewsFilter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * This small program demonstrates how to initialize Dukascopy client and start a strategy
 */
public class NewsFilterMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewsFilterMain.class);

    //url of the DEMO jnlp
    private static String jnlpUrl = "https://www.dukascopy.com/client/demo/jclient/jforex.jnlp";
    //user name
    private static String userName = "msasc1EU";
    //password
    private static String password = "C1a2r3l4a5";

    public static void main(String[] args) throws Exception {
        //get the instance of the IClient interface
        final IClient client = ClientFactory.getDefaultInstance();
        //set the listener that will receive system events
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
                    client.reconnect();
                    --lightReconnects;
                } else {
                    try {
                        //sleep for 10 seconds before attempting to reconnect
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        //ignore
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
        //connect to the server using jnlp, user name and password
        client.connect(jnlpUrl, userName, password);

        //wait for it to connect
        int i = 10; //wait max ten seconds
        while (i > 0 && !client.isConnected()) {	
            Thread.sleep(1000);
            i--;
        }
        if (!client.isConnected()) {
            LOGGER.error("Failed to connect Dukascopy servers");
            System.exit(1);
        }
        
        //subscribe to the instruments
        Set<Instrument> instruments = new HashSet<Instrument>();
        instruments.add(Instrument.EURUSD);
        LOGGER.info("Subscribing instruments...");
        client.setSubscribedInstruments(instruments);
        

        
        NewsFilter newsFilter = new NewsFilter();
        newsFilter.setTimeFrame(NewsFilter.TimeFrame.TODAY);
        
        //filter indicies
        newsFilter.getStockIndicies().add(INewsFilter.StockIndex.NYSE);
        newsFilter.getStockIndicies().add(INewsFilter.StockIndex.DJI);
                
        //filter countries and regions
        newsFilter.getCountries().add(INewsFilter.Country.US);
        newsFilter.getCountries().add(INewsFilter.Country.FR);
        
        //filter market sectors
        newsFilter.getMarketSectors().add(INewsFilter.MarketSector.FCL);
        newsFilter.getMarketSectors().add(INewsFilter.MarketSector.ENE);
        
        //filter keywords
        newsFilter.getKeywords().add("Profit");
        newsFilter.getKeywords().add("Loss");
        
        client.addNewsFilter(newsFilter);
                        
        
        
        CalendarFilter calendarFilter = new CalendarFilter();
        calendarFilter.setTimeFrame(NewsFilter.TimeFrame.SPECIFIC_DATE);
        
        // custom historical interval
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date dateFrom = dateFormat.parse("2011-03-11");
        calendarFilter.setFrom(dateFrom);       

        //filter countries and regions
        newsFilter.getCountries().add(INewsFilter.Country.G7);
        
        //filter keywords
        calendarFilter.getKeywords().add("Treasury");
        calendarFilter.getKeywords().add("GDP");
        calendarFilter.getKeywords().add("trade");
        
        
        client.addNewsFilter(calendarFilter);
        
        
        //start the strategy
        LOGGER.info("Starting strategy");
        client.startStrategy(new NewsStrategyNoTrades());

    }
}
