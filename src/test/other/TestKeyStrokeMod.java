package test.other;

import java.awt.event.KeyEvent;

public class TestKeyStrokeMod {

	public static void main(String[] args) {
		int keyCode = KeyEvent.VK_W;
		int modifiers = KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK;
		
		System.out.println(modifiers | KeyEvent.CTRL_DOWN_MASK);
		System.out.println(modifiers | KeyEvent.ALT_DOWN_MASK);
		System.out.println((modifiers & KeyEvent.SHIFT_DOWN_MASK) != 0);
		System.out.println(modifiers & KeyEvent.CTRL_DOWN_MASK);
		System.out.println(modifiers & KeyEvent.ALT_DOWN_MASK);

	}

}
