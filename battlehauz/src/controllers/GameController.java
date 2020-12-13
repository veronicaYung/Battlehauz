package controllers;

import models.Move;
import models.Shop;
import models.gameCharacters.Player;
import models.gameCharacters.enemy.Calcifer;
import models.gameCharacters.enemy.Dragon;
import models.gameCharacters.enemy.Enemy;
import models.gameCharacters.enemy.Ogre;
import models.items.Item;
import models.utilities.Turn;
import models.utilities.WordsHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;


/* move starts with Player choosing move/item
 if move, then call performTurn(int index, boolean isMove), which calls:
    - chooseMove(int index): returns Move at specified index
    -

 */

//remember, a controller is there so that we don't assume where the user input is coming from.
//Straight from Lanthier's notes, we assume there is no System.out.println or scanner anywhere but here.
public class GameController {

    private int currentFloor;
    private final Queue<Enemy> enemiesToFight;
    private Enemy currentEnemy;
    private Player gamePlayer;
    private final Shop shop;
    private boolean playersTurn = false;

    public GameController() throws IOException {
        shop = new Shop();
        enemiesToFight = new LinkedList<>();
        currentFloor = 1;
    }

    //**************************[Getter methods]*********************************************

    public Player getGamePlayer() {
        return this.gamePlayer;
    }

    public Shop getShop() {
        return shop;
    }

    public String start(String name) {
        this.gamePlayer = new Player(name);
        return "Welcome to the Battlehauz, " + name + "!";
    }

    public String doPlayerTurn(int moveIndex) throws IndexOutOfBoundsException {
        moveIndex--;
        Turn currentTurn = gamePlayer.performTurn(moveIndex, currentEnemy);
        playerTurnEnd();
        return "You " + currentTurn.toStringMove();
    }

    public int playerCurrentLevel(){
        return gamePlayer.calculateLevel();
    }

    public String playerUseItem(int itemIndex) {
        Turn currentTurn = gamePlayer.useItem(itemIndex);
        playerTurnEnd();
        return "You " + currentTurn.toStringItem();
    }

    public String doEnemyTurn() {
        Turn currentTurn = currentEnemy.performTurn(currentEnemy.generateMoveIndex(), gamePlayer);
        playerTurnStart();
        return currentEnemy.getName() + " " + currentTurn.toStringMove();
    }

    private int numberOfEnemies() {
        return currentFloor + 2;
    }

    private void generateEnemyQueue() {
        enemiesToFight.clear();
        int numEnemies = numberOfEnemies();
        Enemy nextEnemy;
        for (int i = 1; i <= numEnemies; i++) {
            if (i % 3 == 0) {
                nextEnemy = new Dragon(WordsHelper.generateEnemyName(), currentFloor);
            }// Dragon case
            else if (i % 3 == 2) {
                nextEnemy = new Calcifer(WordsHelper.generateEnemyName(), currentFloor);
            }//Calcifer case
            else {
                nextEnemy = new Ogre(WordsHelper.generateEnemyName(), currentFloor);
            }// Ogre case
            enemiesToFight.add(nextEnemy);
        }
    }

    public String startBattle() {
        currentEnemy = enemiesToFight.remove();
        playerTurnStart();
        return "You have encountered the " + currentEnemy.toString() + "!";
    }

    public void playerTurnStart() {
        playersTurn = true;
    }

