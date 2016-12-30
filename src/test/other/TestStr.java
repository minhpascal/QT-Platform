package test.other;

public class TestStr {

	static enum Things {
		Car,
		House,
		Fork,
		Star
	}
	
	public static void main(String[] args) {
		Things[] things = Things.values();
		for (Things thing : things) {
			System.out.println(thing.name());
		}

	}

}
