package edu.uci.inf122.guildquest.entities.playablecharacters;

import edu.uci.inf122.guildquest.content.Inventory;
import edu.uci.inf122.guildquest.entities.Entity;
import edu.uci.inf122.guildquest.entities.domain_primitives.*;
import edu.uci.inf122.guildquest.entities.interfaces.HasHealth;
import edu.uci.inf122.guildquest.entities.npcs.NPC;
import edu.uci.inf122.guildquest.content.items.Item;
import edu.uci.inf122.guildquest.ui.playablecharacteruis.PlayableCharacterUI;

import java.util.List;

public abstract class PlayableCharacter extends Entity implements HasHealth {
    private Health health;
    private final Level level;
    private final CharacterClass characterClass;
    private final Inventory inventory;
    private static final List<Move.ValidMoves> moves = List.of(
            Move.ValidMoves.TRAVEL,
            Move.ValidMoves.USE_ITEM,
            Move.ValidMoves.REQUEST_HINT,
            Move.ValidMoves.SKIP
            );



    private WithPrincess withPrincess;

    public PlayableCharacter(Name name, Health health, Level level, CharacterClass characterClass) {
        super(name);
        this.health = health;
        this.level = level;
        this.characterClass = characterClass;
        this.inventory = new Inventory();
        this.withPrincess = new WithPrincess(false);
    }

    @Override
    public abstract void act();

    public abstract void move();

    public abstract void receiveHealing(Amount amount);

    public abstract void takeDamage(Damage damage);

    /**
     * Heals this player.
     *
     * @param amount the amount to heal by.
     */
    public void heal(Amount amount) {
        health.increaseBy(amount);
    }

    public void heal(DecimalAmount amount) {
        health.increaseBy(amount);
    }

    protected void dealDamage(Entity target, Damage damage) {
        try {

            if (target instanceof PlayableCharacter playableTarget) {
                throw new IllegalArgumentException("Cannot attack another player character.");
            }

            if (target instanceof NPC npcTarget) {
                npcTarget.takeDamage(damage);
            }
        } catch (Exception e) {
            System.out.println("An error occurred while trying to deal damage: " + e.getMessage());
        }
    }

    @Override
    public Health getHealth() {
        return health;
    }

    public Level getLevel() {
        return level;
    }

    public CharacterClass getCharacterClass() {
        return characterClass;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void attack(Entity target) {
        throw new UnsupportedOperationException(
                getClass().getSimpleName() + " does not implement attack(Entity) yet.");
    }

    public boolean isDead() {
        return health.getHealth() <= 0;
    }

    public WithPrincess getWithPrincess() {
        return withPrincess;
    }

    public void pickUpPrincess(){
        withPrincess = withPrincess.startEscorting();
    }

    public abstract List<Move.ValidMoves> getMoves();

    public void addToInventory(Item e) {
        inventory.addItem(e);
    }

    public abstract PlayableCharacterUI getUI();
    public static List<Move.ValidMoves> defaultMoves() {
        return moves;
    }

}
