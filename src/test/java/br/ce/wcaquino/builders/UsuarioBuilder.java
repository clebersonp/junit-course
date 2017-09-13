package br.ce.wcaquino.builders;

import br.ce.wcaquino.entidades.Usuario;

public class UsuarioBuilder {

	private Usuario usuario;
	
	private UsuarioBuilder() {
		
	}
	
	public static UsuarioBuilder criaUmUsuario() {
		UsuarioBuilder builder = new UsuarioBuilder();
		builder.usuario = new Usuario("Usuario 1");
		return builder;
	}
	
	public UsuarioBuilder vazio() {
		this.usuario = null;
		return this;
	}
	
	public Usuario agora() {
		return this.usuario;
	}

	public UsuarioBuilder comNome(String outroNome) {
		usuario.setNome(outroNome);
		return this;
	}
	
}
