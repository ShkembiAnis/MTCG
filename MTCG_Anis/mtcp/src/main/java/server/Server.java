package server;

import card_packs.Deck;
import card_packs.Package;
import client.Client;

import java.io.*;
import java.net.Socket;
import java.util.*;


public class Server implements Runnable{
    private Battlefield _battle;
    private final PostGre _db = new PostGre();
    private BufferedReader _in;
    private BufferedWriter _out;
    private StringBuilder _messageSeparator = new StringBuilder();
    private Verb _myVerb = Verb.OTHER;
    private String _message;
    private String _payload;
    private String[] _command;
    private final Map<String, String> __header = new HashMap<>();
    private boolean _http_first_line = true;

    private final String[] _allowedReq = {"users", "sessions", "packages", "transactions",
            "cards", "deck", "stats", "score", "battles", "tradings", "deck?format=plain"};


    public Server() {

    }

    public Server(Socket clientSocket,Battlefield battle) throws IOException {
        this._in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this._out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        this._battle = battle;
        Thread t = new Thread(this, battle.toString());
        t.start();
    }


    protected void setMyVerb(String myVerb) {
        switch (myVerb) {
            case "GET" -> _myVerb = Verb.GET;
            case "POST" -> _myVerb = Verb.POST;
            case "PUT" -> _myVerb = Verb.PUT;
            case "DELETE" -> _myVerb = Verb.DELETE;
            default -> _myVerb = Verb.OTHER;
        }
    }

    private void separateMessage() {
        //separate message
        String[] request = _messageSeparator.toString().split(System.getProperty("line.separator"));
        _messageSeparator = new StringBuilder();
        for (String line : request) {
            if (!line.isEmpty()) {
                if (_http_first_line) {
                    //saving folder and version
                    String[] first_line = line.split(" ");
                    setMyVerb(first_line[0]);
                    _message = first_line[1];
//                    _version = first_line[2];
                    _http_first_line = false;
                } else {
                    //saving the header
                    if (!line.contains("{") && !line.contains("[") && !line.contains("\"")) {
                        String[] other_lines = line.split(": ");
                        __header.put(other_lines[0], other_lines[1]);
                    }
                    //saving the payload
                    else {
                        _messageSeparator.append(line);
                        _messageSeparator.append("\r\n");
                    }
                }
            }
        }
        _payload = _messageSeparator.toString();
    }

    public void readRequest() throws IOException {
        //read and save request
        //save request
        while (_in.ready()) {
            _messageSeparator.append((char) _in.read());
        }
        separateMessage();
        int status = checkRequest();
        performRequest(status);


    }

