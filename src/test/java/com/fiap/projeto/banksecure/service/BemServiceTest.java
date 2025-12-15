package com.fiap.projeto.banksecure.service;

import com.fiap.projeto.banksecure.domain.Apolice;
import com.fiap.projeto.banksecure.domain.Bem;
import com.fiap.projeto.banksecure.dto.BemDTO;
import com.fiap.projeto.banksecure.repository.ApoliceRepository;
import com.fiap.projeto.banksecure.repository.BemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BemServiceTest {

    @InjectMocks
    private BemService bemService;

    @Mock
    private BemRepository bemRepository;

    @Mock
    private ApoliceRepository apoliceRepository;

    @Test
    @DisplayName("Deve cadastrar Bem com sucesso")
    void deveCadastrarBem() {
        UUID apoliceId = UUID.randomUUID();
        UUID bemId = UUID.randomUUID();

        BemDTO dtoMock = mock(BemDTO.class);
        when(dtoMock.apoliceId()).thenReturn(apoliceId);

        Bem bemMock = new Bem();
        bemMock.setTitulo("Carro");
        bemMock.setValor(new BigDecimal("50000"));
        when(dtoMock.toEntity()).thenReturn(bemMock);

        Apolice apoliceMock = new Apolice();
        apoliceMock.setId(apoliceId);

        when(apoliceRepository.findById(apoliceId)).thenReturn(Optional.of(apoliceMock));

        when(apoliceRepository.existsById(apoliceId)).thenReturn(true);

        when(bemRepository.save(any(Bem.class))).thenAnswer(i -> {
            Bem b = i.getArgument(0);
            b.setId(bemId);
            return b;
        });

        BemDTO resultado = bemService.cadastrarBem(dtoMock);

        assertNotNull(resultado);
        verify(apoliceRepository).findById(apoliceId);
        verify(bemRepository).save(any(Bem.class));
    }

    @Test
    @DisplayName("Deve falhar cadastro se Apólice não for encontrada")
    void deveFalharCadastroSemApolice() {
        UUID apoliceId = UUID.randomUUID();
        BemDTO dtoMock = mock(BemDTO.class);
        when(dtoMock.apoliceId()).thenReturn(apoliceId);
        when(dtoMock.toEntity()).thenReturn(new Bem());

        when(apoliceRepository.findById(apoliceId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bemService.cadastrarBem(dtoMock));

        assertEquals("Apólice não encontrada", ex.getMessage());
        verify(bemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios e valores do Bem")
    void deveFalharValidacaoCamposInvalidos() {
        UUID apoliceId = UUID.randomUUID();
        BemDTO dtoMock = mock(BemDTO.class);
        when(dtoMock.apoliceId()).thenReturn(apoliceId);
        Apolice apoliceMock = new Apolice(); apoliceMock.setId(apoliceId);
        when(apoliceRepository.findById(apoliceId)).thenReturn(Optional.of(apoliceMock));

        Bem bemSemTitulo = new Bem();
        bemSemTitulo.setValor(BigDecimal.TEN);
        when(dtoMock.toEntity()).thenReturn(bemSemTitulo);

        IllegalArgumentException exTitulo = assertThrows(IllegalArgumentException.class,
                () -> bemService.cadastrarBem(dtoMock));
        assertEquals("Título do bem é obrigatório.", exTitulo.getMessage());

        Bem bemSemValor = new Bem();
        bemSemValor.setTitulo("Casa");
        bemSemValor.setValor(null);
        when(dtoMock.toEntity()).thenReturn(bemSemValor);

        IllegalArgumentException exValor = assertThrows(IllegalArgumentException.class,
                () -> bemService.cadastrarBem(dtoMock));
        assertEquals("Valor do bem é obrigatório.", exValor.getMessage());

        Bem bemValorZero = new Bem();
        bemValorZero.setTitulo("Casa");
        bemValorZero.setValor(BigDecimal.ZERO);
        when(dtoMock.toEntity()).thenReturn(bemValorZero);

        IllegalArgumentException exZero = assertThrows(IllegalArgumentException.class,
                () -> bemService.cadastrarBem(dtoMock));
        assertEquals("Valor do bem deve ser maior que zero.", exZero.getMessage());
    }


    @Test
    @DisplayName("Deve atualizar Bem com sucesso")
    void deveAtualizarBem() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID apoliceId = UUID.randomUUID();

        Bem bemExistente = new Bem();
        bemExistente.setId(id);
        bemExistente.setTitulo("Antigo");
        bemExistente.setValor(new BigDecimal("100.00"));
        Apolice apolice = new Apolice(); apolice.setId(apoliceId);
        bemExistente.setApolice(apolice);

        BemDTO dtoMock = mock(BemDTO.class);
        when(dtoMock.titulo()).thenReturn("Novo Título");
        when(dtoMock.valor()).thenReturn(new BigDecimal("200.00"));

        when(bemRepository.findById(id)).thenReturn(Optional.of(bemExistente));
        when(apoliceRepository.existsById(apoliceId)).thenReturn(true); // Necessário para validarBem
        when(bemRepository.save(any(Bem.class))).thenAnswer(i -> i.getArgument(0));

        BemDTO resultado = bemService.atualizarBem(id, dtoMock);

        assertEquals("Novo Título", bemExistente.getTitulo());
        assertEquals(new BigDecimal("200.00"), bemExistente.getValor());
        verify(bemRepository).save(bemExistente);
    }

    @Test
    @DisplayName("Deve falhar atualização se ID não existe")
    void deveFalharAtualizacaoIdInexistente() {
        UUID id = UUID.randomUUID();
        BemDTO dto = mock(BemDTO.class);
        when(bemRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bemService.atualizarBem(id, dto));
    }

    @Test
    @DisplayName("Deve deletar Bem se não estiver vinculado (Regra existsByIdAndApoliceIsNotNull)")
    void deveDeletarBem() {
        UUID id = UUID.randomUUID();
        Bem bem = new Bem();

        when(bemRepository.existsByIdAndApoliceIsNotNull(id)).thenReturn(false);

        when(bemRepository.findById(id)).thenReturn(Optional.of(bem));

        bemService.deletarBem(id);

        verify(bemRepository).delete(bem);
    }

    @Test
    @DisplayName("Deve impedir deleção se Bem estiver vinculado a Apólice")
    void deveFalharDelecaoVinculado() {
        UUID id = UUID.randomUUID();

        when(bemRepository.existsByIdAndApoliceIsNotNull(id)).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> bemService.deletarBem(id));

        assertEquals("Não é possível excluir bem vinculado a uma apólice", ex.getMessage());
        verify(bemRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve falhar deleção se Bem não for encontrado")
    void deveFalharDelecaoIdInexistente() {
        UUID id = UUID.randomUUID();
        when(bemRepository.existsByIdAndApoliceIsNotNull(id)).thenReturn(false);
        when(bemRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bemService.deletarBem(id));
    }

    @Test
    @DisplayName("Deve buscar Bem por ID")
    void deveBuscarPorId() {
        UUID id = UUID.randomUUID();
        Bem bem = new Bem();
        bem.setId(id);
        when(bemRepository.findById(id)).thenReturn(Optional.of(bem));

        BemDTO resultado = bemService.buscarPorId(id);
        assertNotNull(resultado);
    }

    @Test
    @DisplayName("Deve listar todos os bens")
    void deveListarTodos() {
        when(bemRepository.findAll()).thenReturn(List.of(new Bem()));
        List<BemDTO> lista = bemService.getAllBens();
        assertFalse(lista.isEmpty());
    }
}