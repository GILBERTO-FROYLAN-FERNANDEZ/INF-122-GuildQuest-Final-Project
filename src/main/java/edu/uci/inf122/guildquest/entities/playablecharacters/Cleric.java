package edu.uci.inf122.guildquest.entities.playablecharacters;

import edu.uci.inf122.guildquest.entities.Entity;
import edu.uci.inf122.guildquest.entities.domain_primitives.*;
import edu.uci.inf122.guildquest.entities.interfaces.CanHealOther;
import edu.uci.inf122.guildquest.entities.interfaces.CanHealSelf;
import edu.uci.inf122.guildquest.ui.playablecharacteruis.ClericUI;
import edu.uci.inf122.guildquest.ui.playablecharacteruis.KnightUI;
import edu.uci.inf122.guildquest.ui.playablecharacteruis.PlayableCharacterUI;

import java.util.ArrayList;
import java.util.List;

public class Cleric extends PlayableCharacter implements CanHealSelf, CanHealOther {
    private static Cleric instance;
    private final DecimalAmount healingPower;

    private static final List<Move.ValidMoves> moves;
    static {
        ArrayList<Move.ValidMoves> temp = new ArrayList<>(defaultMoves());
        temp.add(1, Move.ValidMoves.HEAL_SELF);
        temp.add(1, Move.ValidMoves.HEAL_OTHER);
        moves=List.copyOf(temp);
    }

    protected Cleric(Name name) {
        super(name, new Health(90), new Level(1), new CharacterClass("Cleric"));
        this.healingPower = new DecimalAmount(20);
    }

    public static Cleric getInstance(Name name) {
        if (instance == null) {
            instance = new Cleric(name);
        }
        return instance;
    }

    @Override
    public void act() {
        // Placeholder for cleric-specific behavior.
    }

    @Override
    public void move() {
        // Placeholder for cleric-specific movement behavior.
    }

    @Override
    public void receiveHealing(Amount amount) {
        heal(amount);
        System.out.println(getName() + " receives " + amount + " healing. Current health: " + getHealth());
    }

    @Override
    public void takeDamage(Damage damage) {
        getHealth().reduceBy(damage);
        System.out.println(getName() + " takes " + damage + " damage. Remaining health: " + getHealth());

    }

    public void heal(Amount amount, PlayableCharacter target) {
        target.heal(amount);
        System.out.println(getName() + " heals "  + amount.getCount() + " health for "+ target.getName());
    }

    @Override
    public Amount getHealAmount() {
        return getHealingPower().toAmount();
    }

    public void attack(Entity target){
        System.out.println("Clerics are pacifists, and cannot attack");
    }

    public DecimalAmount getHealingPower() {
        return healingPower;
    }
    @Override
    public List<Move.ValidMoves> getMoves(){
        return moves;
    }

    @Override
    public void heal(int amount) {

    }
    @Override
    public ClericUI getUI() {
        return ClericUI.getClericUI(this);
    }
}