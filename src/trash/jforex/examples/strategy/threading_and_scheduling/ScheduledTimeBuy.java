package trash.jforex.examples.strategy.threading_and_scheduling;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.*;

import com.dukascopy.api.*;
import com.dukascopy.api.util.DateUtils;

@RequiresFullAccess
public class ScheduledTimeBuy implements IStrategy {
    
    public static final TimeZone GMT_TIME_ZONE = TimeZone.getTimeZone("GMT 0");
    
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH_mm_ss_SSS");
    static {
        TIME_FORMAT.setTimeZone(GMT_TIME_ZONE);
    }

    @Configurable("")
    public int orderCount = 5;    
    @Configurable("")
    public double orderAmount = 0.001;
    @Configurable("")
    public Instrument instrument = Instrument.EURUSD;
    @Configurable("")
    public String orderPrefix = "order";
    
    private static Calendar calTodayNoon;
    static {
        calTodayNoon = Calendar.getInstance();
        calTodayNoon.set(Calendar.HOUR_OF_DAY, 12);
        calTodayNoon.set(Calendar.MINUTE, 00);
        calTodayNoon.set(Calendar.SECOND, 00);        
    }

    @Configurable(value="start time GMT", description="default is today at 12:00:00", datetimeAsLong=true)
    public long timeInMillis = calTodayNoon.getTimeInMillis();
    
    @Configurable(value="time unit")
    public TimeUnit timeUnit = TimeUnit.DAYS;
    
    @Configurable(value="interval")
    public long interval = 1;
    
    private int orderCounter = 0;
    
    private Timer timer = new Timer();
    private IEngine engine;
    private IConsole console;
    
    @Override
    public void onStart(final IContext context) throws JFException {
        this.engine = context.getEngine();
        this.console = context.getConsole();

        Date dateOff = new Date(timeInMillis + TimeZone.getDefault().getRawOffset());
        
        if(dateOff.getTime() < System.currentTimeMillis()){
            console.getWarn().format("The scheduled time %s has passed - now it is %s. Will reschedule for the same time tomorrow", 
                    DateUtils.format(dateOff.getTime()), DateUtils.format(System.currentTimeMillis())).println();
            dateOff = new Date(dateOff.getTime() + TimeUnit.DAYS.toMillis(1));
        }
          
        console.getOut().println("Time left till the order creation start: " + formatInterval(dateOff.getTime() - System.currentTimeMillis()));
        
        timer.scheduleAtFixedRate(new TimerTask(){
          @Override
          public void run() {
              context.executeTask(new Callable<IOrder>(){ 

                  @Override
                  public IOrder call() throws Exception {
                      if(orderCounter >= orderCount){
                          console.getOut().println("All orders created");
                          timer.cancel();
                          timer.purge();
                          context.stop();
                          return null;
                      }
                      String label = String.format("%s_%s_%s", orderPrefix, orderCounter++, TIME_FORMAT.format(System.currentTimeMillis()));
                      return engine.submitOrder(label, instrument, IEngine.OrderCommand.BUY, orderAmount);
                  }});
          }}

        , dateOff, timeUnit.toMillis(interval));

    }
    
    private static String formatInterval(final long l) {
        final long hr = MILLISECONDS.toHours(l);
        final long min = MILLISECONDS.toMinutes(l - HOURS.toMillis(hr));
        final long sec = MILLISECONDS.toSeconds(l - HOURS.toMillis(hr) - MINUTES.toMillis(min));
        final long ms = MILLISECONDS.toMillis(l - HOURS.toMillis(hr) - MINUTES.toMillis(min) - SECONDS.toMillis(sec));
        return String.format("%02d:%02d:%02d.%03d", hr, min, sec, ms);
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {
    }

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
    }

    @Override
    public void onMessage(IMessage message) throws JFException {
        IOrder o = message.getOrder();
        if(message.getType() != IMessage.Type.ORDER_FILL_OK){
            return;
        }
        console.getInfo().format("%s created on server at %s, filled at %s", 
                o.getLabel(), DateUtils.format(o.getCreationTime()), DateUtils.format(o.getFillTime())
        ).println();
    }

    @Override
    public void onAccount(IAccount account) throws JFException {
    }

    @Override
    public void onStop() throws JFException {
        timer.cancel();
    }

}
