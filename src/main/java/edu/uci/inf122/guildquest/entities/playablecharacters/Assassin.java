package edu.uci.inf122.guildquest.entities.playablecharacters;

import edu.uci.inf122.guildquest.entities.Entity;
import edu.uci.inf122.guildquest.entities.domain_primitives.*;
import edu.uci.inf122.guildquest.entities.interfaces.AttackMove;
import edu.uci.inf122.guildquest.entities.interfaces.CardinalTravelMove;
import edu.uci.inf122.guildquest.ui.TerminalGrid;
import edu.uci.inf122.guildquest.ui.playablecharacteruis.AssassinUI;
import edu.uci.inf122.guildquest.ui.playablecharacteruis.PlayableCharacterUI;

import java.util.List;

public class Assassin extends PlayableCharacter implements AttackMove, CardinalTravelMove {
    private static Assassin instance;
    private final DecimalAmount criticalHitChance;
    private final Amount attackPower;
    private static final List<ValidMoves> moves = defaultMoves();

    private Assassin(Name name) {
        super(name, new Health(80), new Level(1), new CharacterClass(new Name("Assassin")));
        this.criticalHitChance = new DecimalAmount(0.50);
        this.attackPower = new Amount(20);
    }

    public static Assassin getInstance(Name name) {
        if (instance == null) {
            instance = new Assassin(name);
        }
        return instance;
    }

    @Override
    public void act() {
        // Placeholder for assassin-specific behavior.
    }

    @Override
    public void move() {
        // Placeholder for assassin-specific movement behavior.
    }

    @Override
    public void receiveHealing(Amount amount) {
        heal(amount);
        System.out.println(getName() + " receives " + amount.getCount() + " healing. Current health: " + getHealth());
    }

    @Override
    public void takeDamage(Damage damage) {
        getHealth().reduceBy(damage);
        System.out.println(getName() + " takes " + damage.getDamage() + " damage. Remaining health: " + getHealth());
    }

    @Override
    public void attack(Entity target) {
        Damage damageDealt = new Damage(attackPower);
        Damage modified = damageDealt;
        if (Math.random() > criticalHitChance.getCount()) {
            modified = new Damage(damageDealt.getDamage().multiply(2));
            System.out.println(getName() + " lands a critical hit!");
        }
        dealDamage(target, modified);
    }

    public DecimalAmount getCriticalHitChance() {
        return criticalHitChance;
    }

    public DecimalAmount getCriticalHitChanceDisplay() {
        DecimalAmount display = criticalHitChance.multiply(100);
        return display;
    }

    public Amount getAttackPower() {
        return attackPower;
    }
    @Override
    public void execute(TerminalGrid state, ValidMoves move) {
    }
    public List<Move.ValidMoves> getMoves(){
        return moves;
    }

    @Override
    public AssassinUI getUI() {
        return AssassinUI.getAssassinUI(this);
    }
}