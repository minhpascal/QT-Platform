package test.com.msasc.library.util;

import com.qtplaf.library.util.FormatUtils;
import com.qtplaf.library.util.Timestamp;

public class TestFormatUtilities {

	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		Timestamp timestamp = new Timestamp(time);
		System.out.println(FormatUtils.unformattedFromTimestamp(timestamp, false, true));
		System.out.println(FormatUtils.unformattedFromTimestamp(timestamp, true, true));
	}

}
