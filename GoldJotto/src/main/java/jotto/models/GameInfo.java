package jotto.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "games")
public class GameInfo {
    @Id
    private String id;

    private String userName;

    private String playerWord;
    private String computerWord;

    private List<String> playerGuesses;
    private List<Integer> playerGuessCounts;
    private List<String> computerGuesses;
    private List<Integer> computerGuessCounts;

    private List<Character> playerUsedCharacters;//All characters used in total
    private List<Character> computerUsedCharacters;

    private List<Character> playerMatchedCharacters;//All characters detrermined to be in the guess word(calculate beore turning these chars green)
    private List<Character> computerMatchedCharacters;

    private List<Character> playerRemovedCharacters;//All characters determined to be not be in the word
    private List<Character> computerRemovedCharacters;

    public GameInfo(){
    }
    public GameInfo(String _userName, String userWord, String _computerWord){
        this.userName = _userName;
        this.playerWord = userWord;
        this.computerWord = _computerWord;
        this.playerGuesses = new ArrayList<String>();
        this.computerGuesses = new ArrayList<String>();
        this.playerGuessCounts = new ArrayList<Integer>();
        this.computerGuessCounts = new ArrayList<Integer>();


        this.playerUsedCharacters = new ArrayList<Character>();
        this.playerRemovedCharacters = new ArrayList<Character>();
        this.computerUsedCharacters = new ArrayList<Character>();
        this.computerRemovedCharacters = new ArrayList<Character>();
        this.playerMatchedCharacters = new ArrayList<Character>();
        this.computerMatchedCharacters = new ArrayList<Character>();
    }

    public String toString(){
        //edit this to print out the data in the proper format
        return "Player results:\n" +
                "The word was: " + playerWord + "\n" +
                "You guessed: " + playerGuesses.toString() + "\n" +
                playerGuessCounts.toString() + "\n\n" +
                "Computer results:\n" +
                "The word was: " + computerWord + "\n" +
                "The computer guessed: " + computerGuesses.toString() + "\n\n";
    }
    public List<String> getPlayerGuesses(){
        return this.playerGuesses;
    }
    public List<Character> getPlayerUsedCharacters(){
        return this.playerUsedCharacters;
    }

    public List<String> getComputerGuesses(){
        return this.computerGuesses;
    }

    public List<Integer> getPlayerGuessCounts(){
        return this.playerGuessCounts;
    }

    public List<Integer> getComputerGuessCounts(){
        return this.computerGuessCounts;
    }

    public List<Character> getComputerUsedCharacters(){
        return this.computerUsedCharacters;
    }

    public List<Character> getPlayerRemovedCharacters(){
        return this.playerRemovedCharacters;
    }

    public List<Character> getComputerRemovedCharacters(){
        return this.computerRemovedCharacters;
    }

    public boolean isPastPlayerGuess(String a){
        boolean ans = false;
        for(int i = 0; i < this.playerGuesses.size();i++){
            String temp = (String)this.playerGuesses.get(i);
            if(temp == a){
                ans = true;
                return ans;
            }
        }
        return ans;

    }

    public boolean isPastComputerGuess(String a){
        boolean ans = false;
        for(int i = 0; i < this.computerGuesses.size();i++){
            String temp = (String)this.computerGuesses.get(i);
            if(temp == a){
                ans = true;
                return ans;
            }
        }
        return ans;
    }

    public String getPlayerGuessAt(int index){
        return this.playerGuesses.get(index);
    }

    public String getComputerGuessAt(int index){
        return this.computerGuesses.get(index);
    }

    public ArrayList<String> getPlayerGuessesWith(char c) {
        ArrayList<String> ans = new ArrayList<String>();
        for(int i = 0; i < playerGuesses.size(); i++){
           String temp = (String) getPlayerGuessAt(i);
           if(temp.indexOf(c) >= 0){
               ans.add(temp);
           }
        }
        return ans;
    }

    public List<String> getComputerGuessesWith(char c){
        ArrayList<String> ans = new ArrayList<String>();
        for(int i = 0; i < computerGuesses.size(); i++){
            String temp = (String) getComputerGuessAt(i);
            if(temp.indexOf(c) >= 0){
                ans.add(temp);
            }
        }
        return ans;
    }

    private void addNewPlayerGuessString(String a){
        this.playerGuesses.add(a);
    }

    private void addNewPlayerGuessCount(int a){
        Integer newInt = new Integer(a);
        this.playerGuessCounts.add(newInt);
    }

    private void addNewComputerGuessCount(int a){
        Integer newInt = new Integer(a);
        this.computerGuessCounts.add(newInt);
    }

    private void addNewComputerGuessString(String a){
        this.computerGuesses.add(a);
    }

    public void addPlayerMatchedChar(char c){
        Character newChar = new Character(c);
        if(!(this.playerMatchedCharacters.contains(newChar))){
            this.playerMatchedCharacters.add(newChar);
        }
    }

    public void addPlayerRemovedChar(char c){
        Character newChar = new Character(c);
        if(!(this.playerRemovedCharacters.contains(newChar))){
            this.playerRemovedCharacters.add(newChar);
        }
    }


