package edu.uci.inf122.guildquest.entities.npcs;

import edu.uci.inf122.guildquest.entities.Entity;
import edu.uci.inf122.guildquest.entities.domain_primitives.Amount;
import edu.uci.inf122.guildquest.entities.playablecharacters.PlayableCharacter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Hostile interface for NPC classes that have hostile behavior
 */
public interface Hostile {
    Random random=new Random();
    int attackPower = 10; // Default attack power for hostile entities

    /**
     * Attacks a target
     * @param target the target to attack
     */
    void attack(Entity target);

    /**
     * Checks if the entity is currently aggressive
     * @return true if aggressive, false otherwise
     */
    boolean isAggressive();

    /**
     * Prints a war cry to intimidate opponents
     */
    void warCry();

    /**
     * Chooses an entity from nearby entities to attack.
     * Default to random nearby PlayableCharacter
     *
     * @param nearby the entities
     * @return the entity
     */
    default List<Entity> prioritizeAttack(List<Entity> nearby, Amount count) {
        ArrayList<PlayableCharacter> nearbyCharacters = new ArrayList<>();
        for (Entity e : nearby){
            if (e instanceof PlayableCharacter pc){
                nearbyCharacters.add(pc);
            }
        }
        if (nearbyCharacters.isEmpty()) return null;

        Entity pc;
        List<Entity> attackTargets=new ArrayList<>();
        for (int i = 0; i<count.getCount()&&i<nearbyCharacters.size(); i++){
            do{
                pc = nearbyCharacters.get(random.nextInt(0, nearbyCharacters.size()));
            } while (attackTargets.contains(pc));
            attackTargets.add(pc);
            nearbyCharacters.remove(pc);
        }
        return attackTargets;
    }

    Amount getSimultaneousTargetCount();
}
