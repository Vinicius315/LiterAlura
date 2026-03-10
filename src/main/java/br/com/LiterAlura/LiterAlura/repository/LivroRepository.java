package br.com.LiterAlura.LiterAlura.repository;

import br.com.LiterAlura.LiterAlura.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    // Busca livros por idioma (ex: 'pt', 'en')
    List<Livro> findByIdioma(String idioma);
}