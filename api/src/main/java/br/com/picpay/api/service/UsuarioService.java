package br.com.picpay.api.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.picpay.api.model.TransacaoModel;
import br.com.picpay.api.model.UsuarioModel;
import br.com.picpay.api.repository.TransacaoRepository;
import br.com.picpay.api.repository.UsuarioRepository;
import br.com.picpay.api.response.MockyResponse;
import jakarta.transaction.Transactional;

@Service
public class UsuarioService {
  
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    private final String mockyUrl = "https://run.mocky.io/v3/8fafdd68-a090-496f-8c9a-3442cf30dae6";
    private final String mockyNotifyUrl = "http://o4d9z.mocklab.io/notify";

    @Autowired
    private RestTemplate restTemplate;

    public String getMockyMessage() {
        MockyResponse response = restTemplate.getForObject(mockyUrl, MockyResponse.class);
        return response.getMessage();
    }

    public ResponseEntity<?> cadastrar_usuario( String nome, 
                                                String cpf, 
                                                String email, 
                                                String tipo, 
                                                String senha){
        Optional<UsuarioModel> usuarioOptional = usuarioRepository.findByCpf(cpf);
        if(!usuarioOptional.isPresent()) {
            usuarioOptional = usuarioRepository.findByEmail(email);
            if(!usuarioOptional.isPresent()){
                UsuarioModel usuario = new UsuarioModel();
                usuario.setNome(nome);
                usuario.setCpf(cpf);
                usuario.setEmail(email);
                usuario.setSenha(senha);
                usuario.setTipo(tipo);
                usuario.setSaldo(0f);
                usuarioRepository.save(usuario);
                return ResponseEntity.ok().build();
            }else{
                return ResponseEntity.badRequest().body("E-mail já existe!");
            }
        }else{
            return ResponseEntity.badRequest().body("CPF/CNPJ já existe");
        }                                          
    }

    @Transactional
public ResponseEntity<?> transferir(String cpf, String cpfDestino, float valor) {
    try {
        Optional<UsuarioModel> usuarioOptional = usuarioRepository.findByCpf(cpf);
        Optional<UsuarioModel> usuarioDestinoOptional = usuarioRepository.findByCpf(cpfDestino);

        if (usuarioOptional.isPresent() && usuarioDestinoOptional.isPresent()) {
            UsuarioModel usuario = usuarioOptional.get();
            UsuarioModel usuarioDestino = usuarioDestinoOptional.get();

            if (!usuario.getTipo().equals("usuario")) {
                return ResponseEntity.badRequest().body("Não é possível realizar transferência sendo " + usuario.getTipo() + "!");
            }

            if (usuario.getSaldo() - valor < 0) {
                return ResponseEntity.badRequest().body("Saldo insuficiente!");
            }

            if (!getMockyMessage().equals("Autorizado")) {
                return ResponseEntity.badRequest().body("A transferência não foi autorizada!");
            }

            ResponseEntity<?> transferResult = realizarTransferencia(usuario, usuarioDestino, valor);

            ResponseEntity<String> notifyResponse = restTemplate.postForEntity(mockyNotifyUrl, null, String.class);

            if (notifyResponse.getStatusCode().is2xxSuccessful()) {
                return transferResult;
            } else {
                return ResponseEntity.internalServerError().body("Erro ao enviar notificação");
            }
            
        } else {
            return ResponseEntity.badRequest().body("Usuário não encontrado");
        }
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().body("Ocorreu um erro inesperado");
    }
}

private ResponseEntity<?> realizarTransferencia(UsuarioModel usuario, UsuarioModel usuarioDestino, float valor) {
    usuario.setSaldo(usuario.getSaldo() - valor);
    usuarioDestino.setSaldo(usuarioDestino.getSaldo() + valor);
    usuarioRepository.save(usuario);
    usuarioRepository.save(usuarioDestino);

    TransacaoModel t = new TransacaoModel();
    t.setPayer(usuario.getCpf());
    t.setValue(valor);
    t.setPayee(usuarioDestino.getCpf());
    transacaoRepository.save(t);
    return ResponseEntity.ok().body("Transferência concluída");
}

    public ResponseEntity<?> addSaldo(String cpf, float valor){
        Optional<UsuarioModel> usuarioOptional = usuarioRepository.findByCpf(cpf);
        if(usuarioOptional.isPresent()){
            UsuarioModel usuario = usuarioOptional.get();
            usuario.setSaldo(usuario.getSaldo()+valor);
            usuarioRepository.save(usuario);
            return ResponseEntity.ok().body("Transferência concluída"); 
        }else{
            return ResponseEntity.badRequest().body("Usuário não encontrado");
        }
    }
}
