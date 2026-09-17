package br.com.leperber.prazoflow.service;

import br.com.leperber.prazoflow.entity.StatusPadrao;
import br.com.leperber.prazoflow.entity.Tecnico;
import br.com.leperber.prazoflow.repository.TecnicoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class TecnicoService {

    private final TecnicoRepository tecnicoRepository;

    public TecnicoService(TecnicoRepository tecnicoRepository) {
        this.tecnicoRepository = tecnicoRepository;
    }

    public Tecnico criar(Tecnico tecnico){
        if(tecnicoRepository.existsByCodigoIdDiscord(tecnico.getCodigoIdDiscord())){
            throw new IllegalArgumentException("O código do ID do Discord ja está sendo utilizado por outro tecnico!");
        }
        if(tecnicoRepository.existsByEmail(tecnico.getEmail())){
            throw new IllegalArgumentException("Foi identificado que o email ja está sendo utilizado por outro tecnico!");
        }
        if(!StringUtils.hasText(tecnico.getNome())){
            throw new IllegalArgumentException("O Nome não pode ser vazio ou nulo!");
        }

        return tecnicoRepository.save(tecnico);

    }

    public Tecnico buscarId(Long id){

        if (!tecnicoRepository.existsById(id)){
            throw new IllegalArgumentException("Tecnico não encontrado!");
        }

        return tecnicoRepository.findById(id).get();
    }

    public Page<Tecnico> buscarTecnicos(Pageable pageable){

        if(tecnicoRepository.findAll(pageable).isEmpty()){
            throw new IllegalArgumentException("Não há nhenhum registro de tecnico!");
        }

        return tecnicoRepository.findAll(pageable);
    }

    public List<Tecnico> buscarTecnicosNome(String nome){

        if (!tecnicoRepository.existsByNomeContaining(nome)){
            throw new IllegalArgumentException("Nenhum Nome localizado com essas palavras!");
        }

        return tecnicoRepository.findByNomeContaining(nome);
    }

    public Tecnico buscarIdDiscord(String codigoIdDiscord){
        if(!tecnicoRepository.existsByCodigoIdDiscord(codigoIdDiscord)){
            throw new IllegalArgumentException("Nenhum tecnico foi localizado com essas código id discord!");
        }

        return tecnicoRepository.findByCodigoIdDiscord(codigoIdDiscord).get();
    }

    public Tecnico alterarNome(Long id, String nome){

        if(!tecnicoRepository.existsById(id)){
            throw new IllegalArgumentException("Nenhum tecnico foi localizado com esse ID!");
        }
        if (!StringUtils.hasText(nome)){
            throw new IllegalArgumentException("O Nome não pode ser vazio ou nulo!");
        }

        Tecnico tecnico = tecnicoRepository.findById(id).get();
        tecnico.setNome(nome);

        return tecnicoRepository.save(tecnico);
    }

    public Tecnico alterarEmail(Long id, String email){
        if(!tecnicoRepository.existsById(id)){
            throw new IllegalArgumentException("Nenhum tecnico foi localizado com esse ID!");
        }
        if (!StringUtils.hasText(email)){
            throw new IllegalArgumentException("O email não pode ser vazio ou nulo!");
        }
        if(tecnicoRepository.existsByEmail(email)){
            throw new IllegalArgumentException("Foi identificado que o email ja está sendo utilizado por outro tecnico!");
        }

        Tecnico tecnico = tecnicoRepository.findById(id).get();
        tecnico.setEmail(email);

        return tecnicoRepository.save(tecnico);
    }

    public Tecnico alterarIdDiscord(Long id, String codigoIdDiscord){
        if(!tecnicoRepository.existsById(id)){
            throw new IllegalArgumentException("Nenhum tecnico foi localizado com esse ID!");
        }
        if (!StringUtils.hasText(codigoIdDiscord)){
            throw new IllegalArgumentException("O código ID do Discord não pode ser vazio ou nulo!");
        }
        if(tecnicoRepository.existsByCodigoIdDiscord(codigoIdDiscord)){
            throw new IllegalArgumentException("O código do ID do Discord ja está sendo utilizado por outro tecnico!");
        }

        Tecnico tecnico = tecnicoRepository.findById(id).get();
        tecnico.setCodigoIdDiscord(codigoIdDiscord);

        return tecnicoRepository.save(tecnico);
    }

    public Tecnico alterarStatus(Long id, StatusPadrao statusPadrao){
        if(!tecnicoRepository.existsById(id)){
            throw new IllegalArgumentException("Nenhum tecnico foi localizado com esse ID!");
        }

        Tecnico tecnico = tecnicoRepository.findById(id).get();

        if (tecnico.getStatus().equals(statusPadrao)){
            throw new IllegalArgumentException("O status do tecnico ja se encontra como " + statusPadrao);
        }

        tecnico.setStatus(statusPadrao);

        return tecnicoRepository.save(tecnico);
    }

}
