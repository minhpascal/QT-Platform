package trash.jforex.examples.itesterclient;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import com.dukascopy.api.*;
import com.dukascopy.api.ICalendarMessage.Detail;

public class NewsStrategyNoTrades implements IStrategy {
    private IConsole console;
    @SuppressWarnings("serial")
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") {{setTimeZone(TimeZone.getTimeZone("GMT"));}};

    public void onStart(IContext context) throws JFException {
        this.console = context.getConsole();
    }

    public void onAccount(IAccount account) throws JFException {
    }

    public void onMessage(IMessage message) throws JFException {  
    	
    	//we are only interested in news and calendar messages
    	if(!(message.getType().equals(IMessage.Type.CALENDAR) || message.getType().equals(IMessage.Type.NEWS))){
    		return;
    	}
    	
    	INewsMessage news = ((INewsMessage) message);    	
    	print("---------------------------");
    	//log some news specific info
    	if (message.getType().equals(IMessage.Type.NEWS)) {
    		print(String.format("[News message] %s at %s", news.getHeader(), sdf.format(news.getCreationTime())));
    	}

    	//log some calendar specific info
        if (message.getType().equals(IMessage.Type.CALENDAR)) {
        	ICalendarMessage cal = ((ICalendarMessage) message);
        	List<Detail> calDetails = cal.getDetails();
        	String detailStr = "[Calendar details]";
        	for (Detail d : calDetails){
        		detailStr += String.format("\n    Id:%s, Description:%s, Expected:%s, Actual:%s, Delta:%s, Previous:%s ", 
        				d.getId(), d.getDescription(), d.getExpected(), d.getActual(), d.getDelta(), d.getPrevious());
        	}
        	print(String.format("[Calendar message] %s, %s, %s, %s, %s, %s, %s ", 
        			cal.getContent(), cal.getCountry(), cal.getCompanyURL(),
        			cal.getEventCode(), cal.getOrganisation(), cal.getPeriod(), sdf.format(cal.getEventDate())));
        	if (calDetails.size() > 0){
        		print(detailStr);
        	}
        } 
        
        //log common meta info
        print(String.format("[meta info] Stock Indicies: %s. Regions: %s. Market sectors: %s. Currencies: %s", 
        		news.getStockIndicies(), news.getGeoRegions(), 
        		news.getMarketSectors(), news.getCurrencies()));

    }
    
    public void onStop() throws JFException {    }
    public void onTick(Instrument instrument, ITick tick) throws JFException {    }
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}    
    private void print(Object o){
    	console.getOut().println(o);
    }
}
