package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.criaUmFilme;
import static br.ce.wcaquino.builders.UsuarioBuilder.criaUmUsuario;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoParametrizadoTest {

	@InjectMocks
	private LocacaoService service;
	
	@Mock
	private LocacaoDAO dao;
	@Mock
	private SPCService spcService;
	
	@Parameter(value = 0)
	public Usuario usuario;
	
	@Parameter(value = 1)
	public List<Filme> filmes;
	
	@Parameter(value = 2)
	public Double valorLocacao;
	
	@Parameter(value = 3)
	public String cenario;
	
	private static Usuario usuario1 = criaUmUsuario().agora();
	private static Filme filme1 = criaUmFilme().comPreco(4.0).agora();
	private static Filme filme2 = criaUmFilme().comPreco(4.0).agora();
	private static Filme filme3 = criaUmFilme().comPreco(4.0).agora();
	private static Filme filme4 = criaUmFilme().comPreco(4.0).agora();
	private static Filme filme5 = criaUmFilme().comPreco(4.0).agora();
	private static Filme filme6 = criaUmFilme().comPreco(4.0).agora();
	private static Filme filme7 = criaUmFilme().comPreco(4.0).agora();
	private static Filme filme8 = criaUmFilme().comPreco(4.0).agora();
	
	@Before
	public void init() {

		MockitoAnnotations.initMocks(this);
// 		ver na classe LocacaoServiceTest o motivo de comentar esse setters
//		this.service = new LocacaoService();
//		LocacaoDAO dao = Mockito.mock(LocacaoDAO.class);
//		this.service.setLocacaoDao(dao);
//		SPCService spcService = Mockito.mock(SPCService.class);
//		this.service.setSPCService(spcService);
	}
	
	@Parameters(name = "{3}")
	public static Collection<Object[]> getParametros() {
		return Arrays.asList(new Object[][] {
			{usuario1, Arrays.asList(filme1, filme2, filme3), 11.0, "Filme 3 com 25% de desconto"},
			{usuario1, Arrays.asList(filme1, filme2, filme3, filme4), 13.0, "Filme 4 com 50% de desconto"},
			{usuario1, Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14.0, "Filme 5 com 75% de desconto"},
			{usuario1, Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.0, "Filme 6 com 100% de desconto"},
			{usuario1, Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6, filme7), 18.0, "Filme 7 sem desconto"},
			{usuario1, Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6, filme7, filme8), 22.0, "Filme 8 sem desconto"},
		});
	}
	
	@Test
	public void deveAplicarDescontoNaAlocacaoDeFilmes() throws LocadoraException, FilmeSemEstoqueException {
		//cenario ficou parametrizado

		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//validacao
		assertThat(locacao.getValor(), is(valorLocacao));
	}
}