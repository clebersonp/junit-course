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
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {

	private LocacaoService service;
	private LocacaoDAO dao;
	private SPCService spcService;
	private EmailService emailService; 

	@Before
	public void init() {
		this.service = new LocacaoService();
		dao = Mockito.mock(LocacaoDAO.class);
		this.service.setLocacaoDao(dao);
		spcService = Mockito.mock(SPCService.class);
		this.service.setSPCService(spcService);
		emailService = Mockito.mock(EmailService.class);
		this.service.setEmailService(emailService);
	}
	
	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException exception = ExpectedException.none();


	@Test
	public void deveAlugarUmFilme() throws Exception {
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		// cenario
		Usuario usuario = criaUmUsuario().agora();
		List<Filme> filmes = Arrays.asList(criaUmFilme().agora());

		// acao
		Locacao locacao;
		locacao = service.alugarFilme(usuario, filmes);

		// verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
//		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(locacao.getDataLocacao(), ehHoje());
//		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
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
	public void deveDevolverFilmesNaSegundaFeiraAoAlugarNoSabado() throws LocadoraException, FilmeSemEstoqueException {
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		//cenario
		Usuario usuario = criaUmUsuario().agora();
		List<Filme> filmes = Arrays.asList(criaUmFilme().comEstoque(1).agora());
		
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
	public void naoDeveAlugarFilmeParaUsuarioNegativadoPeloSPC() throws FilmeSemEstoqueException {
		//cenario
		Usuario usuario = criaUmUsuario().agora();
		List<Filme> filmes = Arrays.asList(criaUmFilme().comEstoque(2).agora());

		// alterar o comportamento default de um metodo para o mock
		when(spcService.possuiNegativacao(usuario)).thenReturn(Boolean.TRUE);
		
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
		List<Locacao> locacoes = 
				Arrays.asList(
						criaUmaLocacao()
						.comUsuario(usuario)
						.comRetorno(DataUtils.obterDataComDiferencaDias(-3))
						.agora());
		
		// como ler: quando eu chamar o metodo obterLocacoesPendentes entao retorna a lista de locacao
		when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		//acao
		service.notificarAtrasos();
		
		//verificacao
		// Como ler: verifica se o metodo chamado recebeu o usuario especifico
		Mockito.verify(emailService).notificarAtraso(usuario);
	}
}

























