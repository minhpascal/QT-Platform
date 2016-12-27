package trash.jforex.examples.strategy.historical_data;

import java.util.List;
import com.dukascopy.api.*;
import com.dukascopy.api.util.*;


public class PriceBreachAccordingToOrder implements IStrategy {
    
    @Configurable("")
    public Instrument instrument = Instrument.EURUSD;
    
    private IHistory history;
    private IConsole console;
    private IEngine engine;
    
    enum Location {
        ABOVE,
        BELOW
    }
    
    private Location priceLocation;
    private double priceLevel;

    @Override
    public void onStart(IContext context) throws JFException {
        history = context.getHistory();
        console = context.getConsole();   
        engine = context.getEngine();
        context.setSubscribedInstruments(java.util.Collections.singleton(Instrument.EURUSD), true);
        
        ITick lastTick = history.getLastTick(instrument);
        for(IOrder order : engine.getOrders(instrument)){
            int breachCount = 0;
            int fallCount = 0;
            //choose as price level the order open price + 5pips
            priceLevel = order.getOpenPrice() + instrument.getPipValue() * 5;
            List<ITick> ticks = history.getTicks(instrument, order.getFillTime(), lastTick.getTime());
            if(ticks == null || ticks.size() == 0){
                console.getErr().println("There are no ticks between order fill time and the last tick");
                continue;
            }
            
            priceLocation = ticks.get(0).getBid() >= priceLevel 
                    ? Location.ABOVE     //the oldest tick price is above the target price
                    : Location.BELOW;
            ticks.remove(0); //not to iterate over the already considered tick
            
            for(ITick tick : ticks){
                if(priceLocation == Location.ABOVE && tick.getBid() < priceLevel){
                    priceLocation = Location.BELOW;
                    fallCount++;
                } else if(priceLocation == Location.BELOW && tick.getBid() > priceLevel){
                    priceLocation = Location.ABOVE;
                    breachCount++;
                }
            }
            console.getOut().println(String.format(
                "For order %s since its fill at %s the price level of %.5f was breached %s and fallen under %s times.", 
                order.getLabel(), DateUtils.format(order.getFillTime()), priceLevel, breachCount, fallCount
            ));
        }
    }


    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {}

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}

    @Override
    public void onMessage(IMessage message) throws JFException {}

    @Override
    public void onAccount(IAccount account) throws JFException {}

    @Override
    public void onStop() throws JFException {}

}
