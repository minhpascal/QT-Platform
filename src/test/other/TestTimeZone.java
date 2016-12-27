package test.other;

import java.util.TimeZone;

public class TestTimeZone {

	public static void main(String[] args) {
		String[] ids = TimeZone.getAvailableIDs();
		for (String id : ids) {
			System.out.println(id+" - "+TimeZone.getTimeZone(id));
		}

	}

}
