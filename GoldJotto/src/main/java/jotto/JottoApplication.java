package jotto;

import jotto.models.Word;
import jotto.models.WordRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@SpringBootApplication
public class JottoApplication {

//    @Autowired
//    private WordRepository wordsrepo;
    public static void main(String[] args) {
        SpringApplication.run(JottoApplication.class, args);
    }

//    @Override
//    public void run(String... args) throws Exception {
//        wordsrepo.deleteAll();
//        try (BufferedReader br = new BufferedReader(new FileReader("./src/main/resources/wordlist.txt"))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                wordsrepo.save(new Word(line));
//            }
//        }
//        //prints all words to console
//        for (Word word : wordsrepo.findAll()){
//            System.out.println(word);
//        }
//    }
}
