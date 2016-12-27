package test.other;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Currency;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public class TestCurrency {

	public static void main(String[] args) {
		Locale us = Locale.US;
		Set<Currency> currencies = Currency.getAvailableCurrencies();
		Currency[] arr = currencies.toArray(new Currency[currencies.size()]);
		Arrays.sort(arr, new CurrencyComparator());
		currencies.clear();
		currencies = new LinkedHashSet<>();
		for (Currency a : arr) {
			currencies.add(a);
		}
		for (Currency currency : currencies) {
			System.out.println(currency.getCurrencyCode() + " - " + currency.getDisplayName(us));
		}
	}

	public static class CurrencyComparator implements Comparator<Currency> {
		public int compare(Currency c1, Currency c2) {
			return c1.getCurrencyCode().compareTo(c2.getCurrencyCode());
		}

	}

}
