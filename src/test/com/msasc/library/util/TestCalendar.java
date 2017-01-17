package test.com.msasc.library.util;

import com.qtplaf.library.util.Calendar;

public class TestCalendar {

	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		System.out.println(new Calendar(time));
		System.out.println(Calendar.getGTMCalendar(time));
	}

}
