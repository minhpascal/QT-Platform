package trash.jforex.examples.strategy.indicators;

import com.dukascopy.api.*;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IndicatorInfo;

@RequiresFullAccess   
public class IndicatorMetadata implements IStrategy {

	private IConsole console;

	@Override
	public void onStart(IContext context) throws JFException {
		console = context.getConsole();
		IIndicator indCOG = context.getIndicators().getIndicator("COG");
		printIndicatorInfos(indCOG);
	}
	
	private void printIndicatorInfos(IIndicator ind){
		IndicatorInfo info = ind.getIndicatorInfo();
		print(String.format("%s: input count=%s, optional input count=%s, output count=%s", 
				info.getTitle(), info.getNumberOfInputs(), info.getNumberOfOptionalInputs(), info.getNumberOfOutputs()));
	    for (int i = 0; i < ind.getIndicatorInfo().getNumberOfInputs(); i++){
	        print(String.format("Input %s: %s - %s", i, ind.getInputParameterInfo(i).getName(), ind.getInputParameterInfo(i).getType()));
	    }
	    for (int i = 0; i < ind.getIndicatorInfo().getNumberOfOptionalInputs(); i++){
	    	print(String.format("Opt Input %s: %s - %s", i, ind.getOptInputParameterInfo(i).getName(), ind.getOptInputParameterInfo(i).getType()));
	    }
	    for (int i = 0; i < ind.getIndicatorInfo().getNumberOfOutputs(); i++){
	    	print(String.format("Output %s: %s - %s", i, ind.getOutputParameterInfo(i).getName(), ind.getOutputParameterInfo(i).getType()));
	    }
	}
	
	private void print(Object o){
	    console.getOut().println(o);
	}
	

	@Override
	public void onTick(Instrument instrument, ITick tick) throws JFException {}

	@Override
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(IMessage message) throws JFException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccount(IAccount account) throws JFException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStop() throws JFException {
		// TODO Auto-generated method stub
		
	}
}