    public void playerTurnEnd() {
        playersTurn = false;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void enterBattleFloor() {
        shop.setPotionBoostPurchased(false);
        generateEnemyQueue();
    }

    public boolean playerIsAlive() {
        return gamePlayer.isAlive();
    }

    public boolean isPlayersTurn() {
        return playersTurn;
    }

    public boolean currentEnemyIsAlive() {
        return currentEnemy.isAlive();
    }

    public boolean hasMoreEnemies() {
        return !enemiesToFight.isEmpty();
    }

    public String displayRules() {
        return """

                The rules of the Battlehauz are simple. You fight an increasing number of enemies at each floor (one-by-one).
                You and the enemy take turns choosing a move (you also have the option to choose an item to help your next move instead of a move).
                You keep fighting enemies and moving up floors until you run out of health.
                (A more dark way of saying it: the only way to leave the Battlehauz is DEATH). Have fun :)
                """;
    }

    public String displayPlayersMovesForShop() {
        ArrayList<Move> moves = gamePlayer.getMoves();
        StringBuilder buffer = new StringBuilder();
        int moveIndex = 1;
        for (Move m : moves) {
            buffer.append(moveIndex).append(": ").append(m.getShopSummary()).append("\n");
            moveIndex++;
        }
        return buffer.toString();
    }

    public String displayPlayersMoves() {
        ArrayList<Move> moves = gamePlayer.getMoves();
        StringBuilder buffer = new StringBuilder();
        int moveIndex = 1;
        for (Move m : moves) {
            buffer.append(moveIndex).append(": ").append(m).append("\n");
            moveIndex++;
        }
        return buffer.toString();
    }

    public String displayPlayerOptions() {
        return """
                1. Attack
                2. Use Item
                """;
    }

    public String displayPlayerInventory() {
        if (gamePlayer.getOwnedItemNames().size() == 0) return "You have no items you can use!";
        ArrayList<Item> items = gamePlayer.getOwnedItemNames();
        HashMap<Item, Integer> itemsQuantity = gamePlayer.getItems();
        StringBuilder buffer = new StringBuilder();
        int itemIndex = 1;
        for (Item i : items) {
            buffer.append(itemIndex).append(": ").append(i.getShopSummary()).append(" | Quantity: ").append(itemsQuantity.get(i)).append("\n");
            itemIndex++;
        }
        return buffer.toString();
    }

    public String displayCurrentFightersStatus() {
        return "Your " + gamePlayer.currentFighterStatus() + "\n" + displayEnemyStatus();
    }

    public String displayEnemyStatus() {
        return currentEnemy.getName() + "'s " + currentEnemy.currentFighterStatus();
    }

    public String displayStats() {
        return gamePlayer.toString();
    }

    public void restorePlayerHealth() {
        gamePlayer.fullRestore();
    }

    public void nextFloor() {
        currentFloor++;
    }

    public String enemyLoss() {
        return enemyTalk('L') ;
    }

    public String displayPlayerRewards() {
        int coins = increasePlayerCoins();
        int initialLevel = gamePlayer.calculateLevel();
        int xp = increasePlayerXP();
        return "You got " + coins + " coins and " + xp + "XP!";}

    private int increasePlayerCoins() {
        int coins;
        switch (enemiesToFight.size()) {
            case 0 -> coins = 25 * currentFloor;
            case 1 -> coins = 20 * currentFloor;
            case 2 -> coins = 15 * currentFloor;
            default -> coins = 10 * currentFloor;
        }
        gamePlayer.increaseCoins(coins);
        return coins;
    }

    private int increasePlayerXP() {
        int xp;
        switch (enemiesToFight.size()) {
            case 0 -> xp = 300 * currentFloor;
            case 1 -> xp = 200 * currentFloor;
            case 2 -> xp = 150 * currentFloor;
            default -> xp = 100 * currentFloor;
        }
        gamePlayer.increaseXP(xp);
        return xp;
    }

    public String enemyTalk(char mode) {
        return currentEnemy.getName() + ": " + currentEnemy.speak(mode);
    }

    public String displayLevelUp(int initialLevel){
        return gamePlayer.displayLevelUp(initialLevel);
    }

    //*************************************************[this is where the shop functions start]*************************************************
    public String enterShop() {
        shop.enterShop(gamePlayer);
        return "Your character enters the shop. Here, they see the shopkeeper: Dave. Welcome!";
    }

    public String displayShopOptions() {
        return ("""
                1. Buy a move
                2. Buy a consumable item
                3. Buy a potion boost
                4. Sell a move back to the shop.
                5. Sell a consumable item back to the shop.
                6. Speak with shopkeeper
                7. Return back to the main menu.""");
    }

    public String displayMovesInShop() {
        return shop.displaySummaryofMovesInShop();
    }

    public String displayConsumableItemsInShop() {
        return shop.displaySummaryofConsumableItemsInShop();
    }

    public String displayPotionBoostsInShop() {
        return shop.displaySummaryOfPotionBoostsInShop();
    }

    public boolean boostHasNotBeenPurchasedCheck() {
        return !shop.isPotionBoostPurchased();
    }

    public void setPotionBoostUsed(boolean b){
        shop.setPotionBoostPurchased(!b);
    }

    public int getSizeOfDisplayedMenu(int i) {
        if (i <= 3) {
            return shop.getSizeOfShopInventory(i);
        } else if (i == 4) {
            return gamePlayer.getMoves().size();
        } else if (i == 5){
            return gamePlayer.getOwnedItemNames().size();
        }
        return 0;
    }

    public String buyMove(int index) {
        return shop.purchaseMoveAtIndex(index - 1);
    }

    public String buyConsumableItem(int index) {
        return shop.purchaseConsumableItemAtIndex(index - 1);
    }

    public String buyPotionBoost(int index) {
        return shop.purchasePotionBoostAtIndex(index - 1);
    }

    public String sellMoveToShop(int index) {
        return shop.buyBackMove(index - 1);
    }

    public String sellItemToShop(int index) {
        return shop.buyBackItem(index - 1);
    }

    public String displayShopkeeperConversation(){
        return shop.getRandomDialogue();
    }

    //*************************************************[this is where the shop functions end]*************************************************

    public String credits() {
        return """
                BattleHauz made with <3 by:
                Aaditya Chopra
                Elias Hawa\s
                Veronica Yung
                Zara Ali""";
    }
}




