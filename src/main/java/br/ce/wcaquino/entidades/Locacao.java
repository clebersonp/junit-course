package br.ce.wcaquino.entidades;

import java.util.Date;
import java.util.List;

public class Locacao {

	private Usuario usuario;
	private List<Filme> filmes;
	private Date dataLocacao;
	private Date dataRetorno;
	private Double valor = 0d;
	
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public Date getDataLocacao() {
		return dataLocacao;
	}
	public void setDataLocacao(Date dataLocacao) {
		this.dataLocacao = dataLocacao;
	}
	public Date getDataRetorno() {
		return dataRetorno;
	}
	public void setDataRetorno(Date dataRetorno) {
		this.dataRetorno = dataRetorno;
	}
	public Double getValor() {
		return valor;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}
	public List<Filme> getFilmes() {
		return filmes;
	}
	public void setFilmes(List<Filme> filmes) {
		this.filmes = filmes;
	}
	public void valorTotal(List<Filme> filmes) {
		if (filmes != null && !filmes.isEmpty()) {
			for (int i = 0; i < filmes.size(); i++) {
				double valorDoFilme = 0d;
				switch(i) {
					case 2: valorDoFilme = filmes.get(i).getPrecoLocacao() * 0.75; break;
					case 3: valorDoFilme = filmes.get(i).getPrecoLocacao() * 0.50; break;
					case 4: valorDoFilme = filmes.get(i).getPrecoLocacao() * 0.25; break;
					case 5: valorDoFilme = 0d; break;
					default : valorDoFilme = filmes.get(i).getPrecoLocacao(); break;
				}
				this.valor += valorDoFilme;
			}
		}
	}
}