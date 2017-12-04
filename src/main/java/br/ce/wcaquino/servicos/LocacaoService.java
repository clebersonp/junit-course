package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoService {
	
	private LocacaoDAO dao;
	private SPCService spcService;
	private EmailService emailService;

	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws LocadoraException, FilmeSemEstoqueException  {
		
		if (usuario == null) {
			throw new LocadoraException("Usuário vazio!");
		}
		
		if (filmes == null || filmes.isEmpty()) {
			throw new LocadoraException("Filme vazio!");
		}
		
		Filme existeFilmeSemEstoqueNaLista = filmes.stream().filter(f -> f.getEstoque().equals(Integer.valueOf(0))).findFirst().orElse(null);
		
		if (existeFilmeSemEstoqueNaLista != null) {
			throw new FilmeSemEstoqueException("Filme sem estoque!");
		}
		
		boolean ehNegativado = false;
		
		try {
			ehNegativado = this.spcService.possuiNegativacao(usuario);
		} catch (Exception e) {
			throw new LocadoraException("Erro no spc, tente novamente!");
		}
		
		if (ehNegativado) {
			throw new LocadoraException("Usuário negativado pelo SPC");
		}

		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
//		locacao.setDataLocacao(new Date()); trocado para testar o powermock para metodos staticos
		locacao.setDataLocacao(Calendar.getInstance().getTime());
		locacao.valorTotal(filmes);
		
		//Entrega no dia seguinte
		Date dataEntrega = Calendar.getInstance().getTime();
		dataEntrega = adicionarDias(dataEntrega, 1);
		if (DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = adicionarDias(dataEntrega, 1);
		}
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		//TODO adicionar método para salvar
		dao.salvar(locacao);
		
		return locacao;
	}
	
	public void notificarAtrasos() {
		List<Locacao> locacoes = dao.obterLocacoesPendentes();
		for (Locacao locacao : locacoes) {
			if (locacao.getDataRetorno().before(new Date())) {
				emailService.notificarAtraso(locacao.getUsuario());
			}
		}
	}
	
	public void prorrogarLocacao(Locacao locacao, int dias) {
		Locacao novaLocacao = new Locacao();
		novaLocacao.setUsuario(locacao.getUsuario());
		novaLocacao.setFilmes(locacao.getFilmes());
		novaLocacao.setDataLocacao(new Date());
		novaLocacao.setDataRetorno(obterDataComDiferencaDias(dias));
		novaLocacao.setValor(locacao.getValor() * dias);
		
		dao.salvar(novaLocacao);
	}
	
	// removi esses setters uma vez que não preciso mais injetar instancias mockadas. Agora o mock se vira. ver as anotaçoes na classe LocacaoServiceTest
//	public void setLocacaoDao(LocacaoDAO dao) {
//		this.dao = dao;
//	}
//	
//	public void setSPCService(SPCService spcService) {
//		this.spcService = spcService;
//	}
//	
//	public void setEmailService(EmailService emailService) {
//		this.emailService = emailService;
//	}
}