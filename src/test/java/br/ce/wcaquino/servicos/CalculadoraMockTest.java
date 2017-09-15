package br.ce.wcaquino.servicos;

import static org.hamcrest.CoreMatchers.is;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class CalculadoraMockTest {

	@Test
	public void testeSomaComMock() {
		Calculadora calc = Mockito.mock(Calculadora.class);
		
		Mockito.when(calc.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(5);
		
		Assert.assertThat(calc.somar(1, 10008), is(5));
		
	}
	
}
