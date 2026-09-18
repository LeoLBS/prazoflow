package br.com.leperber.prazoflow.service;

import br.com.leperber.prazoflow.entity.StatusPadrao;
import br.com.leperber.prazoflow.entity.Usuario;
import br.com.leperber.prazoflow.exception.UsuarioNaoEncontradoException;
import br.com.leperber.prazoflow.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario cadastrar(Usuario usuario) {
        if (!StringUtils.hasText(usuario.getUsuario())) {
            throw new IllegalArgumentException("O nome de usuário não pode ser vazio ou nulo!");
        }
        if (!StringUtils.hasText(usuario.getSenha())) {
            throw new IllegalArgumentException("A senha não pode ser vazia ou nula!");
        }
        if (usuarioRepository.existsByUsuario(usuario.getUsuario())) {
            throw new IllegalArgumentException("Esse nome de usuário já está em uso!");
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        return usuarioRepository.save(usuario);
    }

    public Usuario buscarId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuario não encontrado com o ID: " + id));
    }

    public Usuario buscarPorUsuario(String usuario) {
        return usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Nenhum usuario foi localizado com o login: " + usuario));
    }

    public Usuario alterarSenha(Long id, String senhaAtual, String novaSenha) {
        if (!StringUtils.hasText(novaSenha)) {
            throw new IllegalArgumentException("A nova senha não pode ser vazia ou nula!");
        }

        Usuario usuarioEncontrado = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuario não encontrado com o ID: " + id));

        if (!passwordEncoder.matches(senhaAtual, usuarioEncontrado.getSenha())) {
            throw new IllegalArgumentException("A senha atual informada está incorreta!");
        }

        usuarioEncontrado.setSenha(passwordEncoder.encode(novaSenha));

        return usuarioRepository.save(usuarioEncontrado);
    }

    public Usuario alterarStatus(Long id, StatusPadrao statusPadrao) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuario não encontrado com o ID: " + id));

        if (usuario.getStatus().equals(statusPadrao)) {
            throw new IllegalArgumentException("O status do usuario já se encontra como " + statusPadrao);
        }

        usuario.setStatus(statusPadrao);

        return usuarioRepository.save(usuario);
    }
}