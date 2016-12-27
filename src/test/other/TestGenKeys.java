package test.other;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class TestGenKeys {

	public static void main(String[] args) {
		int expected_modifiers =
			(Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL);
		Field[] fields = KeyEvent.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getModifiers() == expected_modifiers
				&& fields[i].getType() == Integer.TYPE
				&& fields[i].getName().startsWith("VK_")) {
				String name = fields[i].getName();
				System.out.println(name);
			}
		}

	}

}
