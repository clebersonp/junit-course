package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import br.ce.wcaquino.runners.ParaleloRunner;

@RunWith(ParaleloRunner.class)
public class CalculadoraMockTest {

	@Mock
	private Calculadora calcMock;
	@Spy
	private Calculadora calcSpy;
	
	// @Spy só funciona para classes concretas
	private EmailService email;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void deveMostrarDiferencaEntreMockSpy() {
		// criando expectativas para alterar o comportamento defaul do mock
		Mockito.when(calcMock.somar(1, 2)).thenReturn(5);

		// alterando o comportamento default do mock quando ele não souber o que fazer
//		Mockito.when(calcMock.somar(1, 2)).thenCallRealMethod();
		
		// dessa maneira, por causa dos precendentes, o metodo sera executado
//		Mockito.when(calcSpy.somar(1, 2)).thenReturn(5);
		
		// alterando a precendencia do spy, para não executa o metodo real, para levar em concideracao a expectativa definida logo acima
		Mockito.doReturn(5).when(calcSpy).somar(1, 2);
		
		// alterando a execucao de um metodo com o spy
		Mockito.doNothing().when(calcSpy).imprime();
		
		// se não definir a expectativa para o mock, sera executado o valor default do metodo
		System.out.println("Mock: " + calcMock.somar(1, 2));
		
		// o comportamento defaul para spy é executar o metodo real.
		// quando definido uma expectativa para o spy, e alterar os valores para deixar o spy perdido,
		// entao o spy executara o metodo real
		System.out.println("Spy: " + calcSpy.somar(1, 2));
		
		System.out.println("Mock ->");
		calcMock.imprime();
		System.out.println("Spy ->");
		calcSpy.imprime();
		
		
	}
	
	@Test
	public void testeSomaComMock() {
		Calculadora calc = Mockito.mock(Calculadora.class);
		ArgumentCaptor<Integer> argCapt = ArgumentCaptor.forClass(Integer.class);
		Mockito.when(calc.somar(argCapt.capture(), argCapt.capture())).thenReturn(5);
		
		Assert.assertEquals(5, calc.somar(10000, -234));
//		System.out.println(argCapt.getAllValues());
		
	}
	
	// Diferente do Mock, quando o spy não sabe qual valor executar, ele executa o metodo real.
	// Enquanto o mock executa o metodo com valores de inicializacao default;
	// Embora os dois tenham esse comportamento, conseguimos altera-los de ambos.
	
}
