package models.gameCharacters.enemy;

import interfaces.Battleable;
import models.Move;
import models.gameCharacters.GameCharacter;
import models.Turn;
import utilities.WordsHelper;

import java.util.Random;

public abstract class Enemy extends GameCharacter implements Battleable {
    protected int level;

    public Enemy(String name, int maxHealth, int level) {
        super(name, maxHealth * level);
        this.level = level;

    }

    /***
     * randomly generates the order of enemy moves
     * used by chooseMove method
     * @return returns the index of move
     */
    public int generateMoveIndex() {
        int upperbound = getMoves().size();
        Random rand = new Random();
        return rand.nextInt(upperbound);
    }

    /***
     * randomly generates probability of hitting player
     * @return boolean that determines if the attack is successful
     */
    public abstract boolean attackSuccessful();

    /***
     * generates a random number proportional to player's level
     * This is used to calculate the damage of enemy moves when they are created
     * @return returns the damage points of an enemy's move
     */
    public int generateDamage() {
        int max = 175 * level;
        int min = 50 * level;
        return (int) (Math.random() * (max - min) + min);
    }

    /***
     * updates enemy's health
     * @param damage the amount of damage points that will be taken from enemy's health
     */
    public void takeDamage(int damage) {
        if (this.getCurrentHealth() >= damage) {
            this.setCurrentHealth(this.getCurrentHealth() - damage);
        }
        else{
            this.setCurrentHealth(0);
        }
    }

    /***
     *
     * @param mode used to get appropriate quote from appropriate list
     * @return random generated quote
     */
    public String speak(char mode){
        return WordsHelper.generateQuote(mode);
    }

    /***
     * steps to decide if enemy will deal damage
     * @param moveIndex used to get move
     * @param opponent  used to direct damage onto opponent
     * @return Turn summary class
     */
    @Override
    public Turn performTurn(int moveIndex, GameCharacter opponent) {
        Move nextMove = this.chooseMove(moveIndex);
        nextMove.updateMove();
        boolean s = this.attackSuccessful();
        if (s) {
            opponent.takeDamage(this.generateDamage());
        }
        return new Turn(nextMove, s);
    }

    /***
     * name generator with their health
     * @return enemy type with specific name of enemy and its current health
     */
    @Override
    public String toString(){
        return getClass().getSimpleName() +  " of the name " + this.getName() + ", with " + this.getCurrentHealth() + " health";
    }

}