    @SuppressWarnings("unused")
    private void showHeader() {
        System.out.println("header:");
        for (Map.Entry<String, String> entry : __header.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }


    //0 - error occurred
    //1 - create account
    //2 - log in
    //3 - add new package
    //4 - buy package
    //5 - show cards from user
    //6 - show deck
    //7 - configure deck
    //8 - show deck other format
    //9 - show users data
    //10- set users data
    //11- show users stats
    //12- show scoreboard of user
    //13- enter battle
    private int checkRequest() {

        //check if command is supported
        if(_myVerb == Verb.OTHER) {
            System.out.println("srv: Request method not supported");
        } else {
            _command = _message.split("/");
            if(Arrays.asList(_allowedReq).contains(_command[1])) {
                if (_command[1].equals("users") && _myVerb == Verb.POST) {
                    return 1;
                }else if (_command[1].equals("sessions") && _myVerb == Verb.POST) {
                    return 2;
                }else if (_command[1].equals("packages") && _myVerb == Verb.POST) {
                    return 3;
                }else if(_command[1].equals("cards") && _myVerb == Verb.GET){
                    return 5;
                }else if(_command[1].equals("deck") && _myVerb == Verb.GET){
                    return 6;
                }else if(_command[1].equals("deck") && _myVerb == Verb.PUT){
                    return 7;
                }else if(_command[1].equals("deck?format=plain") && _myVerb == Verb.GET){
                    return 8;
                }else if(_command[1].equals("users") && _myVerb == Verb.GET){
                    return 9;
                }else if(_command[1].equals("users") && _myVerb == Verb.PUT){
                    return 10;
                }else if(_command[1].equals("stats") && _myVerb == Verb.GET){
                    return 11;
                }else if(_command[1].equals("score") && _myVerb == Verb.GET){
                    return 12;
                }else if(_command[1].equals("battles") && _myVerb == Verb.POST){
                    return 13;
                }
                if (_command.length == 3) {
                    if (_command[1].equals("transactions") && _command[2].equals("packages")) {
                        if (_myVerb == Verb.POST) {
                            return 4;
                        }
                    } else {
                        return 0;
                    }
                }
            }
        }
        return 0;
    }

    private void performRequest(int status) throws IOException {
        if (status == 0) {
            _out.write("HTTP/1.1 400\r\n");
            _out.write("Content-Type: text/html\r\n");
            _out.write("\r\n");
            _out.write("Bad request!\r\n");
        } else {
            _out.write("HTTP/1.1 200 OK\r\n");
            _out.write("Content-Type: text/html\r\n");
            _out.write("\r\n");
        }
        switch (status) {
            case 1 -> createUser(_payload);
            case 2 -> logInUser(_payload);
            case 3 -> savePackage(_payload);
            case 4 -> buyPackage();
            case 5 -> showStack();
            case 6 -> showDeck();
            case 7 -> configureDeck();
            case 8 -> showDeckOther();
            case 9 -> showUserData();
            case 10 -> setStats();
            case 11 -> showStats();
            case 12 -> showScoreboard();
            case 13 -> battle();
        }
        _out.flush();
    }

    private void battle() throws IOException {
        if(getUserInfoHeader() != null){
            String[] uname = getUserInfoHeader();
            if(isUserValid(uname[0], uname[1])){
                Deck deck = _db.getDeck(uname[0]);
                _battle.addDeck(deck, uname[0]);
                if(_battle.readyToBattle()){
                    String winner = _battle.fight();
                    _out.write(winner);
                }
            }else{
                _out.write("User is not valid");
            }
        }else{
            _out.write("No user entered.");
        }
    }

    //depending on requestMode
    private void createUser(String json) throws IOException {
        Client user = new Client(json);
        if (_db.registerUser(user) == 1) {
            _out.write("New user is created\n");
        } else {
            _out.write("Username already exists\n");
        }
    }

    private void logInUser(String json) throws IOException {
        Client user = new Client(json);
        _db.logInUser(user);
        if (_db.logInUser(user) == 0) {
            _out.write("Can't log user in\n");
        } else {
            _out.write("User is logged.\n");
        }
    }

    private void savePackage(String json) throws IOException {
        if(getUserInfoHeader() != null){
            String[] uname = getUserInfoHeader();
            if (uname[0].equals("admin")) {
                if(isUserValid(uname[0], uname[1])){
                    Package p = new Package(json);
                    if (p.isCreated()) {
                        p.savePackage();
                    }
                    _out.write("New Package is created");
                }else{
                    _out.write("Can not create package");
                }
            } else {
                _out.write("The user is not an admin");
            }
        }else{
            _out.write("No user entered.");
        }
    }

    private void buyPackage() throws IOException {
        if(getUserInfoHeader() != null) {
            String[] uname = getUserInfoHeader();
            if (isUserValid(uname[0], uname[1])) {
                if (_db.buyPackage(uname[0]) == 1) {
                    _out.write("Package is acquired");
                } else {
                    _out.write("Cannot buy package");
                }
            } else {
                _out.write("User is not valid");
            }
        }else{
            _out.write("No user entered.");
        }

    }

    private void showStack() throws IOException {
        if(getUserInfoHeader() != null){
            String[] uname = getUserInfoHeader();

            if(isUserValid(uname[0], uname[1])){
                String stack = _db.getStack(uname[0]);
                _out.write(stack);
            }else{
                _out.write("User is not valid");
            }
        }else{
            _out.write("No user entered.");
        }
    }

    private void showDeck() throws IOException {
        if(getUserInfoHeader() != null){
            String[] uname = getUserInfoHeader();
            if(isUserValid(uname[0], uname[1])){
                String deck = _db.getDeckString(uname[0], false);
                _out.write(deck);
            }else{
                _out.write("User is not valid");
            }
        }else {
            _out.write("No user entered.");
        }
    }

    private void showDeckOther() throws IOException {
        if(getUserInfoHeader() != null){
            String[] uname = getUserInfoHeader();
            if(isUserValid(uname[0], uname[1])){
                String deck = _db.getDeckString(uname[0], true);
                _out.write(deck);
            }else{
                _out.write("User is not valid");
            }
        }else {
            _out.write("No user entered.");
        }
    }

    private void configureDeck() throws IOException {
        if(getUserInfoHeader() != null){
            String[] uname = getUserInfoHeader();
            if(isUserValid(uname[0], uname[1])){
                if(_db.setDeck(_payload, uname[0])){
                    _out.write("Deck is reconfigured");
                }else{
                    _out.write("Cannot configure deck");
                }
            }else{
                _out.write("User is not valid");
            }
        }else {
            _out.write("No user entered.");
        }
    }

    private void setStats() throws IOException {
        if(getUserInfoHeader() != null){
            String[] uname = getUserInfoHeader();
            if(isUserValid(uname[0], uname[1]) && _command[2].equals(uname[0]) ){
                if(_db.setStats(_payload, uname[0])){
                    _out.write("user updated");
                }else{
                    _out.write("Something went wrong");
                }
            }else{
                _out.write("User is not valid");
            }
        }else{
            _out.write("No user entered.");
        }
    }

    private void showUserData() throws IOException {
        if(getUserInfoHeader() != null){
            String[] uname = getUserInfoHeader();
            if(isUserValid(uname[0], uname[1]) && _command[2].equals(uname[0]) ){
                String stats = _db.getUserData(uname[0]);
                _out.write(stats);
            }else{
                _out.write("User is not valid");
            }
        }else{
            _out.write("No user entered.");
        }
    }
    private void showStats() throws IOException {
        if(getUserInfoHeader() != null){
            String[] uname = getUserInfoHeader();
            if(isUserValid(uname[0], uname[1])){
                System.out.println(uname[0]);
                String stats = _db.getStats(uname[0]);
                _out.write(stats);
            }else{
                _out.write("User is not valid");
            }
        }else{
            _out.write("No user entered.");
        }
    }

    private void showScoreboard() throws IOException {
        if(getUserInfoHeader() != null){
            String[] uname = getUserInfoHeader();
            if(isUserValid(uname[0], uname[1])){
                String stats = _db.getScoreboard(uname[0]);
                _out.write(stats);
                _out.write("\n");
            }else{
                _out.write("User is not valid");
            }
        }else{
            _out.write("No user entered.");
        }
    }

    public static void log(String msg) {
        File file = new File("log.txt");

        // creates the file
        try {
            file.createNewFile();
            // creates a FileWriter Object
            FileWriter writer = new FileWriter(file, true);

            // Writes the content to the file
            writer.write("logged: " + msg + "\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private String[] getUserInfoHeader() {
        if(__header.get("Authorization") != null){
            String[] token = __header.get("Authorization").split(" ");
            return token[1].split("-");
        }
        return null;

    }

    private boolean isUserValid(String username, String token) {
        if (_db.isLogged(username)) {
            return token.contains("mtcgToken");

        } else {
            return false;
        }
    }

    @Override
    public void run() {

    }
}

