package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.criaUmFilme;
import static br.ce.wcaquino.builders.FilmeBuilder.criaUmFilmeSemEstoque;
import static br.ce.wcaquino.builders.LocacaoBuilder.criaUmaLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.criaUmUsuario;
import static br.ce.wcaquino.matchers.MatcherProprio.ehHoje;
import static br.ce.wcaquino.matchers.MatcherProprio.ehHojeComDiferencaDias;
import static br.ce.wcaquino.matchers.MatcherProprio.ehSegunda;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.runners.ParaleloRunner;
import br.ce.wcaquino.utils.DataUtils;

// @RunWith(JUnit4.class) runner default do Junit4

//criando classe para rodar os metodos de teste em paralelo, mas as classes serao executadas em serie
// Essa abordagem não da para ser feita quando uma classe de teste já estiver um RunWith() definido
@RunWith(ParaleloRunner.class)
public class LocacaoServiceTest {

	@InjectMocks // falando em qual classe que sera testada que devera injetar os mock
	@Spy // para controlar o comportamento de metodos da classe de servico, por default os metodos serao executados
	private LocacaoService service;
	
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
	    Date dataAtual = DataUtils.obterData(28, 4, 2017);
        Mockito.doReturn(dataAtual).when(service).obterDataAtual();
		Usuario usuario = criaUmUsuario().agora();
		List<Filme> filmes = Arrays.asList(criaUmFilme().agora());

		// acao
		Locacao locacao;
		locacao = service.alugarFilme(usuario, filmes);

		// verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
//		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
//		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
		
		// por causa do matcherproprio, que chama metodos do DataUtils que tem construtores default,
		// preciso tambem preparar a classe datautils para funcionar corretamente com o power mock
		// Existem duas formar de solucionar o problema, Primeira: criando assertivas com as datas fixa para 28/04/2017
		// e 29/04/2017, a Segunda: é preparando a classe DataUtils com o powermock
//		error.checkThat(locacao.getDataLocacao(), ehHoje());
//		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), dataAtual), is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.adicionarDias(dataAtual, 1)), is(true));
	}

	@Test(expected = FilmeSemEstoqueException.class)
	public void naoDeveAlugarFilmeSemEstoque_Elegante() throws LocadoraException, FilmeSemEstoqueException {
		// cenario
		Usuario usuario = criaUmUsuario().agora();
		List<Filme> filmes = Arrays.asList(criaUmFilmeSemEstoque().agora());

		// acao
		service.alugarFilme(usuario, filmes);
	}

	@Test
	public void naoDeveAceitarUsuarioVazio() throws FilmeSemEstoqueException {
		// cenario
		Usuario usuario = criaUmUsuario().vazio().agora();
		List<Filme> filmes = Arrays.asList(criaUmFilme().agora());

		// acao
		try {
			service.alugarFilme(usuario, filmes);
			fail("Deveria ter lancado uma exceção de usuario vazio!");
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuário vazio!"));
		}
	}

	@Test
	public void naoDeveAceitarFilmeVazio() throws FilmeSemEstoqueException, LocadoraException {
		// cenario
		Usuario usuario = criaUmUsuario().agora();
		List<Filme> filmes = new ArrayList<>();

		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio!");
		// acao
		service.alugarFilme(usuario, filmes);
	}
	
	// testes parametrizados na classe CalculoValorLocacaoParametrizadoTest
