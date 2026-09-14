package br.com.leperber.prazoflow.repository;

import br.com.leperber.prazoflow.entity.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TecnicoRepository extends JpaRepository<Tecnico, Long>{
    List<Tecnico> findByNomeContaining(String nome);
    Optional<Tecnico> findByCodigoIdDiscord(String codigoIdDiscord);
}
