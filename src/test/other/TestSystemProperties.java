package test.other;

public class TestSystemProperties {

	public static void main(String[] args) {
		System.setProperty("log4j.configurationFile", "log4j.xml");
		System.getProperties().list(System.out);
	}

}
