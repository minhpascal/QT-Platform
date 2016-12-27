package trash.jforex.examples.strategy.chart_objects;

import com.dukascopy.api.*;
import com.dukascopy.api.drawings.IShortLineChartObject;
import com.dukascopy.api.drawings.ISignalDownChartObject;
import com.dukascopy.api.util.DateUtils;

/**
 * The strategy finds intersection point of two short lines and then
 * plots a signal chart object on each of those intersections.
 *
 */
public class IntersectionPoint implements IStrategy {
    
    private IHistory history;
    private IChart chart;
    private IConsole console;
    
    @Configurable("")
    public Instrument instrument = Instrument.EURUSD;
    @Configurable("")
    public Period period = Period.ONE_MIN;
    @Configurable("")
    public OfferSide side = OfferSide.BID;
    
    class PointDbl {
        public final double x;
        public final double y;        
        public PointDbl(double x, double y){
            this.x = x;
            this.y = y;
        }
    }

    @Override
    public void onStart(IContext context) throws JFException {
        history = context.getHistory();
        console = context.getConsole();
        chart = context.getChart(instrument);
        if(chart == null){
            console.getErr().println("No chart opened, can't plot lines");
            context.stop();
        }

        IBar bar1 = history.getBar(instrument, period, side, 1);
        IBar bar10 = history.getBar(instrument, period, side, 10);
        IShortLineChartObject line1 = chart.getChartObjectFactory().createShortLine("line1", 
                bar1.getTime(), bar1.getHigh(), bar10.getTime(), bar10.getLow()
        );
        IShortLineChartObject line2 = chart.getChartObjectFactory().createShortLine("line2", 
                bar1.getTime(), bar1.getLow(), bar10.getTime(), bar10.getHigh()
        );
        chart.add(line1);
        chart.add(line2);
        PointDbl point = intersection( 
                line1.getTime(0), line1.getPrice(0), line1.getTime(1), line1.getPrice(1), 
                line2.getTime(0), line2.getPrice(0), line2.getTime(1), line2.getPrice(1) 
                );
        if(point == null){
            console.getOut().println("Lines don't intersect");
        } else {
            console.getOut().println(String.format("Lines intersect at %s:%.5f ",DateUtils.format(point.x), point.y));
            ISignalDownChartObject signalArr = chart.getChartObjectFactory().createSignalDown("intersection_point", (long)point.x + period.getInterval()/2, point.y);
            signalArr.setStickToCandleTimeEnabled(false);
            chart.add(signalArr);
        }

    }

    public PointDbl intersection(
            double x1, double y1, double x2, double y2, 
            double x3, double y3, double x4, double y4
        ) {
        double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (d == 0)
            return null;

        double xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
        double yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;

        return new PointDbl(xi, yi);
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
