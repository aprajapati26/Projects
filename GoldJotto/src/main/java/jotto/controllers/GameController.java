package jotto.controllers;

import jotto.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import jotto.models.User;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.io.IOException;

@Controller
public class GameController {
    @Autowired
    private WordRepository wordsRepo;
    @Autowired
    private GameInfoRepository gamesRepo;
    private List<String> computerWordsRepo;
    private String currentUser;
    private GameInfo gameInfo;
    private int numGuesses;

    @RequestMapping(value = "/past", method = RequestMethod.GET)
    public ModelAndView pastGames(){
        ModelAndView mv = new ModelAndView();
        mv.addObject("pastGames",gamesRepo.findByuserName(currentUser).toString());
        mv.setViewName("past");
        return mv;
    }
    @RequestMapping(value = "/game", method = RequestMethod.GET)
    public ModelAndView newGame() {
        String user = "";
        Object princ = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(princ instanceof UserDetails){
             currentUser = ((UserDetails)princ).getUsername();

        }else{
             currentUser = princ.toString();
        }
        this.numGuesses = 0;
        ModelAndView mv = new ModelAndView();
        mv.addObject("playerWord", new WordGuess());
        mv.addObject("currentUser", currentUser);
        mv.setViewName("new");
        //updates wordlist
        //only need to be run once to update, uncomment if wordlist needs to be changed
        //updateWordlist("./src/main/resources/wordlist.txt",wordsRepo);
        return mv;

    }

    @RequestMapping(value = "/game", method = RequestMethod.POST)
    public ModelAndView newGameWordSubmit(@ModelAttribute WordGuess playerWord) {
        ModelAndView mv = new ModelAndView();
        String word = playerWord.getPlayerWord().toLowerCase();
        if(word.length() != 5) {
            mv.addObject("playerWord", new WordGuess());
            mv.addObject("error", "Word does not contain 5 letters");
            mv.addObject("currentUser", currentUser);
            mv.setViewName("new");
            return mv;
        }

        boolean repeating = checkForRepeatingChars(word);
        if(repeating) {
            mv.addObject("playerWord", new WordGuess());
            mv.addObject("error", "Word has repeating letters");
            mv.addObject("currentUser", currentUser);
            mv.setViewName("new");
            return mv;
        }

        //check if word is real
        boolean valid = false;
        List<Word> allWords = new ArrayList<>();
        valid = checkIfWordIsValid(word, allWords);

        if(!valid) {
            mv.addObject("playerWord", new WordGuess());
            mv.addObject("error", "Word is invalid");
            mv.addObject("currentUser", currentUser);
            mv.setViewName("new");
            return mv;
        }

        allWords = filterWords(allWords);
        int numWords = allWords.size();
        Random rand = new Random();
        int randomWordIndex = rand.nextInt(numWords + 1);
        Word computerWord = allWords.get(randomWordIndex);
        this.gameInfo = new GameInfo(currentUser, word, computerWord.getWord());
        mv.addObject("playerWord", word);
        mv.addObject("computerWordID", computerWord.getId());
        mv.setViewName("game");
        mv.addObject("currentUser", currentUser);
        return mv;
    }

