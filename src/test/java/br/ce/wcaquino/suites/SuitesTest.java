package br.ce.wcaquino.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import br.ce.wcaquino.servicos.CalculoValorLocacaoParametrizadoTest;
import br.ce.wcaquino.servicos.LocacaoServiceTest;

@RunWith(Suite.class)
@SuiteClasses({
	CalculoValorLocacaoParametrizadoTest.class,
	LocacaoServiceTest.class,
})
public class SuitesTest {

}
