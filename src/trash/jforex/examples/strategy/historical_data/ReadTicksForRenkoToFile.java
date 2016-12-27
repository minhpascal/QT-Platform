package trash.jforex.examples.strategy.historical_data;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import com.dukascopy.api.*;
import com.dukascopy.api.drawings.IRectangleChartObject;
import com.dukascopy.api.feed.IRenkoBar;

/**
 * The following strategy shows how one can assamble his own renko bars
 * for multiple instruments over a custom period and write them to a file -
 * renkos for each instrument get written into a separate file.
 * 
 * The strategy uses asynchronous reading methods, meaning that all data
 * reads take place in parallel - in different threads.
 * 
 * Also the used approach is memory-efficient, since the strategy works
 * only with one tick and one renko bar at a time, as opposed to cases,
 * when ticks get loaded by IHistory.getTicks over big intrevals.
 * 
 * Note that the renko assembling algorithm is simplified, thus the results
 * differ from the ones of the platorm.
 *
 */
@RequiresFullAccess
public class ReadTicksForRenkoToFile implements IStrategy {

    private IHistory history;
    private IConsole console;
    private IContext context;

    @SuppressWarnings("serial")
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss") {
        {
            setTimeZone(TimeZone.getTimeZone("GMT"));
        }
    };

    @Configurable("from dd-MM-yyyy HH:mm:ss")
    public String fromStr = "18-07-2012 00:00:00";
    @Configurable("to dd-MM-yyyy HH:mm:ss")
    public String toStr = "20-07-2012 10:00:00";
    @Configurable("")
    public OfferSide side = OfferSide.BID;
    @Configurable("renko brick size")
    public int brickSize = 10;
    @Configurable("log to file (otherwise to messages tab)")
    public boolean logToFile = true;
    @Configurable("plot to chart (as rectangles)")
    public boolean plotOnChart = true;

    private final Set<Instrument> instruments = new HashSet<Instrument>(Arrays.asList(new Instrument[] { 
            Instrument.CHFJPY,
            Instrument.EURJPY, 
            Instrument.EURUSD, 
            Instrument.USDJPY }
    ));

    private Map<Instrument, Boolean> insrtDataLoaded = new HashMap<Instrument, Boolean>();
    private List<MyTickListener> tickListeners = new ArrayList<MyTickListener>();

    @Override
    public void onStart(IContext context) throws JFException {
        history = context.getHistory();
        console = context.getConsole();
        this.context = context;

        context.setSubscribedInstruments(instruments, true);

        long from = 0, to = 0;
        try {
            from = sdf.parse(fromStr).getTime();
            to = sdf.parse(toStr).getTime();
        } catch (ParseException e) {
            console.getErr().println(e + " on date parsing. The straetgy will stop.");
            context.stop();
        }

        for (Instrument instrument : instruments) {
            insrtDataLoaded.put(instrument, false);
                MyTickListener tickListener = logToFile 
                    ? new MyTickListener(new File (String.format("renkos_%s_%spips_%s_to_%s.txt",instrument,brickSize,fromStr,toStr).replaceAll("[-\\/:]", "_")))
                    : new MyTickListener(instrument);

            tickListeners.add(tickListener);
            history.readTicks(instrument, from, to, tickListener, new MyLoadingProgressListener(instrument) );
        }

    }
      
    public class MyTickListener implements LoadingDataListener {        

        MockRenko renko = null;
        MockRenko prevRenko = null;
        private PrintStream printStream;
        private FileOutputStream fileOutputStream;
        
        public MyTickListener (File logFile){
            try {        
                fileOutputStream = new FileOutputStream(logFile, false);
                console.getInfo().println("log to file: " + logFile.getAbsolutePath());
                printStream = new PrintStream(fileOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public MyTickListener(final Instrument instrument) {
            printStream = new InstrPrefixedStream(instrument, console.getOut());
        }        

        @Override
        public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {
            
            double price = side == OfferSide.BID ? bid : ask;
            double renkoHeight = instrument.getPipValue() * brickSize;
            double volume = side == OfferSide.BID ? bidVol : askVol;
            if (renko == null) {
                renko = new MockRenko(price, volume, time, renkoHeight);
                return;
            }
            if (renko.high < price) {
                renko.high = price;
            }
            if (renko.low > price) {
                renko.low = price;
            }
            renko.close = price; 
            renko.vol += volume;
            renko.endTime = time;
            renko.tickCount++;
            
            if(renko.isComplete()){
                renko.postProcess();
                
                printStream.println(renko);
                if(plotOnChart){
                    plotOnChart(renko, instrument);
                }

                //new bar start at the same price, but on the next millisecond
                renko = MockRenko.getNextRenko(renko);
                return;
            }
        }
        
        @Override
        public void newBar(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low,
                double high, double vol) {
            // no bars expected

        }
        
        private void plotOnChart(MockRenko renko, Instrument instrument){
            IChart chart = context.getChart(instrument);
            if (chart != null) {
                IRectangleChartObject obj = chart.getChartObjectFactory().createRectangle(UUID.randomUUID().toString(),
                        renko.startTime, renko.low, renko.endTime, renko.high);
                obj.setColor(renko.close > renko.open ? Color.GREEN : Color.RED);
                obj.setText(String.format("O=%.5f, C=%.5f, H=%.5f, L=%.5f", renko.open, renko.close, renko.high, renko.low));
                chart.addToMainChart(obj);
            }
        }
        
        public void dispose(){
            printStream.close();
        }
    }    
    
    /**
     * Input stream which prefixes outputs with an instrument
     */
    public class InstrPrefixedStream extends PrintStream {

        private final Instrument instrument;
        
        public InstrPrefixedStream(Instrument instrument, PrintStream out) {
            super(out);
            this.instrument = instrument;
        }
        @Override
        public void println(Object x) {
            super.println(instrument + " " + x);
        }  
        
    }
    
    public class MyLoadingProgressListener implements LoadingProgressListener {
        
        private final Instrument instrument;
        public MyLoadingProgressListener(Instrument instrument){
            this.instrument = instrument;
        }
        
        @Override
        public void dataLoaded(long start, long end, long currentPosition, String information) {}

        @Override
        public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition) {
            print("loadingFinished: instrument=%s, allDataLoaded=%s, start=%s, end=%s, currentPosition=%s", instrument,
                    allDataLoaded, sdf.format(start), sdf.format(end), sdf.format(currentPosition));
            insrtDataLoaded.put(instrument, true);
        }

        @Override
        public boolean stopJob() {
            return false;
        }
    }

    private void print(String format, Object... args) {
        console.getOut().println(String.format(format, args));
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {
        if (!insrtDataLoaded.values().contains(Boolean.FALSE)) {
            print("All renkos loaded, stopping the strategy.");
            for(MyTickListener tickListener : tickListeners){
                tickListener.dispose();
            }
            context.stop();
        }
    }
    
    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
    }

    @Override
    public void onMessage(IMessage message) throws JFException {
    }

    @Override
    public void onAccount(IAccount account) throws JFException {
    }

    @Override
    public void onStop() throws JFException {
    }

 

}

class MockRenko implements IRenkoBar {

    public double open;
    public double close;
    public double low;
    public double high;
    public double vol;
    public long startTime;
    public long endTime;
    public int tickCount;
    
    private MockRenko prevRenko;
    private double height;
    
    @SuppressWarnings("serial")
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss") {
        {
            setTimeZone(TimeZone.getTimeZone("GMT"));
        }
    };
    
    public static MockRenko getNextRenko(MockRenko prevRenko) {
        MockRenko renko = new MockRenko(prevRenko.close, 0, prevRenko.endTime + 1, prevRenko.height);
        renko.prevRenko = prevRenko;
        return renko;
    }

    public MockRenko(double price, double volume, long time, double height) {
        this.height = height;
        open = close = low = high = getRoundedPrice(price);
        vol = volume;
        startTime = time;
        endTime = time;
        tickCount = 1;
    }       
    
    public boolean isComplete(){
        return isGreenComplete() || isRedComplete();
    }
    
    private boolean isGreenComplete(){
        return prevRenko == null 
            ? high - open >= height
            : high - prevRenko.high >= height;
    }
    
    private boolean isRedComplete(){
        return prevRenko == null 
            ? open - low >= height
            : prevRenko.low - low >= height;
    }
    
    public void postProcess(){
        //on trend change high-low difference is double the renko height - adjust it here
        if(isGreenComplete()){
            low = high - height;
        } else {
            high = low + height;
        }
        //make "solid" bricks with prices rounded to the brick height
        low = getRoundedPrice(low);
        high = getRoundedPrice(high);
        close = getRoundedPrice(close);
        open = getRoundedPrice(open);
    }
    
    private double getRoundedPrice(double price){
        //rounded to the closest pip value that is divisible with brickSize
        double delta1 = price % height;
        double delta2 = height - price % height;
        double priceRounded = delta1 <= delta2
                ? price - delta1
                : price + delta2;
        return priceRounded;
    }

    @Override
    public double getOpen() {
        return open;
    }

    @Override
    public double getClose() {
        return close;
    }

    @Override
    public double getLow() {
        return low;
    }

    @Override
    public double getHigh() {
        return high;
    }

    @Override
    public double getVolume() {
        return vol;
    }

    @Override
    public long getTime() {
        return startTime;
    }

    @Override
    public String toString() {            
        return String.format("StartTime: %s EndTime: %s O: %.5f C: %.5f H: %.5f L: %.5f V: %.5f TickCount: %s",
                sdf.format(startTime), sdf.format(endTime), open, close, high, low, vol, tickCount);
    }

    @Override
    public long getEndTime() {
        return endTime;
    }

    @Override
    public long getFormedElementsCount() {
        return tickCount;
    }

    @Override
    public IRenkoBar getInProgressBar() {
        return null;
    }

	@Override
	public Double getWickPrice() {
		return null;
	}

}
