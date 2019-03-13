var stompClient = null;
var playerWord = null;
var computerWordID = null;

function connect() {
    var socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/turn/computer', function (guessRes) {
            var res = JSON.parse(guessRes.body);
            handleGuessResponse(res.error, res.word, res.guessCount, res.computerGuess, res.computerGuessCount, res.playerWins, res.computerWins);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function sendGuess() {
    var guess = {
        'word': $("#word").val(),
        'playerWord': playerWord,
        'computerWordID': computerWordID
    }
    stompClient.send("/guess", {}, JSON.stringify(guess));
}

function handleGuessResponse(error, word, guessCount, computerGuess, computerGuessCount, playerWins, computerWins) {
    if(playerWins) {
        alert("YOU WON!");
        redirectToEndResults(0);
        return;
    }
    if(computerWins) {
        alert("COMPUTER WON...");
        redirectToEndResults(1);
        return;
    }

    $("#error").text(error);
    if(error === "") {
        var wordSpans = "";
        for(var i = 0; i < word.length; i++) {
            wordSpans = wordSpans + "<span>"+word.charAt(i)+"</span>";
        }
        // $("#player-guesses ul").append("<li>" + word + " "+guessCount+ "</li>");
        $("#player-guesses ul").append("<li>" + wordSpans + " "+guessCount+ "</li>");

        $("#computer-guesses ul").append("<li>" + computerGuess + " "+computerGuessCount+ "</li>");
    }
}

function redirectToEndResults(winner) {
    disconnect();
    window.location.href = '/end?win='+winner;
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    connect();
    playerWord = $("#player-word").val();
    computerWordID = $("#computer-word-id").val();
    $( "#send" ).click(function() { sendGuess(); });
});
