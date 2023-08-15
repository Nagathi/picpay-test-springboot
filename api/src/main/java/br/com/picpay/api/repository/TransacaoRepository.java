package br.com.picpay.api.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.picpay.api.model.TransacaoModel;

public interface TransacaoRepository extends CrudRepository<TransacaoModel, Long>{
    
}
