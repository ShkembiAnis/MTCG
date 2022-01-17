import card_packs.Deck;
import card_packs.Stack;
import card_packs.card.CARDTYPE;
import card_packs.card.Card;
import card_packs.card.ELEMENT;
import card_packs.Package;
import client.Client;
import org.junit.jupiter.api.Test;
import server.Battlefield;
import server.PostGre;

import static org.junit.jupiter.api.Assertions.*;

public class MtcpTest {
    public static Card monster = new Card("id1", "Dragon", 10.0);
    public static Card spell1 = new Card("id2", "FireSpell", 30.0);
    public static Card spell2 = new Card("id3", "WaterSpell", 11.0);
    public static Card spell3 = new Card("id4", "RegularSpell", 11.0);

    @Test
    void cleanAll(){
        PostGre db = new PostGre();
        db.deleteAll();
    }

    @Test
    void configureCardMonsterTest() {
        assertEquals(CARDTYPE.MONSTER.toString(), monster.isMonster());
        assertEquals(ELEMENT.NOT_SET.toString(), monster.getElementType());
    }

    @Test
    void configureCardSpellTest() {
        assertEquals(CARDTYPE.SPELL.toString(), spell1.isMonster());
        assertEquals(ELEMENT.FIRE.toString(), spell1.getElementType());
        assertEquals(CARDTYPE.SPELL.toString(), spell2.isMonster());
        assertEquals(ELEMENT.WATER.toString(), spell2.getElementType());
        assertEquals(CARDTYPE.SPELL.toString(), spell3.isMonster());
        assertEquals(ELEMENT.NORMAL.toString(), spell3.getElementType());
    }

    //check if only Packages with 5 Cards can be created
    @Test
    void packageTest() {
        Package ok = new Package("[{\\\"Id\\\":\\\"845f0dc7-37d0-426e-994e-43fc3ac83c08\\\", \\\"Name\\\":\\\"WaterGoblin\\\", \\\"Damage\\\": 10.0}, {\\\"Id\\\":\\\"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\\\", \\\"Name\\\":\\\"Dragon\\\", \\\"Damage\\\": 50.0}, {\\\"Id\\\":\\\"e85e3976-7c86-4d06-9a80-641c2019a79f\\\", \\\"Name\\\":\\\"WaterSpell\\\", \\\"Damage\\\": 20.0}, {\\\"Id\\\":\\\"1cb6ab86-bdb2-47e5-b6e4-68c5ab389334\\\", \\\"Name\\\":\\\"Ork\\\", \\\"Damage\\\": 45.0}, {\\\"Id\\\":\\\"dfdd758f-649c-40f9-ba3a-8657f4b3439f\\\", \\\"Name\\\":\\\"FireSpell\\\",    \\\"Damage\\\": 25.0}]");
        Package nok = new Package("[{\\\"Id\\\":\\\"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\\\", \\\"Name\\\":\\\"Dragon\\\", \\\"Damage\\\": 50.0}, {\\\"Id\\\":\\\"e85e3976-7c86-4d06-9a80-641c2019a79f\\\", \\\"Name\\\":\\\"WaterSpell\\\", \\\"Damage\\\": 20.0}, {\\\"Id\\\":\\\"1cb6ab86-bdb2-47e5-b6e4-68c5ab389334\\\", \\\"Name\\\":\\\"Ork\\\", \\\"Damage\\\": 45.0}, {\\\"Id\\\":\\\"dfdd758f-649c-40f9-ba3a-8657f4b3439f\\\", \\\"Name\\\":\\\"FireSpell\\\",    \\\"Damage\\\": 25.0}]");
        assertTrue(ok.isCreated());
        assertFalse(nok.isCreated());
    }

    @Test
    void deckTest() {
        //3 cards inserted, should not work -> return 1
        Deck deck = new Deck();
        deck.append(monster);
        deck.append(spell1);
        deck.append(spell2);
        assertEquals(1, deck.checkDeckCreated());

        //4 cards inserted, should work -> return 0
        Deck deck2 = new Deck();
        deck2.append(monster);
        deck2.append(spell1);
        deck2.append(spell2);
        deck2.append(spell3);
        assertEquals(0, deck2.checkDeckCreated());
//        deck.showDeck();
    }

