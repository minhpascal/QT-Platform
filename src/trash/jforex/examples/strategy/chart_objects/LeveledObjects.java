package trash.jforex.examples.strategy.chart_objects;

import java.awt.Color;

import com.dukascopy.api.*;
import com.dukascopy.api.drawings.*;

/**
 * The following strategy demonstrates how one can modify leveled objects:
 * - remove a level
 * - add a level
 * - modify a level
 *
 */
public class LeveledObjects implements IStrategy {

    private IChart chart;
    private IHistory history;
    private IConsole console;
    
    @Configurable("")
    public Instrument instrument = Instrument.EURUSD;

    private OfferSide offerSide;
    private Period period;
        
    @Override
    public void onStart(IContext context) throws JFException {
        history = context.getHistory();
        console = context.getConsole();
        chart = context.getChart(instrument);
        
        if(chart == null){
            console.getErr().println("No chart opened for " + instrument);
            context.stop(); //stop the strategy
        }
        period = chart.getSelectedPeriod();
        offerSide = chart.getSelectedOfferSide();
        
        IChartObjectFactory factory = chart.getChartObjectFactory();     
        IBar bar10 = history.getBar(instrument, period, offerSide, 10);
        IBar bar30 = history.getBar(instrument, period, offerSide, 30);   
        
        IFiboRetracementChartObject fiboRetracement = factory.createFiboRetracement("fiboRetracement",
                bar30.getTime(), bar30.getLow(),
                bar10.getTime(), bar30.getLow()+ instrument.getPipValue() * 10);
        
        IFiboTimeZonesChartObject fiboTimezones = factory.createFiboTimeZones("fiboTimeZones",
                bar30.getTime(), bar30.getLow(),
                bar30.getTime() + period.getInterval() * 2, bar30.getLow());
        
        IFiboExpansionChartObject fiboExpansion =  factory.createFiboExpansion("fiboExpansion", 
                bar30.getTime(), bar30.getClose(),
                bar10.getTime(), bar10.getClose() + instrument.getPipValue() * 10,   
                bar10.getTime(), bar10.getClose());
        
        IFiboFanChartObject fiboFan = factory.createFiboFan("fiboFan",
                bar30.getTime(), bar30.getLow(),
                bar10.getTime(), bar30.getLow()+ instrument.getPipValue() * 20);
        
        IAndrewsPitchforkChartObject andrewsPitchfork = factory.createAndrewsPitchfork("andrewsPitchfork", 
                bar30.getTime(), bar30.getClose(),
                bar10.getTime(), bar10.getClose() + instrument.getPipValue() * 10,   
                bar10.getTime(), bar10.getClose());
        
        IPercentChartObject percent = factory.createPercent("percent",
                bar30.getTime(), bar30.getLow(), 
                bar30.getTime(), bar30.getLow() + instrument.getPipValue() * 30);
        chart.addToMainChart(percent);
        
        ILeveledChartObject[] leveledObjects = new ILeveledChartObject[]{
                fiboRetracement, fiboTimezones, fiboExpansion, fiboFan, andrewsPitchfork,  percent
        };
        
        for (ILeveledChartObject leveledObject : leveledObjects) {
            customizeLevels(leveledObject);
            chart.addToMainChart(leveledObject);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) { 
                e.printStackTrace();
            }
            chart.remove(leveledObject);
        }

    }
    
    private void customizeLevels( ILeveledChartObject leveledObject){
        printLevels(leveledObject, "before modification: ");
        
        console.getOut().println("remove level: "+leveledObject.getLevelValue(2));
        leveledObject.removeLevel(2);
        
        leveledObject.addLevel("new level", 0.90, Color.RED); 
        
        leveledObject.setLevelValue(3, 1.40);
        leveledObject.setLevelColor(3, Color.BLUE);
        leveledObject.setLevelLabel(3, "modified level");
        
        printLevels(leveledObject, "after modification: ");
    }
    
    private void printLevels( ILeveledChartObject leveledObject, String comment){
        String str = "";
        for(int i = 0; i <leveledObject.getLevelsCount(); i++){
            str +=  i + "=" + leveledObject.getLevelValue(i) + ", ";
        }
        console.getOut().println(comment + leveledObject.getClass().getSimpleName() + " levels: " +str);
    }


    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {   
    }

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}

    @Override
    public void onMessage(IMessage message) throws JFException {}

    @Override
    public void onAccount(IAccount account) throws JFException {}

    @Override
    public void onStop() throws JFException {
        chart.removeAll();
    }

}
