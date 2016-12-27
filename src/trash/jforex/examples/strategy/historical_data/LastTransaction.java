package trash.jforex.examples.strategy.historical_data;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import com.dukascopy.api.*;

/** 
 * The strategy prints order transaction history over the selected period.
   Order changes that we consider as transactions are:
     1) order fill (check with active orders)
     2) order close (check with history orders)
   since they are the only ones that affect balance.
*/
public class LastTransaction implements IStrategy {

	@Configurable("Transaction history period")
	public Period historyPeriod = Period.DAILY;
	
	private IHistory history;
	private IConsole console;
	private IEngine engine;
	
	@SuppressWarnings("serial")
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS") {
		{
			setTimeZone(TimeZone.getTimeZone("GMT"));
		}
	};
	public static DecimalFormat df = new DecimalFormat("0.000##");
	
	@Override
	public void onStart(IContext context) throws JFException {
		history = context.getHistory();
		engine = context.getEngine();
		console = context.getConsole();
		
		List<IOrder> activeOrders = engine.getOrders();
		List<IOrder> historyOrders = new ArrayList<IOrder>();
		long from = System.currentTimeMillis() - historyPeriod.getInterval();
		long to = System.currentTimeMillis();
		for (Instrument instrument : context.getSubscribedInstruments()){
			historyOrders.addAll(history.getOrdersHistory(instrument, from, to));
		}
			
		print("History (Closed/Cancelled) order count from " + sdf.format(from) + " to " + sdf.format(to) + " is " + historyOrders.size());
		print("Active (Created/Opened/Filled) order count at " + sdf.format(to) + " is " + activeOrders.size());
		

		
		//get all transactions within a period
		for (IOrder order : activeOrders){
			if(order.getFillTime() > from && order.getFillTime() < to){
				print(order.getId() + " fill transaction time: " + sdf.format(order.getFillTime()) + " amount: " + df.format(order.getAmount()));
			}
		}
		for (IOrder order : historyOrders){
			if(order.getCloseTime() > from && order.getCloseTime() < to){
				print(order.getId() + " close transaction time: " + sdf.format(order.getCloseTime()) + " amount: " + df.format(order.getAmount()));
			}
		}
		
		//get last transaction		
		long timeTrans = 0;
		IOrder orderTrans = null;
		for (IOrder order : activeOrders){
			if(order.getFillTime() > timeTrans){
				timeTrans = order.getFillTime();
				orderTrans = order;
			}
		}
		for (IOrder order : historyOrders){
			if(order.getCloseTime() > timeTrans){
				timeTrans = order.getCloseTime();
				orderTrans = order;
			}
		}
		if (orderTrans == null){
			print("No trasactions were found within the selected period");
		} else {
			print("Last transaction time: " + sdf.format(timeTrans) + " amount: " + df.format(orderTrans.getAmount()));
		}
		
		context.stop();
	}
	
	private void print(Object o){
		console.getOut().println(o);
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
