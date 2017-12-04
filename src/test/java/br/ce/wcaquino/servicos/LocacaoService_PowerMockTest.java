package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.criaUmFilme;
import static br.ce.wcaquino.builders.UsuarioBuilder.criaUmUsuario;
import static br.ce.wcaquino.matchers.MatcherProprio.ehHoje;
import static br.ce.wcaquino.matchers.MatcherProprio.ehHojeComDiferencaDias;
import static br.ce.wcaquino.matchers.MatcherProprio.ehSegunda;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;

// PowerMock e indicado para codigos legados e muito acoplados. Pois podemos criar teste menos intrusivos, ou seja, nao precisaremos ter que fatorar todo o projeto
// O uso do PowerMockito diminui a cobertura de teste, pois precisa preparar um monte de coisas antes de executar os metodos de test
// Segundo o instrutor, já aconteceu de criar testes com 0% de cobertura por conta de usar o powermock
// Muitas fontes sujerem alterar a visibilidade dos metodos privados para protected para conseguir testa-los sem usar o powermock
// Teste com PowerMockito é mais demorado, pois o framework deve preparar todo o ambiente antes da execucao
// estou dizendo que devera ser gerenciada pelo power mock
@RunWith(PowerMockRunner.class)

// estou informando qual classe deve ser preparada para o power mock atuar
@PrepareForTest({LocacaoService.class, DataUtils.class})
public class LocacaoService_PowerMockTest {

	@InjectMocks
	private LocacaoService servicePowerMockSpy;
	
	@Mock // injetando os mocks
	private LocacaoDAO dao;
	@Mock
	private SPCService spcService;
	@Mock
	private EmailService emailService; 

	@Before
	public void init() {

		// estou dizendo que é para iniciar os mock que tiverem com anotaçao nesta classe de teste
		MockitoAnnotations.initMocks(this);
//		não preciso mais de passar as instancias mockadas para os setters. Ver classe LocacaoService
//		this.service = new LocacaoService();
//		dao = Mockito.mock(LocacaoDAO.class);
//		this.service.setLocacaoDao(dao);
//		spcService = Mockito.mock(SPCService.class);
//		this.service.setSPCService(spcService);
//		emailService = Mockito.mock(EmailService.class);
//		this.service.setEmailService(emailService);
		
		// iniciando servicePowerMockSpy com powermockito
		servicePowerMockSpy = PowerMockito.spy(servicePowerMockSpy);
	}
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void deveAlugarUmFilme() throws Exception {
//		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));    
	    // alterar o comportamento do new Date() atraves do power mock;
		
	    // cenario

	    // mockando uma data sendo um dia da semana, menos sabado
	    PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(28, 4, 2017));
	    
	    // E se eu precisar criar um comportamento para metodos staticos?
	    // vou trocar o new Date() do servico de alugar filme para Calendar.getInstance().getTime();
	    // O PowerMock tbm consegue alterar comportamentos de metodos staticos
//	    Calendar calendar = Calendar.getInstance();
//	    calendar.set(Calendar.DAY_OF_MONTH, 28);
//	    calendar.set(Calendar.MONTH, Calendar.APRIL);
//	    calendar.set(Calendar.YEAR, 2017);
//	    PowerMockito.mockStatic(Calendar.class);
//	    PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
	    
		Usuario usuario = criaUmUsuario().agora();
		List<Filme> filmes = Arrays.asList(criaUmFilme().agora());
		

		// acao
		Locacao locacao;
		locacao = servicePowerMockSpy.alugarFilme(usuario, filmes);

		// verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
//		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
//		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
		
		// por causa do matcherproprio, que chama metodos do DataUtils que tem construtores default,
		// preciso tambem preparar a classe datautils para funcionar corretamente com o power mock
		// Existem duas formar de solucionar o problema, Primeira: criando assertivas com as datas fixa para 28/04/2017
		// e 29/04/2017, a Segunda: é preparando a classe DataUtils com o powermock
		error.checkThat(locacao.getDataLocacao(), ehHoje());
		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
		
		// para verificar se um construtor do Date() foi chamado usando powermock
		// cuidado com essa abordagem, utilizar somente se precisar, pois qualquer alteracao no codigo do servico,
		// vai quebrar os testes
		// usando PowerMockito com junit e Mockito
		PowerMockito.verifyNew(Date.class, Mockito.times(4)).withNoArguments();
		
		
		// A validacao para powermock com chamadas staticas é um pouco diferente
//		PowerMockito.verifyStatic(Mockito.times(8)); // 8 vezes, pois utiliza o LocadoraService e no DataUtils
//		Calendar.getInstance();
	}
	
	@Test
	public void deveDevolverFilmesNaSegundaFeiraAoAlugarNoSabado() throws Exception {
//		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
	    // alterar o comportamento do new Date() atraves do power mock;
	    
		//cenario
		Usuario usuario = criaUmUsuario().agora();
		List<Filme> filmes = Arrays.asList(criaUmFilme().comEstoque(1).agora());

		// mockando uma data sendo sabado
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29, 4, 2017));
		
		// testando o powermock com metodos staticos
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.DAY_OF_MONTH, 29);
//		calendar.set(Calendar.MONTH, Calendar.APRIL);
//		calendar.set(Calendar.YEAR, 2017);
//		PowerMockito.mockStatic(Calendar.class);
//		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
		
		//acao
		Locacao locacao = servicePowerMockSpy.alugarFilme(usuario, filmes);
		
		//validacao
//		boolean ehSegunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
//		assertTrue(ehSegunda);
//		assertThat(locacao.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
//		assertThat(locacao.getDataRetorno(), caiEm(Calendar.MONDAY));
		assertThat(locacao.getDataRetorno(), ehSegunda());
		
		// o construtor default Date() sera executado 2 vezes dentro do servico de alugar um filme;
		PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
		
		// verificando a chamado de metodo statico com powermockito
//		PowerMockito.verifyStatic(Mockito.times(6)); // 6 vezes pois chama o Calendar.getInstance() no servico de alugar fime e no DataUtils
//		Calendar.getInstance();
	}
	
	// mockando metodo privado com powermockito spy
	@Test
	public void deveCalcularValorTotalAluguel_AlterandoComportamento() throws Exception {
	    // cenario
	    Usuario usuario = UsuarioBuilder.criaUmUsuario().agora();
	    List<Filme> filmes = Arrays.asList(FilmeBuilder.criaUmFilme().agora()); // cria um filme com o valor 5.0
	    
	    // mockando metodo privado
	    PowerMockito.doReturn(1.0).when(servicePowerMockSpy, "calcularValorTotal", filmes);
	    
	    // acao
	    Locacao locacao = servicePowerMockSpy.alugarFilme(usuario, filmes);
	    
	    // verificacao
	    assertThat(locacao.getValor(), is(1.0));
	    
	    // verificar se o powermockito invocou um metodo privado passando parametros
	    PowerMockito.verifyPrivate(servicePowerMockSpy, Mockito.times(1)).invoke("calcularValorTotal", filmes);
	}
	
	// mockando metodo privado diretamento com WhiteBox do powermockito
	@Test
	public void deveCalcularValorAluguel() throws Exception {
	    // cenario
	    List<Filme> filmes = Arrays.asList(FilmeBuilder.criaUmFilme().agora());
	    
	    // mockando diretamento metodo privado
	    // acao
	    Double valorTotal = (Double) Whitebox.invokeMethod(servicePowerMockSpy, "calcularValorTotal", filmes);
	    
	    // verificacao
	    assertThat(valorTotal, is(equalTo(5.0)));
	}
}