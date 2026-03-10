package br.com.LiterAlura.LiterAlura.repository;

import br.com.LiterAlura.LiterAlura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    // Busca se o autor já existe no banco
    Autor findByNome(String nome);

    // JPQL personalizado para buscar autores vivos num determinado ano
    @Query("SELECT a FROM Autor a WHERE a.anoNascimento <= :ano AND (a.anoFalecimento IS NULL OR a.anoFalecimento >= :ano)")
    List<Autor> findAutoresVivosNoAno(Integer ano);
}