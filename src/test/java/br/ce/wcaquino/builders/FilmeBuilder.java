package br.ce.wcaquino.builders;

import br.ce.wcaquino.entidades.Filme;

public class FilmeBuilder {

	private Filme filme;
	
	private FilmeBuilder() {
		
	}
	
	public static FilmeBuilder criaUmFilme() {
		FilmeBuilder builder = new FilmeBuilder();
		builder.filme = new Filme("Filme", 2, 5.0);
		return builder;
	}
	
	public static FilmeBuilder criaUmFilmeSemEstoque() {
		FilmeBuilder builder = FilmeBuilder.criaUmFilme();
		builder.filme.setEstoque(0);
		return builder;
	}

	public FilmeBuilder comEstoque(int qtdEstoque) {
		this.filme.setEstoque(qtdEstoque);
		return this;
	}
	
	public FilmeBuilder comPreco(double preco) {
		this.filme.setPrecoLocacao(preco);
		return this;
	}

	public Filme agora() {
		return this.filme;
	}
}
