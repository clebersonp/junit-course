package br.ce.wcaquino.builders;

import static br.ce.wcaquino.builders.FilmeBuilder.criaUmFilme;
import static br.ce.wcaquino.builders.UsuarioBuilder.criaUmUsuario;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoBuilder {

	private Locacao locacao;
	
	public static LocacaoBuilder criaUmaLocacao() {
		LocacaoBuilder builder = new LocacaoBuilder();
		inicializarDadosPadroes(builder);
		return builder;
	}
	
	public static void inicializarDadosPadroes(LocacaoBuilder builder) {
		builder.locacao = new Locacao();
		Locacao locacao = builder.locacao;
		
		locacao.setUsuario(criaUmUsuario().agora());
		locacao.setFilmes(Arrays.asList(criaUmFilme().agora()));
		locacao.setDataLocacao(new Date());
		locacao.setDataRetorno(obterDataComDiferencaDias(1));
		locacao.setValor(4.0);
	}
	
	public LocacaoBuilder comFilmes(List<Filme> filmes) {
		this.locacao.setFilmes(filmes);
		return this;
	}
	
	public LocacaoBuilder comUsuario(Usuario usuario) {
		this.locacao.setUsuario(usuario);
		return this;
	}
	
	public LocacaoBuilder comRetorno(Date dataRetorno) {
		this.locacao.setDataRetorno(dataRetorno);
		return this;
	}
	
	public LocacaoBuilder comLocacao(Date dataLocacao) {
		this.locacao.setDataLocacao(dataLocacao);
		return this;
	}
	
	public LocacaoBuilder atrasado() {
		this.locacao.setDataLocacao(DataUtils.obterDataComDiferencaDias(-4));
		this.locacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(-2));
		return this;
	}
	
	public Locacao agora() {
		return this.locacao;
	}
	
}
