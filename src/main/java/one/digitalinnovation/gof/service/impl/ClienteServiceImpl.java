package one.digitalinnovation.gof.service.impl;

import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.ClienteRepository;
import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.model.EnderecoRepository;
import one.digitalinnovation.gof.service.ClienteService;
import one.digitalinnovation.gof.service.ViaCepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private EnderecoRepository enderecoRepository;
    @Autowired
    private ViaCepService viaCepService;

    @Override
    public Iterable<Cliente> buscarTodos(){
        return clienteRepository.findAll();
    }

    @Override
    public Cliente buscarPorId(Long id){
        Optional<Cliente> cliente = clienteRepository.findById(id);
        return cliente.get();
    }

    @Override
    public void inserir(Cliente cliente){
        salvarClientePorCep(cliente);
    }

    @Override
    public void atualizar(Long id, Cliente cliente){
        Optional<Cliente> attCliente = clienteRepository.findById(id);
        if(attCliente.isPresent()) {
            salvarClientePorCep(cliente);
        }
    }

    @Override
    public void deletar(Long id){
        clienteRepository.deleteById(id);
    }

    // Pra evitar duplicar o código em 'inserir' e 'atualizar'
    // Criamos um método que retorna um endereço novo ou já existente.
    private void salvarClientePorCep(Cliente cliente){
        String cep = cliente.getEndereco().getCep();
        // Verifica se o endereco existe
        // Armazena em endereco se exisitr. Caso nao exista consome a API do ViaCep
        Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
            Endereco novoEndereco = viaCepService.consultarCep(cep);
            enderecoRepository.save(novoEndereco);
            return novoEndereco;
        });
        // Salva o novo endereco
        cliente.setEndereco(endereco);
        // Salva o cliente
        clienteRepository.save(cliente);
    }
}
