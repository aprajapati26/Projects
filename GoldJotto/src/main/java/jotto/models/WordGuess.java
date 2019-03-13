package jotto.models;

public class WordGuess {
    private String word;
    private String playerWord;
    private String computerWordID;

    public WordGuess() { }

    public WordGuess(String word, String playerWord, String computerWordID) {
        this.word = word;
        this.playerWord = playerWord;
        this.computerWordID = computerWordID;
    }

    public String getWord() {
        return word;
    }
    public String getPlayerWord() {
        return playerWord;
    }
    public String getComputerWordID() {
        return computerWordID;
    }

    public void setWord(String word) {
        this.word = word;
    }
    public void setPlayerWord(String playerWord) {
        this.playerWord = playerWord;
    }
    public void setComputerWordID(String computerWordID) {
        this.computerWordID = computerWordID;
    }
}
