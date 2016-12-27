package trash.jforex.examples.strategy.chart_objects;

import com.dukascopy.api.*;
import com.dukascopy.api.chart.mouse.IChartPanelMouseEvent;
import com.dukascopy.api.chart.mouse.IChartPanelMouseListener;

public class MouseListenerStrat implements IStrategy {

    private IConsole console;
    private IChart chart;
    private IChartPanelMouseListener listener;
    
    @Override
    public void onStart(IContext context) throws JFException {
        console = context.getConsole();
        chart = context.getChart(Instrument.EURUSD);
        if(chart == null){
            console.getErr().println("No chart opened for EURUSD!");
            context.stop();
        }
        chart.addMouseListener(false, listener = new IChartPanelMouseListener(){
            public void mouseClicked(IChartPanelMouseEvent e) { print(e, "mouse Clicked");}
            public void mousePressed(IChartPanelMouseEvent e) { print(e, "mouse Pressed");}
            public void mouseReleased(IChartPanelMouseEvent e) { print(e, "mouse Released");}
            public void mouseEntered(IChartPanelMouseEvent e) { print(e, "mouse Entered");}
            public void mouseExited(IChartPanelMouseEvent e) { print(e, "mouse Exited");}
            public void mouseDragged(IChartPanelMouseEvent e) { print(e, "mouse Dragged");}
            public void mouseMoved(IChartPanelMouseEvent e) { print(e, "mouse Moved");}
        });

    }
    
    private void print(IChartPanelMouseEvent e, String comment){
        console.getOut().println(String.format("%s %s x=%s y=%s",comment, e.toString(), e.getSourceEvent().getXOnScreen(), e.getSourceEvent().getYOnScreen()));
    }
    
    @Override
    public void onStop() throws JFException {
        chart.removeMouseListener(listener);
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {}

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}

    @Override
    public void onMessage(IMessage message) throws JFException {}

    @Override
    public void onAccount(IAccount account) throws JFException {}



}
