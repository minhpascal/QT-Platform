package trash.jforex.examples.strategy.practices;

import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import com.dukascopy.api.*;

/**
 * The strategy demonstrates how one can create a customized table
 * which allows the user both to modify orders and attach some customized
 * data to them.
 *
 */
public class OrderUpdateTableStrategy2 implements IStrategy {
    private static final String TAB_NAME = "Order Update Table";
    private IContext context;
    private IUserInterface userInterface;

    private OrderTableModel tableModel;
    private JTable table;
    private IEngine engine;
    public IHistory history;
    
    public void onStart(IContext context) throws JFException {
        this.context = context;
        this.engine = context.getEngine();
        this.history = context.getHistory();
        userInterface = context.getUserInterface();
        placeControlsOnTab(context);
        updateTable();
    }

    private void placeControlsOnTab(IContext context) {
        JPanel mainPanel = userInterface.getBottomTab(TAB_NAME);
        mainPanel.setLayout(new BorderLayout());
        tableModel = new OrderTableModel();
        table = new JTable(tableModel);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

    }

    private void updateTable() throws JFException {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    try {
                        tableModel.setData(engine.getOrders());
                    } catch (JFException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();            
        }
    }
    
    public double getLastPrice(Instrument instrument, boolean isLong){
        try {
            ITick tick = history.getLastTick(instrument);
            return isLong ? tick.getAsk() : tick.getBid(); 
        } catch (JFException e) {
            e.printStackTrace();
        }
        return 0d;
    }

    public void onAccount(IAccount account) throws JFException {
    }

    public void onMessage(IMessage message) throws JFException {
        if (message.getOrder() != null) {
            updateTable();
        }
    }

    public void onStop() throws JFException {
        userInterface.removeBottomTab(TAB_NAME);
    }

