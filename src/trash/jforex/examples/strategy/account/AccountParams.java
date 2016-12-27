package trash.jforex.examples.strategy.account;

import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IConsole;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IEngine;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.Period;

public class AccountParams implements IStrategy {

    @Override
    public void onStart(IContext context) throws JFException {
        IConsole console = context.getConsole();
        IEngine engine = context.getEngine();
        IAccount account = context.getAccount();
        console.getOut().format("Account type: %s login: %s run mode: %s", engine.getType(), account.getUserName(), engine.getRunMode()).println();
        context.stop();
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
