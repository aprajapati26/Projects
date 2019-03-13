package jotto.models;

import org.springframework.data.annotation.Id;

public class Word {
    @Id
    private String id;

    private String word;

    public Word(){}

    public Word(String word){
        this.word = word;
    }

    public String getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Override
    public String toString() {
        return String.format(
                "Word[id=%s, word = '%s'",
                id, word);
    }
}

