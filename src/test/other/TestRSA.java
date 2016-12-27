package test.other;

import java.math.BigInteger;
import java.util.Random;

import com.qtplaf.library.app.sec.RSA;
import com.qtplaf.library.util.NumberUtils;

public class TestRSA {

	public static void main(String[] args) {

		String base = "car-la-sas--";
		int mult = 2;
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < mult; i++) {
			b.append(base);
		}

		RSA rsa = new RSA();
		byte[] encripted = rsa.encrypt(b.toString().getBytes());
		System.out.println(new String(encripted));
		byte[] decripted = rsa.decrypt(encripted);
		System.out.println(new String(decripted));

		System.out.println();
		System.out.println();
		BigInteger p = BigInteger.probablePrime(512, new Random());
		BigInteger q = BigInteger.probablePrime(512, new Random());

		rsa.setKeyModuleAndPhi(p, q);
		encripted = rsa.encrypt(b.toString().getBytes());
		System.out.println(new String(encripted));
		decripted = rsa.decrypt(encripted);
		System.out.println(new String(decripted));

		p =
			new BigInteger(
				"11810477465018058780413589397869011296049287592642882688922194200897207642713506252556696358994504123422088231096302551047647104721926967969658108238987871");
		q =
			new BigInteger(
				"11810477465018058780413589397869011296049287592642882688922194200897207642713506252556696358994504123422088231096302551047647104721926967969658108238988279");

		System.out.println();
		System.out.println();
		rsa.setKeyModuleAndPhi(p, q);
		encripted = rsa.encrypt(b.toString().getBytes());
		System.out.println(new String(encripted));
		decripted = rsa.decrypt(encripted);
		System.out.println(new String(decripted));

		System.out.println();
		System.out.println();
		StringBuilder s = new StringBuilder();
		for (byte i : encripted) {
			s.append(NumberUtils.toHexString(i));
		}
		System.out.println(s);

		byte[] enc = new byte[encripted.length];
		int cursor = 0;
		String senc = s.toString();
		for (int i = 0; i < enc.length; i++) {
			String sb = senc.substring(cursor, cursor + 2);
			enc[i] = NumberUtils.parseByte(sb);
			cursor += 2;
		}
		byte[] dec = rsa.decrypt(enc);
		System.out.println(new String(dec));
		System.out.println();
		System.out.println();
		
		String se = rsa.encrypt(b.toString());
		System.out.println(se);
		System.out.println(rsa.decrypt(se));
		

	}

}
