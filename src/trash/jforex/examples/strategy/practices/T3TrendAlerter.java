package trash.jforex.examples.strategy.practices;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.sound.sampled.*;

import com.dukascopy.api.*;
import com.dukascopy.api.IIndicators.AppliedPrice;

@RequiresFullAccess
public class T3TrendAlerter implements IStrategy {
    private IContext context;
    private IIndicators indicators;
    private IHistory history;
    
    @Configurable("")
    public Instrument instrument = Instrument.EURUSD;
    @Configurable("")
    public Period period = Period.TEN_SECS;
    @Configurable("")
    public OfferSide side = OfferSide.BID;
    @Configurable("")
    public AppliedPrice appliedPrice = AppliedPrice.CLOSE;
    @Configurable("")
    public Filter filter = Filter.NO_FILTER;
    @Configurable("")
    public int t3TimePeriod = 5;
    @Configurable("")
    public double t3vFactor = 1.0;
    @Configurable("")
    public File trendUpAlarm = new File("C:\\temp\\duck.wav");    
    @Configurable("")
    public File trendDownAlarm = new File("C:\\temp\\bird.wav");

    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss SSS");
    private static int LAST = 2;
    private static int PREV = 1;
    private static int SCND_TO_LAST = 0;
    
    public void onStart(IContext context) throws JFException {
        this.context = context;
        this.indicators = context.getIndicators();    
        this.history = context.getHistory();
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));        
        print("start");
    }
    
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
        if (!this.period.equals(period) || !this.instrument.equals(instrument)){
            return;
        }

        double[] t3 = indicators.t3(instrument, period, side, appliedPrice, t3TimePeriod, t3vFactor, filter, 3, bidBar.getTime(), 0);
       
        if(t3[PREV] > t3[SCND_TO_LAST] && t3[LAST] < t3[PREV]){ //down trend
            playSound(trendDownAlarm);
        } else if(t3[PREV] < t3[SCND_TO_LAST] && t3[LAST] > t3[PREV]){ //up trend
            playSound(trendUpAlarm);
        }
    }
    
    private void playSound(File wavFile) throws JFException {
        print(sdf.format(history.getLastTick(instrument).getTime()) + " play: " + wavFile.getName());
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavFile);
            AudioFormat af = audioInputStream.getFormat();
            int nSize = (int) (af.getFrameSize() * audioInputStream.getFrameLength());
            byte[] audio = new byte[nSize];
            DataLine.Info info = new DataLine.Info(Clip.class, af, nSize);
            audioInputStream.read(audio, 0, nSize);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(af, audio, 0, nSize);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
            print("error on play: " + e.getMessage());
            //throw new JFException(e);
        }
    }
    
    private void print(Object o){
        context.getConsole().getOut().println(o);
    }

    public void onAccount(IAccount account) throws JFException {    }
    public void onMessage(IMessage message) throws JFException {    }
    public void onStop() throws JFException {}
    public void onTick(Instrument instrument, ITick tick) throws JFException {    }

}
