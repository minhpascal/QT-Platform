package test.other;

import com.qtplaf.library.app.Argument;
import com.qtplaf.library.app.ArgumentManager;

public class TestCommandLineArgs {

	public static void main(String[] args) {
		Argument arg1 = new Argument("A", "Argument A", true, true, true);
		arg1.addPossibleValue("XX1");
		arg1.addPossibleValue("XX2");
		arg1.addPossibleValue("XX3");
		Argument arg2 = new Argument("B", "Argument B", true, false, false);
		ArgumentManager argMngr = new ArgumentManager();
		argMngr.add(arg1);
		argMngr.add(arg2);
		argMngr.parse(args);
		System.out.println(argMngr.getErrors());
		System.out.println(argMngr.getValues("A"));
		System.out.println();
		System.out.println(arg1);
		System.out.println();
		System.out.println(arg2);
	}

}