    @Test
    void stackAddPackageTest() {
        Package p_works = new Package("[{\\\"Id\\\":\\\"845f0dc7-37d0-426e-994e-43fc3ac83c08\\\", \\\"Name\\\":\\\"WaterGoblin\\\", \\\"Damage\\\": 10.0}, {\\\"Id\\\":\\\"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\\\", \\\"Name\\\":\\\"Dragon\\\", \\\"Damage\\\": 50.0}, {\\\"Id\\\":\\\"e85e3976-7c86-4d06-9a80-641c2019a79f\\\", \\\"Name\\\":\\\"WaterSpell\\\", \\\"Damage\\\": 20.0}, {\\\"Id\\\":\\\"1cb6ab86-bdb2-47e5-b6e4-68c5ab389334\\\", \\\"Name\\\":\\\"Ork\\\", \\\"Damage\\\": 45.0}, {\\\"Id\\\":\\\"dfdd758f-649c-40f9-ba3a-8657f4b3439f\\\", \\\"Name\\\":\\\"FireSpell\\\",    \\\"Damage\\\": 25.0}]");
        Package p_n_works = new Package("[{\\\"Id\\\":\\\"4a2757d6-b1c3-47ac-b9a3-91deab093531\\\", \\\"Name\\\":\\\"Dragon\\\", \\\"Damage\\\": 55.0}, {\\\"Id\\\":\\\"4a2757d6-b1c3-47ac-b9a3-91deab093531\\\", \\\"Name\\\":\\\"Dragon\\\", \\\"Damage\\\": 55.0}, {\\\"Id\\\":\\\"91a6471b-1426-43f6-ad65-6fc473e16f9f\\\", \\\"Name\\\":\\\"WaterSpell\\\", \\\"Damage\\\": 21.0}, {\\\"Id\\\":\\\"4ec8b269-0dfa-4f97-809a-2c63fe2a0025\\\", \\\"Name\\\":\\\"Ork\\\", \\\"Damage\\\": 55.0}]");

        Stack stc = new Stack();
        stc.appendPackage(p_works);
        stc.appendPackage(p_n_works);
        assertEquals(5, stc.getCardCount());

        Stack stc2 = new Stack();
        stc2.appendPackage(p_works);
        stc2.appendPackage(p_works);
        assertEquals(10, stc2.getCardCount());
    }

    @Test
    void userRegisterTest() {
        PostGre db = new PostGre();
        Client user = new Client("{\"Username\":\"test\", \"Password\":\"test\"}");
        assertEquals(1, db.registerUser(user));
        assertEquals(0, db.registerUser(user));

    }

    @Test
    void deleteUserTest() {
        PostGre db = new PostGre();
        Client user = new Client("{\"Username\":\"test\", \"Password\":\"test\"}");
        assertEquals(1, db.deleteUser(user));
        Client user2 = new Client("{\"Username\":\"tes234t\", \"Password\":\"tes234t\"}");
        assertEquals(0, db.deleteUser(user2));
    }

    @Test
    void userLogInTest() {
        PostGre db = new PostGre();
        Client user = new Client("{\"Username\":\"test2\", \"Password\":\"tes2\"}");
        Client user2 = new Client("{\"Username\":\"test3\", \"Password\":\"test3\"}");
        db.registerUser(user);
        assertEquals(1, db.logInUser(user));
        assertEquals(0, db.logInUser(user2));
        db.deleteUser(user);
    }

    @Test
    void userLoggedTest() {
        PostGre db = new PostGre();
        Client user = new Client("{\"Username\":\"login\", \"Password\":\"login\"}");
        assertEquals(1, db.registerUser(user));
        assertEquals(1, db.logInUser(user));
        assertTrue(db.isLogged(user.getUsername()));
        db.deleteUser(user);
    }

    @Test
    void getIdFromUserTest(){
        PostGre db = new PostGre();
        Client user = new Client("{\"Username\":\"login\", \"Password\":\"login\"}");
        db.registerUser(user);
        db.logInUser(user);
        assertTrue(db.getIdFromUsername("login")>0);
        db.deleteUser(user);
    }

    @Test
    void decreaseCoinsFromUserTest(){
        PostGre db = new PostGre();
        Client user = new Client("{\"Username\":\"login\", \"Password\":\"login\"}");
        db.registerUser(user);
        db.logInUser(user);
        assertEquals(1, db.decreaseCoinsFromUser("login"));
        db.deleteUser(user);
    }


    @Test
    void savePackageTest(){
        PostGre db = new PostGre();
        Client user = new Client("{\"Username\":\"login\", \"Password\":\"login\"}");
        db.registerUser(user);
        db.logInUser(user);
        Package p = new Package("[{\\\"Id\\\":\\\"845f0dc7-37d0-426e-994e-43fc3ac83c08\\\", \\\"Name\\\":\\\"WaterGoblin\\\", \\\"Damage\\\": 10.0}, {\\\"Id\\\":\\\"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\\\", \\\"Name\\\":\\\"Dragon\\\", \\\"Damage\\\": 50.0}, {\\\"Id\\\":\\\"e85e3976-7c86-4d06-9a80-641c2019a79f\\\", \\\"Name\\\":\\\"WaterSpell\\\", \\\"Damage\\\": 20.0}, {\\\"Id\\\":\\\"1cb6ab86-bdb2-47e5-b6e4-68c5ab389334\\\", \\\"Name\\\":\\\"Ork\\\", \\\"Damage\\\": 45.0}, {\\\"Id\\\":\\\"dfdd758f-649c-40f9-ba3a-8657f4b3439f\\\", \\\"Name\\\":\\\"FireSpell\\\",    \\\"Damage\\\": 25.0}]");
        if (p.isCreated()) {
            p.savePackage();
        }
        assertTrue(db.getMaxId()>0);
        db.deleteUser(user);
    }

