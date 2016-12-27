package test.com.msasc.library.trading;

import java.util.Collection;

import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.util.Calendar;

public class TestUtils {

	public static void main(String[] args) {
		Calendar calendar = Calendar.getGTMCalendar(System.currentTimeMillis());
		System.out.println(calendar);
		
		Period period;
		Collection<Period> periods;
	}

}
