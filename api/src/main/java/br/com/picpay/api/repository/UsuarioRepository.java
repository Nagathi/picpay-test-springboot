package br.com.picpay.api.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import br.com.picpay.api.model.UsuarioModel;

public interface UsuarioRepository extends CrudRepository<UsuarioModel, Long> {
    Optional<UsuarioModel> findByCpf(String cpf);
    Optional<UsuarioModel> findByEmail(String email);
}