    public void addComputerMatchedChar(char c){
        Character newChar = new Character(c);
        if(!(this.computerMatchedCharacters.contains(newChar))){
            this.computerMatchedCharacters.add(newChar);
        }
    }


    public void addComputerRemovedChar(char c){
        Character newChar = new Character(c);
        if(!(this.computerRemovedCharacters.contains(newChar))){
            this.computerRemovedCharacters.add(newChar);
        }
    }

    public boolean charInPlayerWord(char c){
        if(playerWord.indexOf(c) >= 0){
            return true;
        }else{
            return false;
        }
    }

    public boolean charInComputerWord(char c){
        if(computerWord.indexOf(c) >= 0){
            return true;
        }else{
            return false;
        }
    }

    public void addUsedPlayerChars(String s){
        char [] sArray = s.toCharArray();
        for(int i = 0;i < sArray.length; i++){
            char a = sArray[i];
            if(!(this.playerUsedCharacters.contains(a))){
                Character newChar = new Character(a);
                this.playerUsedCharacters.add(newChar);
            }
        }
    }

    public void addUsedComputerChars(String s){
        char [] sArray = s.toCharArray();
        for(int i = 0; i > sArray.length; i++){
            char a = sArray[i];
            if(!(this.computerUsedCharacters.contains(a))){
                Character newChar = new Character(a);
                this.computerUsedCharacters.add(newChar);
            }
        }
    }

    public void addNewPlayerGuess(String guess, int guessCount){
        this.addNewPlayerGuessString(guess);
        this.addNewPlayerGuessCount(guessCount);
    }

    public void addNewComputerGuess(String guess, int guessCount){
        this.addNewComputerGuessString(guess);
        this.addNewComputerGuessCount(guessCount);
    }


    public void calculateComputerCharacters(){
        //first iterate through all guessed words
        ArrayList<Character> matchedChars = new ArrayList<Character>();
        ArrayList<Character> removedChars = new ArrayList<Character>();
        for(int i = 0; i < this.computerGuesses.size(); i++){
            String currentGuess = (String)this.computerGuesses.get(i);
            char [] guessArray = currentGuess.toCharArray();

            for(int j = 0; j < guessArray.length;j++){

                char currA = guessArray[j];
                Character newCharA = new Character(currA);
                if(this.playerWord.indexOf(newCharA) >= 0){

                    if(!(matchedChars.contains(newCharA))){
                        matchedChars.add(newCharA);
                    }

                }else{
                    removedChars.add(newCharA);
                }
            }

                /*

            }else if(this.computerGuesses.size() == 1){
                char [] guessArray = currentGuess.toCharArray();
                for(int a = 0; a < guessArray.length; a++){
                    char currA = guessArray[a];
                    Character newCharA = new Character(currA);
                    if(this.playerWord.indexOf(currA) >= 0){

                        if(!(matchedChars.contains(newCharA))){
                            matchedChars.add(newCharA);
                        }
                    }else{
                        if(!(removedChars.contains(newCharA))){
                            removedChars.add(newCharA);
                        }
                    }
                }
            }else{
                //the currCount is higher than 0, meaning there's stuff to be scanned
                char [] guessArray = currentGuess.toCharArray();
                //compare this to the rest of the strings in the array
                for(int k = (i+1); k < this.computerGuesses.size(); k++){
                    String compareGuess =  this.computerGuesses.get(k);
                    char [] compareGuessArray = compareGuess.toCharArray();
                    int compareCount = this.computerGuessCounts.get(k);
                    if(compareCount == 0){
                        for(int p = 0; p < compareGuessArray.length; p++){
                            char current = compareGuessArray[p];
                            Character currentChar = new Character(current);
                            if(this.playerWord.indexOf(current) >= 0){

                                if(!(matchedChars.contains(currentChar))){
                                    matchedChars.add(currentChar);
                                }
                            }else{
                                if(!(removedChars.contains(currentChar))){
                                    removedChars.add(currentChar);
                                }
                            }
                        }
                    }else{
                        for(int p1 = 0; p1 < compareGuessArray.length; p1++){
                            char current = compareGuessArray[p1];
                            Character currentChar = new Character(current);
                            if(this.playerWord.indexOf(current) >= 0){

                                if(!(matchedChars.contains(currentChar))){
                                    matchedChars.add(currentChar);
                                }
                            }else{
                                if(!(removedChars.contains(currentChar))){
                                    removedChars.add(currentChar);
                                }
                            }
                        }
                        //compare the two for common characters
                        /*for(int i1 = 0; i1 < guessArray.length; i1++){
                            char firstChar = guessArray[i1];
                            for(int i2 = 0; i2 < compareGuessArray.length; i2++){
                                char secondChar = compareGuessArray[i2];
                                if(firstChar == secondChar){
                                    if(!(matchedChars.contains(firstChar))){
                                        matchedChars.add(firstChar);
                                    }
                                }
                            }
                        }*/



        }

        this.computerMatchedCharacters = matchedChars;
        this.computerRemovedCharacters = removedChars;

    }

    public String getComputerWord(){
        return this.computerWord;
    }

    public String getUserWord(){
        return this.computerWord;
    }



}
