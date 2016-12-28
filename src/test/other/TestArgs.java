package test.other;

import java.util.HashMap;
import java.util.Map;

public class TestArgs {

	public static void main(String[] args) {
		if (args != null) {
			for (String arg :args) {
				System.out.println(arg);
			}
		}
		
		Map<String, String> map = new HashMap<>();
		map.put("A", null);

	}

}
