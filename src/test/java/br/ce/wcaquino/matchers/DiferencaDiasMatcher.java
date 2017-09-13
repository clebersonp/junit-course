package br.ce.wcaquino.matchers;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.ce.wcaquino.utils.DataUtils;

public class DiferencaDiasMatcher extends TypeSafeMatcher<Date> {

	private Integer diferencaDias;

	public DiferencaDiasMatcher(Integer diferencaDias) {
		this.diferencaDias = diferencaDias;
	}
	
	@Override
	public void describeTo(Description description) {
		Date data = DataUtils.obterDataComDiferencaDias(diferencaDias);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		String displayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("pt", "BR"));
		description.appendText(displayName);
	}

	@Override
	protected boolean matchesSafely(Date data) {
		return DataUtils.isMesmaData(data, DataUtils.obterDataComDiferencaDias(diferencaDias));
	}

}
