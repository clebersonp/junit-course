package br.ce.wcaquino.builders;

import java.util.Date;
import java.util.List;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;

public class LocacaoBuilder {

	private Locacao locacao;
	
	public static LocacaoBuilder criaUmaLocacao() {
		LocacaoBuilder builder = new LocacaoBuilder();
		builder.locacao = new Locacao();
		return builder;
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
	
	public Locacao agora() {
		return this.locacao;
	}
	
}
