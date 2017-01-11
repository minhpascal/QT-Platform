package test.com.msasc.library.trading.data;

import com.qtplaf.library.trading.data.Period;

public class TestPeriod {

	public static void main(String[] args) {
		Period[] periods = new Period[]{
			Period.OneMin,
			Period.ThreeMins,
			Period.FiveMins,
			Period.FifteenMins,
			Period.ThirtyMins,
			Period.OneHour,
			Period.FourHours,
			Period.Daily,
			Period.Weekly,
			Period.Monthly
		};
		for (Period period : periods) {
			System.out.println(period.getId() + " " + Period.parseId(period.getId()));
		}
	}

}
