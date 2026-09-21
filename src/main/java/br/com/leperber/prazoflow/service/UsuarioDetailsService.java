package br.com.leperber.prazoflow.service;

import br.com.leperber.prazoflow.entity.StatusPadrao;
import br.com.leperber.prazoflow.entity.Usuario;
import br.com.leperber.prazoflow.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario não encontrado com o login: " + username));

        return User.builder()
                .username(usuario.getUsuario())
                .password(usuario.getSenha())
                .disabled(usuario.getStatus() == StatusPadrao.INATIVO)
                .roles("ADMIN")
                .build();
    }
}