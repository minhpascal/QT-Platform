package test.other;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.KeyStroke;

import com.qtplaf.library.util.StringUtils;

public class TestKeyStroke {

	public static void main(String[] args) {
		int[] modifiers = new int[] {
			KeyEvent.SHIFT_MASK,
			KeyEvent.CTRL_MASK,
			KeyEvent.META_MASK,
			KeyEvent.ALT_MASK,
			KeyEvent.ALT_GRAPH_MASK,
			KeyEvent.BUTTON1_MASK,
			KeyEvent.BUTTON2_MASK,
			KeyEvent.BUTTON3_MASK,
			KeyEvent.SHIFT_DOWN_MASK,
			KeyEvent.CTRL_DOWN_MASK,
			KeyEvent.META_DOWN_MASK,
			KeyEvent.ALT_DOWN_MASK,
			KeyEvent.BUTTON1_DOWN_MASK,
			KeyEvent.BUTTON2_DOWN_MASK,
			KeyEvent.BUTTON3_DOWN_MASK,
			KeyEvent.ALT_GRAPH_DOWN_MASK
		};
		int[] keyCodes = new int[] {
			KeyEvent.VK_ENTER,
			KeyEvent.VK_BACK_SPACE,
			KeyEvent.VK_TAB,
			KeyEvent.VK_CANCEL,
			KeyEvent.VK_CLEAR,
			KeyEvent.VK_SHIFT,
			KeyEvent.VK_CONTROL,
			KeyEvent.VK_ALT,
			KeyEvent.VK_PAUSE,
			KeyEvent.VK_CAPS_LOCK,
			KeyEvent.VK_ESCAPE,
			KeyEvent.VK_SPACE,
			KeyEvent.VK_PAGE_UP,
			KeyEvent.VK_PAGE_DOWN,
			KeyEvent.VK_END,
			KeyEvent.VK_HOME,
			KeyEvent.VK_LEFT,
			KeyEvent.VK_UP,
			KeyEvent.VK_RIGHT,
			KeyEvent.VK_DOWN,
			KeyEvent.VK_COMMA,
			KeyEvent.VK_MINUS,
			KeyEvent.VK_PERIOD,
			KeyEvent.VK_SLASH,
			KeyEvent.VK_0,
			KeyEvent.VK_1,
			KeyEvent.VK_2,
			KeyEvent.VK_3,
			KeyEvent.VK_4,
			KeyEvent.VK_5,
			KeyEvent.VK_6,
			KeyEvent.VK_7,
			KeyEvent.VK_8,
			KeyEvent.VK_9,
			KeyEvent.VK_SEMICOLON,
			KeyEvent.VK_EQUALS,
			KeyEvent.VK_A,
			KeyEvent.VK_B,
			KeyEvent.VK_C,
			KeyEvent.VK_D,
			KeyEvent.VK_E,
			KeyEvent.VK_F,
			KeyEvent.VK_G,
			KeyEvent.VK_H,
			KeyEvent.VK_I,
			KeyEvent.VK_J,
			KeyEvent.VK_K,
			KeyEvent.VK_L,
			KeyEvent.VK_M,
			KeyEvent.VK_N,
			KeyEvent.VK_O,
			KeyEvent.VK_P,
			KeyEvent.VK_Q,
			KeyEvent.VK_R,
			KeyEvent.VK_S,
			KeyEvent.VK_T,
			KeyEvent.VK_U,
			KeyEvent.VK_V,
			KeyEvent.VK_W,
			KeyEvent.VK_X,
			KeyEvent.VK_Y,
			KeyEvent.VK_Z,
			KeyEvent.VK_OPEN_BRACKET,
			KeyEvent.VK_BACK_SLASH,
			KeyEvent.VK_CLOSE_BRACKET,
			KeyEvent.VK_NUMPAD0,
			KeyEvent.VK_NUMPAD1,
			KeyEvent.VK_NUMPAD2,
			KeyEvent.VK_NUMPAD3,
			KeyEvent.VK_NUMPAD4,
			KeyEvent.VK_NUMPAD5,
			KeyEvent.VK_NUMPAD6,
			KeyEvent.VK_NUMPAD7,
			KeyEvent.VK_NUMPAD8,
			KeyEvent.VK_NUMPAD9,
			KeyEvent.VK_MULTIPLY,
			KeyEvent.VK_ADD,
			KeyEvent.VK_SEPARATER,
			KeyEvent.VK_SEPARATOR,
			KeyEvent.VK_SUBTRACT,
			KeyEvent.VK_DECIMAL,
			KeyEvent.VK_DIVIDE,
			KeyEvent.VK_DELETE,
			KeyEvent.VK_NUM_LOCK,
			KeyEvent.VK_SCROLL_LOCK,
			KeyEvent.VK_F1,
			KeyEvent.VK_F2,
			KeyEvent.VK_F3,
			KeyEvent.VK_F4,
			KeyEvent.VK_F5,
			KeyEvent.VK_F6,
			KeyEvent.VK_F7,
			KeyEvent.VK_F8,
			KeyEvent.VK_F9,
			KeyEvent.VK_F10,
			KeyEvent.VK_F11,
			KeyEvent.VK_F12,
		};
		ArrayList<String> keys = new ArrayList<>();
		for (int keyCode : keyCodes) {
			for (int modifier : modifiers) {
				keys.add(StringUtils.toString(KeyStroke.getKeyStroke(keyCode, modifier)));
			}
		}
		String[] karr = keys.toArray(new String[keys.size()]);
		Arrays.sort(karr);
		keys.clear();
		for (String k : karr) {
			if (!keys.contains(k)) {
				keys.add(k);
			}
		}
		ArrayList<String> tokTmp = new ArrayList<>();
		for (String key : keys) {
			String[] words = StringUtils.parse(key, " ");
			for (String word : words) {
				if (!tokTmp.contains(word)) {
					tokTmp.add(word);
				}
			}
		}
		
		// Final list
		ArrayList<String> tokens = new ArrayList<>();
		
		// temp list
		ArrayList<String> tmp = new ArrayList<>();
		
		// One char.
		for (String s : tokTmp) {
			if (s.length() == 1) {
				tmp.add(s);
			}
		}
		for (String s : tmp) {
			tokTmp.remove(s);
		}
		tokens.addAll(sorted(tmp));
		tmp.clear();
		
		// F1...F9
		for (String s : tokTmp) {
			if (s.startsWith("F") && s.length() == 2) {
				tmp.add(s);
			}
		}
		for (String s : tmp) {
			tokTmp.remove(s);
		}
		tokens.addAll(sorted(tmp));
		tmp.clear();
		
		// F10...FXX
		for (String s : tokTmp) {
			if (s.startsWith("F") && s.length() == 3) {
				tmp.add(s);
			}
		}
		for (String s : tmp) {
			tokTmp.remove(s);
		}
		tokens.addAll(sorted(tmp));
		tmp.clear();
		
		// Rest
		tokens.addAll(sorted(tokTmp));
		
		
		
		// RunAction it.
		for (String tok : tokens) {
			StringBuilder b = new StringBuilder();
			b.append("<entry key=\"");
			b.append("key_");
			b.append(tok);
			b.append("\">");
			b.append(tok);
			b.append("</entry>");
			System.out.println(b.toString());
		}
		
		
	}

