package card_packs;

import card_packs.card.Card;

import java.util.ArrayList;
import java.util.Arrays;

public class Stack {
    private static final ArrayList<Card> _stackCards = new ArrayList<>();
    private int _cardCount = 0;

    public Stack(){
    }

    public String getStack(){
        StringBuilder str = new StringBuilder("Stack: \n");
        for (Card stackCard : _stackCards) {
            str.append(stackCard.getId()).append(": ");
            str.append(stackCard.getName()).append(" - ");
            str.append(stackCard.getDamage()).append("dmg - ");
            str.append(stackCard.getElementType()).append("\n");
        }
        return str.toString();
    }

    public void addCard(String id, String name, double damage){
        Card card = new Card(id, name, damage);
        _stackCards.add(card);
    }

    public void appendPackage(Package p_package) {
        if( p_package.isCreated()) {
            _stackCards.addAll(Arrays.asList(p_package.getPackage()));
            _cardCount += 5;
        }
    }

    public int getCardCount(){
        return _cardCount;
    }
}
