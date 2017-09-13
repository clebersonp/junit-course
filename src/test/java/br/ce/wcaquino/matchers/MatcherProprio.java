package br.ce.wcaquino.matchers;

import java.util.Calendar;

public class MatcherProprio {

	private MatcherProprio() {
		
	}
	
	public static DiaSemanaMatcher caiEm(Integer diaDaSemana) {
		return new DiaSemanaMatcher(diaDaSemana);
	}

	public static DiaSemanaMatcher ehSegunda() {
		return new DiaSemanaMatcher(Calendar.MONDAY);
	}
	
	public static DiferencaDiasMatcher ehHojeComDiferencaDias(Integer diferencaDias) {
		return new DiferencaDiasMatcher(diferencaDias);
	}
	
	public static DiferencaDiasMatcher ehHoje() {
		return new DiferencaDiasMatcher(0);
	}
}
