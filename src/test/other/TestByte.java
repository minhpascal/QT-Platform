package test.other;

import org.apache.commons.lang3.StringUtils;

import com.qtplaf.library.util.NumberUtils;

public class TestByte {
	public static void main(String[] args) {
		for (int i = -127; i <= 127; i++) {
			byte b = (byte) i;
			String hex = NumberUtils.toHexString(b);
			byte v = NumberUtils.parseByte(hex);
			System.out.println(hex + " " + i + " " + (byte) v);
		}
	}
}
