

package br.com.LiterAlura.LiterAlura;

import br.com.LiterAlura.LiterAlura.model.Autor;
import br.com.LiterAlura.LiterAlura.model.DadosResposta;
import br.com.LiterAlura.LiterAlura.model.Livro;
import br.com.LiterAlura.LiterAlura.repository.AutorRepository;
import br.com.LiterAlura.LiterAlura.repository.LivroRepository;
import br.com.LiterAlura.LiterAlura.service.ConsumoApi;
import br.com.LiterAlura.LiterAlura.service.ConverteDados;

import java.util.List;
import java.util.Scanner;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://gutendex.com/books/?search=";

    private LivroRepository livroRepositorio;
    private AutorRepository autorRepositorio;

    public Principal(LivroRepository livroRepositorio, AutorRepository autorRepositorio) {
        this.livroRepositorio = livroRepositorio;
        this.autorRepositorio = autorRepositorio;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    \n*** LiterAlura - Catálogo de Livros ***
                    1 - Buscar livro pelo título
                    2 - Listar livros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos em um determinado ano
                    5 - Listar livros em um determinado idioma
                    0 - Sair
                    """;

            System.out.println(menu);
            System.out.print("Escolha uma opção: ");
            
            try {
                opcao = Integer.parseInt(leitura.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite um número válido.");
                continue;
            }

            switch (opcao) {
                case 1 -> buscarLivroWeb();
                case 2 -> listarLivrosRegistrados();
                case 3 -> listarAutoresRegistrados();
                case 4 -> listarAutoresVivosNoAno();
                case 5 -> listarLivrosPorIdioma();
                case 0 -> System.out.println("Saindo da aplicação...");
                default -> System.out.println("Opção inválida!");
            }
        }
    }

    private void buscarLivroWeb() {
        System.out.print("Digite o nome do livro para busca: ");
        var tituloLivro = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + tituloLivro.replace(" ", "+").toLowerCase());
        var dadosResposta = conversor.obterDados(json, DadosResposta.class);

        if (dadosResposta.resultados() != null && !dadosResposta.resultados().isEmpty()) {
            var dadosLivro = dadosResposta.resultados().get(0);
            var dadosAutor = dadosLivro.autores().get(0);

            // Verifica se o livro já existe no banco
            List<Livro> livrosExistentes = livroRepositorio.findAll();
            boolean livroJaCadastrado = livrosExistentes.stream()
                    .anyMatch(l -> l.getTitulo().equalsIgnoreCase(dadosLivro.titulo()));

            if (livroJaCadastrado) {
                System.out.println("\nEste livro já está cadastrado no banco de dados!");
                return;
            }

            // Verifica se o autor já existe para não duplicar
            Autor autor = autorRepositorio.findByNome(dadosAutor.nome());
            if (autor == null) {
                autor = new Autor(dadosAutor);
                autorRepositorio.save(autor);
            }

            Livro livro = new Livro(dadosLivro);
            livro.setAutor(autor);
            livroRepositorio.save(livro);

            System.out.println("\nLivro salvo no banco com sucesso!");
            System.out.println(livro);
        } else {
            System.out.println("\nLivro não encontrado na API do Gutendex.");
        }
    }

    private void listarLivrosRegistrados() {
        System.out.println("\n--- Livros Registrados ---");
        List<Livro> livros = livroRepositorio.findAll();
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro cadastrado no banco.");
        } else {
            livros.forEach(System.out::println);
        }
    }

    private void listarAutoresRegistrados() {
        System.out.println("\n--- Autores Registrados ---");
        List<Autor> autores = autorRepositorio.findAll();
        if (autores.isEmpty()) {
            System.out.println("Nenhum autor cadastrado no banco.");
        } else {
            autores.forEach(System.out::println);
        }
    }

    private void listarAutoresVivosNoAno() {
        System.out.print("Digite o ano para pesquisar autores vivos: ");
        try {
            Integer ano = Integer.parseInt(leitura.nextLine());
            List<Autor> autores = autorRepositorio.findAutoresVivosNoAno(ano);
            
            if (autores.isEmpty()) {
                System.out.println("\nNenhum autor vivo encontrado no ano de " + ano + ".");
            } else {
                System.out.println("\n--- Autores vivos no ano de " + ano + " ---");
                autores.forEach(System.out::println);
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, digite um ano válido.");
        }
    }

    private void listarLivrosPorIdioma() {
        System.out.println("""
                \nDigite o idioma para busca:
                es - Espanhol
                en - Inglês
                fr - Francês
                pt - Português
                """);
        String idioma = leitura.nextLine();
        
        List<Livro> livros = livroRepositorio.findByIdioma(idioma);
        if (livros.isEmpty()) {
            System.out.println("\nNão existem livros registrados nesse idioma.");
        } else {
            System.out.println("\n--- Livros no idioma '" + idioma + "' ---");
            livros.forEach(System.out::println);
        }
    }
}