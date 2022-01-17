package server;

import card_packs.Deck;
import card_packs.card.CARDTYPE;
import card_packs.card.Card;
import card_packs.card.ELEMENT;

import java.util.Arrays;
import java.util.Random;

public class Battlefield {
    private String _username1;
    private String _username2;
    private final PostGre _db = new PostGre();
    private Deck _deck1;
    private Deck _deck2;
    int[] cards_taken_1 = new int[4];
    int[] cards_taken_2 = new int[4];

    public Battlefield(){
        Arrays.fill(cards_taken_1, 0);
        Arrays.fill(cards_taken_2, 0);
    }

    public void addDeck(Deck deck, String username){
        if(_deck1 == null){
            _deck1 = deck;
            _username1 = username;
        }else{
            if(_deck2 == null){
                _deck2 = deck;
                _username2 = username;
            }
        }
    }

    public boolean readyToBattle(){
        return _deck1 != null && _deck2 != null;
    }

    public String fight(){
        // create instance of Random class
        Random rand = new Random();

        Server.log(_username1 + " vs " + _username2 + "\n");
        //100 rounds max
        for(int i=0; i<100; i++){
            Server.log("Round " + (i+1) + "");
            // Generate random integers in range 1 to 4
            int rand_num;
            int card1, card2;

            while(true){
                rand_num = rand.nextInt(4) + 1;
                if(!isTaken(rand_num, 1)){
                    card1 = rand_num;
                    break;
                }
            }
            while(true){
                rand_num = rand.nextInt(4) + 1;
                if(!isTaken(rand_num, 2)){
                    card2 = rand_num;
                    break;
                }
            }
            fightCards(_deck1.getCard(card1), card1, _deck2.getCard(card2), card2);
            if(isOver()){
                return checkWinner();
            }
            if(i == 99){
                return checkWinner();
            }
        }
        return null;
    }

    public String checkWinner(){
        int cardsDown1, cardsDown2;
        cardsDown1 = getCardsTaken(1);
        cardsDown2 = getCardsTaken(2);
        cleanVar();
        if(cardsDown1 > cardsDown2){
            _db.updateScore(_username1, _username2, 1);
            return _username1 + " won this battle\n";
        }else if(cardsDown2 > cardsDown1){
            _db.updateScore(_username1, _username2, 2);
            return _username2 + " won this battle\n";
        }else{
            _db.updateScore(_username1, _username2, 0);
            return "This battle was a draw\n";
        }
    }

    private void cleanVar() {
        Arrays.fill(cards_taken_1, 0);
        Arrays.fill(cards_taken_2, 0);
        _deck1 = null;
        _deck2 = null;
    }

    public void fightCards(Card card1, int card1_num, Card card2, int card2_num) {
        int winner;
        Server.log(_username1 + " plays his card: " + card1.getName());
        Server.log(_username2 + " plays his card: " + card2.getName());
        if(card1.getCardType() == card2.getCardType()){
            if(card1.getCardType() == CARDTYPE.MONSTER){
                Server.log("Both cards are monster cards.");
                winner = compareCards(card1,card1_num, card2, card2_num);
            }else{
                Server.log("Both cards are spell cards.");
                winner = getWinner(card1, card1_num, card2, card2_num);
            }
        }else{
            Server.log("Cards are of different types.");
            Server.log(card1.getName() + "is of " + card1.getElementType());
            Server.log(card2.getName() + "is of " + card2.getElementType());
            winner = getWinner(card1, card1_num, card2, card2_num);
        }

        switch (winner) {
            case 0 -> Server.log("This round is a tie\n");
            case 1 -> Server.log(_username1 + " won this round.\n");
            case 2 -> Server.log(_username2 + " won this round.\n");
        }
    }

    private int getWinner(Card card1, int card1_num, Card card2, int card2_num) {
        int winner;
        if(card1.getElement() == ELEMENT.WATER && card2.getElement() == ELEMENT.FIRE){
            card1.doubleDamage();
            card2.halfDamage();
            winner = compareCards(card1,card1_num, card2, card2_num);
        }else if(card2.getElement() == ELEMENT.WATER && card1.getElement() == ELEMENT.FIRE){
            card2.doubleDamage();
            card1.halfDamage();
            winner = compareCards(card1,card1_num, card2, card2_num);
        }else if(card1.getElement() == ELEMENT.FIRE && card1.getElement() == ELEMENT.NORMAL){
            card1.doubleDamage();
            card2.halfDamage();
            winner = compareCards(card1,card1_num, card2, card2_num);
        }else if(card2.getElement() == ELEMENT.NORMAL && card1.getElement() == ELEMENT.FIRE) {
            card2.doubleDamage();
            card1.halfDamage();
            winner = compareCards(card1, card1_num, card2, card2_num);
        }
        else if(card1.getElement() == ELEMENT.NORMAL && card1.getElement() == ELEMENT.WATER){
            card1.doubleDamage();
            card2.halfDamage();
            winner = compareCards(card1,card1_num, card2, card2_num);
        }else if(card2.getElement() == ELEMENT.WATER && card1.getElement() == ELEMENT.NORMAL) {
            card2.doubleDamage();
            card1.halfDamage();
            winner = compareCards(card1, card1_num, card2, card2_num);
        }else{
            winner = compareCards(card1,card1_num, card2, card2_num);
        }
        return winner;
    }

    private int compareCards(Card card1, int card1_num, Card card2, int card2_num){
        if(card1.getDamage() > card2.getDamage()){
            occupyInt(card2_num, 2);
            return 1;
        }else if(card1.getDamage() < card2.getDamage()){
            occupyInt(card1_num, 1);
            return 2;
        }else{
            return 0;
        }
    }

    public void occupyInt(int number, int player){
        if(player == 1){
            for( int i=0; i<4; i++){
                if(cards_taken_1[i] == 0){
                    cards_taken_1[i] = number;
                    break;
                }
            }
        }else{
            for( int i=0; i<4; i++){
                if(cards_taken_2[i] == 0){
                    cards_taken_2[i] = number;
                    break;
                }
            }
        }
    }

    public boolean isTaken(int number, int player){
        if(player == 1){
            for( int i=0; i<4; i++){
                if(cards_taken_1[i] == number){
                    return true;
                }
            }
        }else{
            for( int i=0; i<4; i++){
                if(cards_taken_2[i] == number){
                    return true;
                }
            }
        }
        return false;
    }

    private int getCardsTaken(int player){
        int count = 0;
        if(player == 1){
            for(int i = 0; i<4; i++){
                if(cards_taken_1[i] != 0){
                    count += 1;
                }
            }
        }else{
            for(int i = 0; i<4; i++){
                if(cards_taken_2[i] != 0){
                    count += 1;
                }
            }
        }
        return count;
    }
    
    @SuppressWarnings("unused")
    public void showIntTaken(){
        for( int i=0; i<4; i++){
            System.out.print(cards_taken_1[i] + " ");
        }
        System.out.println();
        for( int i=0; i<4; i++){
            System.out.print(cards_taken_2[i] + " ");
        }
    }

    private boolean isOver(){
        for(int i=0; i<4; i++){
            if(cards_taken_1[i] == 0){
                return false;
            }
        }
        return true;
    }


}
