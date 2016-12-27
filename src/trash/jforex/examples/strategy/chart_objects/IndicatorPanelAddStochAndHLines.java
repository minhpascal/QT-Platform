package trash.jforex.examples.strategy.chart_objects;

import java.awt.Color;

import com.dukascopy.api.*;
import com.dukascopy.api.drawings.IHorizontalLineChartObject;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;

public class IndicatorPanelAddStochAndHLines implements IStrategy {

    @Configurable("")
    public Instrument instrument = Instrument.EURUSD;
    private IChart chart;
    private IConsole console;
    private IIndicators indicators;

    @Override
    public void onStart(IContext context) throws JFException {
        this.chart = context.getChart(instrument);
        this.console = context.getConsole();
        this.indicators = context.getIndicators();
        
        if(chart == null){            
            console.getErr().println("No chart opened for " + instrument);
            context.stop(); //stop the strategy right away
        }
        
        //add RSI with timePeriod = 15
        IChartPanel rsiPanel = chart.add(indicators.getIndicator("RSI"), new Object[]{15});
        
        //and another RSI to the same panel
        rsiPanel.add(indicators.getIndicator("RSI"), new Object[]{30}, new Color[]{Color.MAGENTA},new DrawingStyle[]{DrawingStyle.LINE}, new int[]{1});
        
        //add an extra level line
        IHorizontalLineChartObject hLine = chart.getChartObjectFactory().createHorizontalLine("subHLine", 50);
        hLine.setColor(Color.RED);
        hLine.setLineStyle(LineStyle.DASH_DOT_DOT);
        rsiPanel.add(hLine);
        
        
        //we can't add to chart panels with isOverChart()=true
        IndicatorInfo emaInfo = indicators.getIndicator("EMA").getIndicatorInfo();
        if(emaInfo.isOverChart() && rsiPanel instanceof IIndicatorPanel){
            console.getOut().println("can't add " + emaInfo.getName() + " to the indicator panel since it can be plotted only on the main chart!");
        }
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {
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
        
        //remove all indicator panels
        while(chart.getIndicatorPanels().size() > 0){
            IChartPanel panel = chart.getIndicatorPanels().get(0);
            for (IIndicator indicator : panel.getIndicators()) {
              panel.removeIndicator(indicator);
            }
        }
    }

}