	private static ArrayList<String> sorted(ArrayList<String> list) {
		String[] a = list.toArray(new String[list.size()]);
		Arrays.sort(a);
		ArrayList<String> result = new ArrayList<>();
		for (String s : a) {
			result.add(s);
		}
		return result;
	}
//	Session session = (Session) action.getValue(ActionKey.Session);
//	Locale locale = (session == null ? Locale.UK : session.getLocale());
//
//	String tooltip = (String) action.getValue(ActionKey.ShortDescription);
//	KeyStroke keyStroke = (KeyStroke) action.getValue(ActionKey.AcceleratorKey);
//	if (tooltip != null && keyStroke != null) {
//		StringBuilder b = new StringBuilder();
//		b.append("<html>");
//		b.append("<center>");
//		b.append(tooltip);
//		b.append("<br>");
//		b.append("<center>");
//		b.append("&lt;");
//		b.append(translate(keyStroke, locale));
//		b.append("&gt;");
//		b.append("</html>");
//		action.putValue(ActionKey.ShortDescription, b.toString());
//	}
//
//	String text = (String) action.getValue(ActionKey.Name);
//	if (text != null && keyStroke != null) {
//		StringBuilder b = new StringBuilder();
//		b.append("<html>");
//		b.append(text);
//		b.append(" ");
//		b.append("&lt;");
//		b.append(translate(keyStroke, locale));
//		b.append("&gt;");
//		b.append("</html>");
//
//		Icon icon = (Icon) action.getValue(ActionKey.SmallIcon);
//		if (icon != null) {
//			button = new JButton(text, icon);
//		} else {
//			button = new JButton(text, icon);
//		}
//		button.setAction(action);
//	} else {
//		button = new JButton(action);
//	}

}
