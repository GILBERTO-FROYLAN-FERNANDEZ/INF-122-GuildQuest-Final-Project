/*
Which objects should be immutable?
Mutable Objects:
*/
public interface Snapshot<T>{
    public T reCreate();
}

public class CharacterSnapshot : Snapshot<Character>{
    public String name;
    public String characterClass;
    public int characterLevel;
    public UUID userId;
    public InventorySnapshot inventorySnapshot;

    public Character reCreate(){
        return new Character(name, characterClass, characterLevel, userId, inventorySnapshot.reCreate);
    }
}
public interface InventorySnapshot : Snapshot<Inventory>{
    public Item reCreate();
}
public interface ItemSnapshot : Snapshot<Item>{
    public Item reCreate();
}

public class WeaponSnapshot : ItemSnapshot{
    public Weapon reCreate();
}
public class ToolSnapshot : ItemSnapshot{
    public Tool reCreate();
}
public class ConsumableSnapshot : ItemSnapshot{
    public Consumable reCreate();
}

// command stuff
public interface Command{
    public void execute();
}
public interface InputCommand : Command{
    public void execute()
}

public abstract class MoveCommand{
    private Entity entity;
    private State state;
    public MoveCommand(Entity entity, State state){
        this.entity=entity;
        this.state=state;
    }
    public void execute();
}
public class MoveUpCommand extends MoveCommand{
    public MoveUpCommand(Entity entity, Grid grid){
        super(entity, grid);
    }
    public void execute(){
        Position pos = grid.getPosition(entity);
        Position newPos = pos.getY() + 1;
        if (grid.isValidPosition(newPos, entity)) {
            grid.removeAt(pos);
            grid.add(entity, newPos);
        } else{
            throw new InvalidMoveError("Cannot move up from current position");
        }
    }
}
public class MoveDownCommand extends MoveCommand{
    public MoveUpCommand(Entity entity, Grid grid){
        super(entity, grid);
    }
    public void execute(){
        Position pos = grid.getPosition(entity);
        Position newPos = pos.getY() - 1;
        if (grid.isValidPosition(newPos, entity)) {
            grid.removeAt(pos);
            grid.add(entity, newPos);
        } else{
            throw new InvalidMoveError("Cannot move up from current position");
        }
    }
}
public class MoveRightCommand extends MoveCommand{
    public MoveRightCommand(Entity entity, Grid grid){
        super(entity, grid);
    }
    public void execute(){
        Position pos = grid.getPosition(entity);
        Position newPos = pos.getX() + 1;
        if (grid.isValidPosition(newPos, entity)) {
            grid.removeAt(pos);
            grid.add(entity, newPos);
        } else{
            throw new InvalidMoveError("Cannot move up from current position");
        }
    }
}
public class MoveLeftCommand extends MoveCommand{
    public MoveLeftCommand(Entity entity, Grid grid){
        super(entity, grid);
    }
    public void execute(){
        Position pos = grid.getPosition(entity);
        Position newPos = pos.getX() - 1;
        if (grid.isValidPosition(newPos, entity)) {
            grid.removeAt(pos);
            grid.add(entity, newPos);
        } else{
            throw new InvalidMoveError("Cannot move up from current position");
        }
    }
}

import Entity
import Weapon
public class AttackCommand : InputCommand {
    private Entity attacker;
    private Weapon weapon;
    private Entity attacked;
    public void execute(){
        // check if hit lands
        if (attacked.avoid(weapon)) return;
        attacked.reduceHealth(Weapon.getDamage(Entity));
    }
}