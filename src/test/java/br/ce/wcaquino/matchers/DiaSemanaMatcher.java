package br.ce.wcaquino.matchers;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.ce.wcaquino.utils.DataUtils;

public class DiaSemanaMatcher extends TypeSafeMatcher<Date> {

	Integer diaDaSemana;
	
	public DiaSemanaMatcher(Integer diaDaSemana) {
		this.diaDaSemana = diaDaSemana;
	}
	
	@Override
	public void describeTo(Description description) {
		Calendar date = Calendar.getInstance();
		date.set(Calendar.DAY_OF_WEEK, diaDaSemana);
		String diaSemana = date.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("pt", "BR"));
		description.appendText(diaSemana);
	}

	@Override
	protected boolean matchesSafely(Date data) {
		return DataUtils.verificarDiaSemana(data, diaDaSemana);
	}

}