    @MessageMapping("/guess")
    @SendTo("/turn/computer")
    public GuessResponse checkGuessedWord(WordGuess message) throws Exception {


        String error = "";
        String guess = message.getWord();
        String playerWord = message.getPlayerWord().toLowerCase();

        ArrayList<Word> filteredWords = getFilteredWords();
        int numW = filteredWords.size();
        Random rand = new Random();
        int randomWIndex = rand.nextInt(numW + 1);
        String computerWord = filteredWords.get(randomWIndex).getWord();

        if (guess.length() != 5) {
            error = "Word does not contain 5 letters";
            return new GuessResponse(error, guess, -1, null, -1, false, false);
        }

        boolean repeating = checkForRepeatingChars(guess);
        if (repeating) {
            error = "Word has repeating letters";
            return new GuessResponse(error, guess, -1, null, -1, false, false);
        }

        //check if word is real
        boolean valid = false;
        List<Word> allWords = new ArrayList<>();


        valid = checkIfWordIsValid(guess, allWords);
        allWords = filterWords(allWords);
        if (!valid) {
            error = "Word is invalid";
            return new GuessResponse(error, guess, -1, null, -1, false, false);
        }

        if ((this.gameInfo.isPastPlayerGuess(playerWord)) && (this.numGuesses != 0)) {
            error = "You have already entered that word";
            return new GuessResponse(error, guess, -1, null, -1, false, false);
        }
        String realComputerVal = this.gameInfo.getComputerWord();
        int guessCount = getGuessCount(guess, realComputerVal);
        System.out.println(realComputerVal);

        //if (this.numGuesses != 0) {
            this.gameInfo.addNewPlayerGuess(guess, guessCount);
            //player won
            if (guessCount == 5) {
                //saves the game information to database
                gamesRepo.save(this.gameInfo);
                return new GuessResponse(error, guess, guessCount, null, -1, true, false);
            }
            ArrayList<Character> computerRemovedChars = (ArrayList<Character>) this.gameInfo.getComputerRemovedCharacters();
            ArrayList<Word> allComputerWords = new ArrayList<Word>();
            getComputerWords(computerRemovedChars, allComputerWords);
            allComputerWords = filterWords(allComputerWords);
            int numWords = allComputerWords.size();
            Random rand2 = new Random();
            int randomWordIndex = rand2.nextInt(numWords + 1);
            String computerGuess = allComputerWords.get(randomWordIndex).getWord();

            int computerGuessCount = getGuessCount(computerGuess, playerWord);
            //computer ai won
            if (computerGuessCount == 5) {
                //saves game info to database
                gamesRepo.save(this.gameInfo);
                return new GuessResponse(error, guess, guessCount, computerGuess, computerGuessCount, false, true);
            }

            this.gameInfo.addNewComputerGuess(computerGuess, computerGuessCount);
            this.gameInfo.calculateComputerCharacters();
            this.numGuesses = this.numGuesses + 1;
            return new GuessResponse(error, guess, guessCount, computerGuess, computerGuessCount, false, false);
        //} else {
         //   this.numGuesses = this.numGuesses + 1;
        //    return new GuessResponse(error, guess, guessCount, "", 0, false, false);
       // }

        /* COMPUTER AI LOGIC HERE

            right now computer picks random word from db
            implement the ai logic here

         */
    }

    //checks if a word has repeating letters
    public boolean checkForRepeatingChars(String word) {
        Set<Character> letters = new HashSet<>();
        for(char c : word.toCharArray()) {
            if(letters.contains(c)) return true;
            letters.add(c);
        }
        return false;
    }

    public ArrayList<Word> getFilteredWords(){
        ArrayList<Word> ans = new ArrayList<Word>();
        for(Word w: wordsRepo.findAll()){
            String wordVal = w.getWord();
            boolean clean = true;
            for(char c : wordVal.toCharArray()){
                if(wordVal.indexOf(c) != wordVal.lastIndexOf(c)){
                    clean = false;
                }
            }

            if(clean == true){
              ans.add(w);
            }
        }
        return ans;
    }

    public ArrayList<Word> filterWords(List<Word> inputList){
        ArrayList<Word> ans = new ArrayList<Word>();

        ArrayList<Word> inputList2 = (ArrayList<Word>) inputList;
        for(Word w: inputList2){
            String wordVal = w.getWord();
            boolean clean = true;
            for(char c : wordVal.toCharArray()){
                if(wordVal.indexOf(c) != wordVal.lastIndexOf(c)){
                    clean = false;
                }
            }

            if(clean == false){
                //inputList2.remove(w);
            }else{
                ans.add(w);
            }
        }

        return ans;
    }

    public int getGuessCount(String guess, String word) {
        int count = 0;
        for(char c : guess.toCharArray()) {
            if(word.indexOf(c) != -1) {
                count++;
            }
        }
        return count;
    }

    //validates if player guesses word is valid
    //also adds all 5 letter words into allWords list
    public boolean checkIfWordIsValid(String word, List<Word> allWords) {
        boolean valid = false;
        for (Word w : wordsRepo.findAll()){
            if(word.equals(w.getWord())) valid = true;
            allWords.add(w);
        }
        return valid;
    }

    public void getComputerWords(ArrayList<Character> removedChars, ArrayList<Word> allComputerWords){
        for(Word w: wordsRepo.findAll()){
            String wordVal = w.getWord();
            boolean clean = true;
            for(int i = 0; i < removedChars.size(); i++){
                Character currChar = removedChars.get(i);
                char c = currChar.charValue();
                if(wordVal.indexOf(c) != -1){
                    clean = false;
                }
            }
            if (clean == true){
                allComputerWords.add(w);
            }
        }

    }

    public void updateWordlist(String filepath, WordRepository wordsRepo){
        wordsRepo.deleteAll();
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                wordsRepo.save(new Word(line));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
