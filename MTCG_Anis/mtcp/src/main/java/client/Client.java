package client;
import org.json.JSONObject;


public class Client{

    //clients info
    private String _username;
    private String _password;
    private String _name;
    private int _coins;
    private int _eloRating;
    private boolean _logged;
    private final String _bio;
    private final String _img;


    public Client(){
        _coins = 20;
        _eloRating = 100;
        _logged = false;
        _bio = "hello";
        _img = ":)";
    }

    public Client(String json){
        this();
        //int i = json.indexOf("{");
        //json = json.substring(i);
        JSONObject _jsonUser = new JSONObject(json);
        _username = _jsonUser.getString("Username");
        _name = _username;
        _password = _jsonUser.getString("Password");
    }

    public Client(String username, String bio, String img, String name){
        _username = username;
        _bio = bio;
        _img = img;
        _name = name;
    }


    public int getEloRating() {        return _eloRating;
    }

    public boolean isLogged() {
        return _logged;
    }

    public String getUsername() {
        return _username;
    }

    public String getPassword() {
        return _password;
    }

    public String getBio() {
        return _bio;
    }

    public String getImg() {
        return _img;
    }

    //show client profile
    @SuppressWarnings("unused")
    public void showStats(){
        System.out.println("Users profile:");
        System.out.println("\r\tUsername: " + _username);
        System.out.println("\r\tCoins: " + _coins);
        System.out.println("\r\tElo Rating: " + _eloRating);
        System.out.println("\r\tBio: " + _bio);
        System.out.println("\r\tElo Img: " + _img);
    }

    public String getStats(){
        return "Users stats:\n" + "\n\tName: " + _name +
                "\n\tCoins: " + _coins +
                "\n\tElo Rating: " + _eloRating +
                "\n\tBio: " + _bio +
                "\n\tImg: " + _img + "\n";
    }

    public String getUserDate(){
        return "Users data:\n" + "\n\tName: " + _name +
                "\n\tBio: " + _bio +
                "\n\tImg: " + _img + "\n";
    }

    public int getCoins(){
        return _coins;
    }

    public void setCoins(int coins) {
        this._coins = coins;
    }

    public void setEloRating(int eloRating) {
        this._eloRating = eloRating;
    }

    public String getName() {
        return _name;
    }
}
