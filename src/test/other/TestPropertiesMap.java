package test.other;

import java.util.HashMap;
import java.util.Map;

public class TestPropertiesMap {
	private static Map<String, Object> properties = new HashMap<>();

	public static void main(String[] args) {
		System.out.println(isKeyDescription());
	}

	public static Object getProperty(String key) {
		return properties.get(key);
	}
	public static void setProperty(String key, Object property) {
		properties.put(key, property);
	}
	public static void setKeyDescription(boolean keyDescription) {
		setProperty("KeyDescription", keyDescription);
	}
	public static boolean isKeyDescription() {
		Boolean keyDescription = (Boolean) getProperty("KeyDescription");
		return (keyDescription == null ? false : keyDescription);
	}
}
