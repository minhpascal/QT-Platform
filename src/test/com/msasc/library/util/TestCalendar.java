package test.com.msasc.library.util;

import java.util.TimeZone;

import com.qtplaf.library.util.Calendar;

public class TestCalendar {

	public static void main(String[] args) {
		test2();
	}

	@SuppressWarnings("unused")
	private static void test1() {
		String[] zones = new String[]{ "GMT", "GMT0", "CET", "MET", "EST", "EAT" };
		for (String zone : zones) {
			Calendar calendar = new Calendar(2016,4,20);
			calendar.setTimeZone(TimeZone.getTimeZone(zone));
			System.out.println(zone+" - "+calendar.getTimeInMillis());
		}
	}
	
	private static void test2() {
		Calendar cal1 = new Calendar(2016,4,20,10,0,0);
		cal1.setTimeZone(TimeZone.getTimeZone("GMT"));
		Calendar cal2 = new Calendar();
		cal2.setTimeZone(TimeZone.getTimeZone("GMT"));
		cal2.setYear(2016);
		cal2.setMonth(4);
		cal2.setDay(20);
		cal2.setHour(10);
		cal2.setMinute(0);
		cal2.setSecond(0);
		cal2.setMilliSecond(0);
		System.out.println(cal1.getTimeInMillis());
		System.out.println(cal2.getTimeInMillis());
	}
}