//	@Test
//	public void deveAplicar25PctDescontoNoFilme3() throws LocadoraException, FilmeSemEstoqueException {
//		//cenario
//		Usuario usuario = new Usuario("Usuario 1");
//		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), new Filme("Filme 3", 2, 4.0), new Filme("Filme 3", 2, 4.0));
//		
//		//acao
//		Locacao locacao = service.alugarFilme(usuario, filmes);
//		
//		//validacao
//		assertThat(locacao.getValor(), is(11.0));
//	}
//
//	@Test
//	public void deveAplicar50PctDescontoNoFilme4() throws LocadoraException, FilmeSemEstoqueException {
//		//cenario
//		Usuario usuario = new Usuario("Usuario 1");
//		List<Filme> filmes = Arrays.asList(
//				new Filme("Filme 1", 2, 4.0), new Filme("Filme 3", 2, 4.0),
//				new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0));
//		
//		//acao
//		Locacao locacao = service.alugarFilme(usuario, filmes);
//		
//		//validacao
//		assertThat(locacao.getValor(), is(13.0));
//	}
//
//	@Test
//	public void deveAplicar75PctDescontoNoFilme5() throws LocadoraException, FilmeSemEstoqueException {
//		//cenario
//		Usuario usuario = new Usuario("Usuario 1");
//		List<Filme> filmes = Arrays.asList(
//				new Filme("Filme 1", 2, 4.0), new Filme("Filme 3", 2, 4.0),
//				new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0),
//				new Filme("Filme 5", 2, 4.0));
//		
//		//acao
//		Locacao locacao = service.alugarFilme(usuario, filmes);
//		
//		//validacao
//		assertThat(locacao.getValor(), is(14.0));
//	}
//	
//	@Test
//	public void deveAplicar100PctDescontoNoFilme6() throws LocadoraException, FilmeSemEstoqueException {
//		//cenario
//		Usuario usuario = new Usuario("Usuario 1");
//		List<Filme> filmes = Arrays.asList(
//				new Filme("Filme 1", 2, 4.0), new Filme("Filme 3", 2, 4.0),
//				new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0), 
//				new Filme("Filme 5", 2, 4.0), new Filme("Filme 6", 2, 4.0));
//		
//		//acao
//		Locacao locacao = service.alugarFilme(usuario, filmes);
//		
//		//validacao
//		assertThat(locacao.getValor(), is(14.0));
//	}
//	
//	@Test
//	public void naodeveAplicarDescontoNoFilme7() throws LocadoraException, FilmeSemEstoqueException {
//		//cenario
//		Usuario usuario = new Usuario("Usuario 1");
//		List<Filme> filmes = Arrays.asList(
//				new Filme("Filme 1", 2, 4.0), new Filme("Filme 3", 2, 4.0),
//				new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0), 
//				new Filme("Filme 5", 2, 4.0), new Filme("Filme 6", 2, 4.0),
//				new Filme("Filme 7", 2, 4.0));
//		
//		//acao
//		Locacao locacao = service.alugarFilme(usuario, filmes);
//		
//		//validacao
//		assertThat(locacao.getValor(), is(18.0));
//	}
//	
//	@Test
//	public void naodeveAplicarDescontoNoFilme8() throws LocadoraException, FilmeSemEstoqueException {
//		//cenario
//		Usuario usuario = new Usuario("Usuario 1");
//		List<Filme> filmes = Arrays.asList(
//				new Filme("Filme 1", 2, 4.0), new Filme("Filme 3", 2, 4.0),
//				new Filme("Filme 3", 2, 4.0), new Filme("Filme 4", 2, 4.0), 
//				new Filme("Filme 5", 2, 4.0), new Filme("Filme 6", 2, 4.0),
//				new Filme("Filme 7", 2, 4.0), new Filme("Filme 8", 2, 4.0));
//		
//		//acao
//		Locacao locacao = service.alugarFilme(usuario, filmes);
//		
//		//validacao
//		assertThat(locacao.getValor(), is(22.0));
//	}
	
	@Test
	public void deveDevolverFilmesNaSegundaFeiraAoAlugarNoSabado() throws Exception {
//		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		//cenario
		Usuario usuario = criaUmUsuario().agora();
		List<Filme> filmes = Arrays.asList(criaUmFilme().comEstoque(1).agora());
		Mockito.doReturn(DataUtils.obterData(29, 4, 2017)).when(service).obterDataAtual();
		// ao inves de usar o metodo obterDataAtual, poderia criar um classe de servico, ex: TimeService.obterDataAtual();

		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//validacao
//		boolean ehSegunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
//		assertTrue(ehSegunda);
//		assertThat(locacao.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
//		assertThat(locacao.getDataRetorno(), caiEm(Calendar.MONDAY));
		assertThat(locacao.getDataRetorno(), ehSegunda());
		
	}
	
	@Test
	public void naoDeveAlugarFilmeParaUsuarioNegativadoPeloSPC() throws Exception {
		//cenario
		Usuario usuario = criaUmUsuario().agora();
		List<Filme> filmes = Arrays.asList(criaUmFilme().comEstoque(2).agora());

		// alterar o comportamento default de um metodo para o mock
//		when(spcService.possuiNegativacao(usuario)).thenReturn(Boolean.TRUE);
		
		// usando matcher do mockito
		when(spcService.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(Boolean.TRUE);
		
		//acao
		try {
			service.alugarFilme(usuario, filmes);
			
			//verificacao
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usuário negativado pelo SPC"));
		}
		
		// verifica se o metodo possuiNegaticacao foi chamado
		Mockito.verify(spcService).possuiNegativacao(usuario);
	}
	
	@Test 
	public void deveEncaminharEmailParaLocacoesComAtraso() {
		//cenario
		Usuario usuario = criaUmUsuario().agora();
		Usuario usuario2 = criaUmUsuario().comNome("Usuario 2").agora();
		Usuario usuario3 = criaUmUsuario().comNome("Usuario 3").agora();
		
		List<Locacao> locacoes = 
				Arrays.asList(criaUmaLocacao().atrasado().comUsuario(usuario).agora(),
						criaUmaLocacao().comUsuario(usuario2).agora(),
						criaUmaLocacao().comRetorno(DataUtils.obterDataComDiferencaDias(-4)).comUsuario(usuario3).agora(),
						criaUmaLocacao().comRetorno(DataUtils.obterDataComDiferencaDias(-12)).comUsuario(usuario3).agora());
		
		// como ler: quando eu chamar o metodo obterLocacoesPendentes entao retorna a lista de locacao
		when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		//acao
		service.notificarAtrasos();
		
		//verificacao
		// Como ler: verifica se o metodo chamado recebeu o usuario especifico
		// Matcher generico de usuario. 
		// Se o metodo tiver mais de um parametro e eu utilizar um Matcher do mockito, 
		// eu terei que usar Matcher em todos os parametros, o mockito não deixa mesclar 
		//  entre valor fixos e Matchers. Ver o teste da classe CalculadoraMockTest
		
		Mockito.verify(emailService, Mockito.times(3)).notificarAtraso(Mockito.any(Usuario.class)); 
		Mockito.verify(emailService).notificarAtraso(usuario);
		Mockito.verify(emailService, Mockito.atLeastOnce()).notificarAtraso(usuario3);
		Mockito.verify(emailService, Mockito.never()).notificarAtraso(usuario2);
		Mockito.verifyNoMoreInteractions(emailService);
	}
	
	@Test
	public void deveTratarErroNoSPC() throws Exception {
		// cenario
		Usuario usuario = criaUmUsuario().agora();
		List<Filme> filmes = Arrays.asList(criaUmFilme().agora());
		
		when(spcService.possuiNegativacao(usuario)).thenThrow(new Exception("Erro catastrófico!"));
		
		//validacao
		exception.expect(LocadoraException.class);
		exception.expectMessage("Erro no spc, tente novamente!");
		
		//acao
		service.alugarFilme(usuario, filmes);
	}
	
	// testar metodo void com ArgumentCaptor 
	@Test
	public void deveProrrogarLocacao() {
		// cenario
		Locacao locacao = criaUmaLocacao().agora();
		int qtdDias = 3;
		
		// acao
		service.prorrogarLocacao(locacao, qtdDias);
		
		// verificacao
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		verify(dao).salvar(argCapt.capture());
		Locacao locacaoRetornada = argCapt.getValue();
		
		// se eu usar mais de um assertThat comum e o primeiro falhar, os demais não serão validados,
		// para isso usamos errorCollector, pois ele executa todos e mostra os erros
		
		error.checkThat(locacaoRetornada.getValor(), is(12.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), is(ehHoje()));
		error.checkThat(locacaoRetornada.getDataRetorno(), is(ehHojeComDiferencaDias(3)));
	
	}
	
	@Test
	public void deveCalcularValorAluguel() throws Exception {
	    // cenario
	    List<Filme> filmes = Arrays.asList(FilmeBuilder.criaUmFilme().agora());
	    
	    // acao
	    // usando api java reflection do java para invocar metodos
	    // e util quando o metodo privado e muito complexo e queremos dar um enfoque maior
	    // lembrando que, se o metodo for tao complexo assim, talvez nao seria melhor
	    // criar uma classe especifica, assim aumentaria a coesao das classes
	    Class<LocacaoService> clazz = LocacaoService.class;
	    Method method = clazz.getDeclaredMethod("calcularValorTotal", List.class);
	    method.setAccessible(true);
	    
	    Double valorTotal = (Double) method.invoke(service, filmes); 
	    
	    // verificacao
	    assertThat(valorTotal, is(equalTo(5.0)));
	}
}

























