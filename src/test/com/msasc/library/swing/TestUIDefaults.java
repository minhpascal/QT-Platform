package test.com.msasc.library.swing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.UIDefaults;
import javax.swing.UIManager;

import org.apache.commons.lang3.StringUtils;

public class TestUIDefaults {

	public static void main(String[] args) {
		UIDefaults uid = UIManager.getLookAndFeelDefaults();
		Iterator<Object> keys = uid.keySet().iterator();
		ArrayList<String> keyList = new ArrayList<>();
		while (keys.hasNext()) {
			Object key = keys.next();
			keyList.add(key.toString());
		}
		
		String[] keyArr = keyList.toArray(new String[keyList.size()]);
		Arrays.sort(keyArr);
		
		int maxLength = 0;
		for (String key : keyArr) {
			maxLength = Math.max(maxLength, key.length()); 
		}
		maxLength += 5;
		
		for (String key : keyArr) {
			int pad = maxLength - key.length();
			Object obj = uid.get(key);
			System.out.println(key+StringUtils.repeat(" ", pad)+obj);
		}
	}

}
