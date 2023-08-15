package br.com.picpay.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.picpay.api.service.UsuarioService;

@Controller
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar_usuario( @RequestParam("nome") String nome,
                                                @RequestParam("cpf") String cpf,
                                                @RequestParam("email") String email,
                                                @RequestParam("senha") String senha,
                                                @RequestParam("tipo") String tipo){
        return usuarioService.cadastrar_usuario(nome, cpf, email, tipo, senha);
    }

    @PostMapping("/transferir")
    public ResponseEntity<?> transferir(@RequestParam("cpf") String cpf,
                                        @RequestParam("cpfDestino") String cpfDestino,
                                        @RequestParam("valor") float valor){
        return usuarioService.transferir(cpf, cpfDestino, valor);
    }

    @PutMapping("/saldo")
    public ResponseEntity<?> addSaldo(  @RequestParam("cpf") String cpf,
                                        @RequestParam("valor") float valor){
        return usuarioService.addSaldo(cpf, valor);
    }
}