    @Test
    void deletePackageTest(){
        PostGre db = new PostGre();
        Client user = new Client("{\"Username\":\"login\", \"Password\":\"login\"}");
        db.registerUser(user);
        db.logInUser(user);
        assertEquals(1, db.deleteCardsFromPackage(db.getMaxId()));
        db.deleteUser(user);
    }

    @Test
    void buyPackageTest(){
        PostGre db = new PostGre();
        Client user = new Client("{\"Username\":\"login\", \"Password\":\"login\"}");
        db.registerUser(user);
        db.logInUser(user);
        Package p = new Package("[{\\\"Id\\\":\\\"845f0dc7-37d0-426e-994e-43fc3ac83c08\\\", \\\"Name\\\":\\\"WaterGoblin\\\", \\\"Damage\\\": 10.0}, {\\\"Id\\\":\\\"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\\\", \\\"Name\\\":\\\"Dragon\\\", \\\"Damage\\\": 50.0}, {\\\"Id\\\":\\\"e85e3976-7c86-4d06-9a80-641c2019a79f\\\", \\\"Name\\\":\\\"WaterSpell\\\", \\\"Damage\\\": 20.0}, {\\\"Id\\\":\\\"1cb6ab86-bdb2-47e5-b6e4-68c5ab389334\\\", \\\"Name\\\":\\\"Ork\\\", \\\"Damage\\\": 45.0}, {\\\"Id\\\":\\\"dfdd758f-649c-40f9-ba3a-8657f4b3439f\\\", \\\"Name\\\":\\\"FireSpell\\\",    \\\"Damage\\\": 25.0}]");
        if (p.isCreated()) {
            p.savePackage();
        }
        assertEquals(1, db.buyPackage("login"));
        db.deleteAll();
    }

    @Test
    void getDeckTest(){
        PostGre db = new PostGre();
        Client user = new Client("{\"Username\":\"login\", \"Password\":\"login\"}");
        db.logInUser(user);
        assertEquals("Deck is empty (not configured)", db.getDeckString("login", false));
    }

    @Test
    void setDeckTest(){
        PostGre db = new PostGre();
        Client user = new Client("{\"Username\":\"login\", \"Password\":\"login\"}");
        db.registerUser(user);
        db.logInUser(user);
        Package p = new Package("[{\\\"Id\\\":\\\"845f0dc7-37d0-426e-994e-43fc3ac83c08\\\", \\\"Name\\\":\\\"WaterGoblin\\\", \\\"Damage\\\": 10.0}, {\\\"Id\\\":\\\"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\\\", \\\"Name\\\":\\\"Dragon\\\", \\\"Damage\\\": 50.0}, {\\\"Id\\\":\\\"e85e3976-7c86-4d06-9a80-641c2019a79f\\\", \\\"Name\\\":\\\"WaterSpell\\\", \\\"Damage\\\": 20.0}, {\\\"Id\\\":\\\"171f6076-4eb5-4a7d-b3f2-2d650cc3d237\\\", \\\"Name\\\":\\\"Ork\\\", \\\"Damage\\\": 45.0}, {\\\"Id\\\":\\\"dfdd758f-649c-40f9-ba3a-8657f4b3439f\\\", \\\"Name\\\":\\\"FireSpell\\\",    \\\"Damage\\\": 25.0}]");
        if (p.isCreated()) {
            p.savePackage();
        }
        db.buyPackage("login");
        assertFalse(db.setDeck("['SA','asd','ads','asd']", "login"));
        assertTrue(db.setDeck("['845f0dc7-37d0-426e-994e-43fc3ac83c08', '99f8f8dc-e25e-4a95-aa2c-782823f36e2a', 'e85e3976-7c86-4d06-9a80-641c2019a79f', 'dfdd758f-649c-40f9-ba3a-8657f4b3439f']", "login"));
        db.deleteAll();
    }

    @Test
    void showIntTakenTest(){
        Battlefield battle = new Battlefield();
        battle.fightCards(spell1, 3, spell1, 4);
    }


}
