package card_packs.card;

public class Card {

    private final String __name;
    private String __id;
    private double __damage;
    private ELEMENT __elementType;
    private CARDTYPE __cardType;

    public Card() {
        this.__damage = 10.0;
        this.__name = "defaultCard";
        this.__elementType = ELEMENT.NOT_SET;
        __cardType = CARDTYPE.NOT_SET;
    }

    public Card(String id, String name, double damage) {
            this.__name = name;
            this.__damage = damage;
            configureCard(id, name);
    }

    public void configureCard(String id, String name){
        if(name.contains("Fire") || name.contains("Water") || name.contains("Regular")){
            if(name.contains("Spell")){
                __cardType = CARDTYPE.SPELL;
                if(name.contains("Fire")){
                    __elementType = ELEMENT.FIRE;
                }else if(name.contains("Water")){
                    __elementType = ELEMENT.WATER;
                }else{
                    __elementType = ELEMENT.NORMAL;
                }
            }else{
                __elementType = ELEMENT.NOT_SET;
                __cardType = CARDTYPE.MONSTER;
            }
        }else{
            __cardType = CARDTYPE.MONSTER;
            __elementType = ELEMENT.NOT_SET;
        }
        __id = id;
    }

    public String getName() {
        return __name;
    }

    public String getId(){
        return __id;
    }

    public double getDamage() {
        return __damage;
    }

    public void doubleDamage() {
        __damage *= 2;
    }

    public void halfDamage() {
        __damage *= 0.5;
    }


    public String getElementType(){
        return __elementType.toString();
    }

    public ELEMENT getElement(){
        return __elementType;
    }

    public String isMonster(){
        return __cardType.toString();
    }

    public CARDTYPE getCardType() {
        return __cardType;
    }
}
