package test.other;

import com.qtplaf.library.app.Argument;
import com.qtplaf.library.app.ArgumentManager;

public class TestCommandLineArgs {

	public static void main(String[] args) {
		Argument arg1 = new Argument("A", "Argument A", true, true, false);
		ArgumentManager argMngr = new ArgumentManager();
		argMngr.add(arg1);
		argMngr.parse(args);
		System.out.println(argMngr.getErrors());
		System.out.println(argMngr.getValue("A"));
	}

}