    public void onTick(Instrument instrument, final ITick tick) throws JFException {
        if(engine.getOrders(instrument).size() > 0){
            updateTable();
        }
    }
    

    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
    }
    
    private static double round(double amount, int decimalPlaces) {
        return (new BigDecimal(amount)).setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    //function delegates for order rows    
    private interface IOrderSet{
        void setValue(IOrder order, Object value) throws JFException;
    }

    private interface IOrderGet {
        String getValue(IOrder order);
    }

    //each order column has its name, value representation function and value update function
    private class OrderColumn {

        private final String name;
        private final boolean editable;
        private final IOrderGet orderGet;
        private final IOrderSet orderSet;

        private OrderColumn(String name, boolean editable, IOrderGet getValueFunc, IOrderSet onChangeFunc) {
            this.name = name;
            this.editable = editable;
            this.orderGet = getValueFunc;
            this.orderSet = onChangeFunc;
        }        

        public String getName() {
            return name;
        }
        
        public boolean isEditable(){
            return editable;
        }

        public IOrderGet getOrderGet() {
            return orderGet;
        }

        public IOrderSet getOrderSet() {
            return orderSet;
        }
    }
    


    @SuppressWarnings("serial")
    private class OrderTableModel extends AbstractTableModel {
        
        private Map<IOrder, String> orderNotes = new HashMap<IOrder, String>();
        
        //define 6 order columns and their behavior
        private final OrderColumn[] orderColumns = new OrderColumn[]{
            new OrderColumn(
                "Order label", 
                false,  //can't edit label                
                new IOrderGet() {
                    @Override
                    public String getValue(IOrder order) {
                        return order.getLabel();
                    }
                }, 
                new IOrderSet() {
                    @Override
                    public void setValue(IOrder order, Object value) throws JFException{}
                }
            ), 
            new OrderColumn(
                "Stop Loss", 
                true, 
                new IOrderGet() {
                    @Override
                    public String getValue(IOrder order) {
                        return order.getStopLossPrice() == 0 ? "-" : String.format("%.6f",order.getStopLossPrice());
                    }
                }, 
                new IOrderSet() {
                    @Override
                    public void setValue(IOrder order, Object value) throws JFException{
                        order.setStopLossPrice(round(Double.valueOf(value.toString()),order.getInstrument().getPipScale() + 1));
                    }
                }
            ), 
            new OrderColumn(
                "Take Profit", 
                true, 
                new IOrderGet() {
                    @Override
                    public String getValue(IOrder order) {
                        return order.getTakeProfitPrice() == 0 ? "-" : String.format("%.6f",order.getTakeProfitPrice());
                    }
                }, 
                new IOrderSet() {
                    @Override
                    public void setValue(IOrder order, Object value) throws JFException{
                        order.setTakeProfitPrice(round(Double.valueOf(value.toString()),order.getInstrument().getPipScale() + 1));
                    }
                }
            ), 
            new OrderColumn(
                "SL distance in pips", 
                true, 
                new IOrderGet() {
                    @Override
                    public String getValue(IOrder order) {
                        if(order.getStopLossPrice() == 0){
                            return "-";
                        }
                        double lastPrice = OrderUpdateTableStrategy2.this.getLastPrice(order.getInstrument(), order.isLong());
                        return String.format("%.1f",Math.abs( lastPrice - order.getStopLossPrice()) / order.getInstrument().getPipValue());
                    }
                }, 
                new IOrderSet() {
                    @Override
                    public void setValue(IOrder order, Object value) throws JFException{
                        double lastPrice = OrderUpdateTableStrategy2.this.getLastPrice(order.getInstrument(), order.isLong());
                        double priceDelta = Double.valueOf(value.toString()) * order.getInstrument().getPipValue();
                        double price = order.isLong() ? lastPrice - priceDelta : lastPrice + priceDelta;
                        order.setStopLossPrice(round(price,order.getInstrument().getPipScale() + 1));
                    }
                }
            ), 
            new OrderColumn(
                "TP distance in pips", 
                true, 
                new IOrderGet() {
                    @Override
                    public String getValue(IOrder order) {
                        if(order.getTakeProfitPrice() == 0){
                            return "-";
                        }
                        double lastPrice = OrderUpdateTableStrategy2.this.getLastPrice(order.getInstrument(), order.isLong());
                        return String.format("%.1f", Math.abs(lastPrice - order.getTakeProfitPrice()) / order.getInstrument().getPipValue());
                    }
                }, 
                new IOrderSet() {  
                    @Override
                    public void setValue(IOrder order, Object value) throws JFException{
                        double lastPrice = OrderUpdateTableStrategy2.this.getLastPrice(order.getInstrument(), order.isLong());
                        double priceDelta = Double.valueOf(value.toString()) * order.getInstrument().getPipValue();
                        double price = order.isLong() ? lastPrice + priceDelta : lastPrice - priceDelta;
                        order.setTakeProfitPrice(round(price,order.getInstrument().getPipScale() + 1));
                    
                    }
                }
           ), 
           new OrderColumn(
                   "Order notes", 
                   true, 
                   new IOrderGet() {
                       @Override
                       public String getValue(IOrder order) {
                           String note = orderNotes.get(order);
                           return note == null ? "" : note;
                       }
                   }, 
                   new IOrderSet() {  
                       @Override
                       public void setValue(IOrder order, Object value) throws JFException{
                           orderNotes.put(order, value.toString());
                       }
                   }
              )            
        };
        
        private List<IOrder> orders;

        public void setData(List<IOrder> orders) {
            this.orders = orders;
            fireTableDataChanged();
        }

        public int getRowCount() {
            return orders.size();
        }

        public int getColumnCount() {
            return orderColumns.length;
        }

        public Object getValueAt(int row, int column) {
            IOrder order = orders.get(row);
            return orderColumns[column].getOrderGet().getValue(order);
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return orderColumns[columnIndex].isEditable();
        }

        public void setValueAt(final Object aValue, int rowIndex, final int columnIndex) {
            final IOrder order = orders.get(rowIndex);
            //order operations need to be executed from the strategy thread
             context.executeTask(new Callable<Object>() {
                 @Override
                 public Object call() throws Exception {
                     orderColumns[columnIndex].getOrderSet().setValue(order, aValue);
                     return null;
                 }
             });
        }

        public String getColumnName(int column) {
            return orderColumns[column].getName();
        }
    }

}
