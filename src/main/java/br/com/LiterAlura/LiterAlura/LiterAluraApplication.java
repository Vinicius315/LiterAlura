

package br.com.LiterAlura.LiterAlura;

import br.com.LiterAlura.LiterAlura.repository.AutorRepository;
import br.com.LiterAlura.LiterAlura.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiterAluraApplication implements CommandLineRunner {

	// O Spring "injeta" as conexões com o banco aqui automaticamente
	@Autowired
	private LivroRepository livroRepository;

	@Autowired
	private AutorRepository autorRepository;

	public static void main(String[] args) {
		SpringApplication.run(LiterAluraApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Passamos os repositórios para a nossa classe Principal
		Principal principal = new Principal(livroRepository, autorRepository);
		principal.exibeMenu();
	}
}