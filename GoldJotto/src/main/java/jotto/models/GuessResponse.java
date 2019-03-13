package jotto.models;

public class GuessResponse {
    private String error;
    private String word;
    private int guessCount;
    private String computerGuess;
    private int computerGuessCount;
    private boolean playerWins;
    private boolean computerWins;

    public GuessResponse() { }

    public GuessResponse(String error, String word, int guessCount, String computerGuess, int computerGuessCount, boolean playerWins, boolean computerWins) {
        this.error = error;
        this.word = word;
        this.guessCount = guessCount;
        this.computerGuess = computerGuess;
        this.computerGuessCount = computerGuessCount;
        this.playerWins = playerWins;
        this.computerWins = computerWins;
    }

    public String getError() {
        return error;
    }

    public String getWord() {
        return word;
    }

    public int getGuessCount() { return guessCount; }

    public boolean getPlayerWins() { return playerWins; }

    public boolean getComputerWins() { return computerWins; }

    public String getComputerGuess() { return computerGuess; }

    public int getComputerGuessCount() { return computerGuessCount; }

    public void setError(String error) {
        this.error = error;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void getGuessCount(int guessCount) { this.guessCount = guessCount; }
}
