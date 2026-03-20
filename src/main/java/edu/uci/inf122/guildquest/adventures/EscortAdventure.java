package edu.uci.inf122.guildquest.adventures;

import edu.uci.inf122.guildquest.api.AdventureSnapshot;
import edu.uci.inf122.guildquest.api.Status;
import edu.uci.inf122.guildquest.api.state.GridCell;
import edu.uci.inf122.guildquest.api.win_conditions.*;
import edu.uci.inf122.guildquest.content.*;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
// import edu.uci.inf122.guildquest.ui;
import edu.uci.inf122.guildquest.content.items.Item;
import edu.uci.inf122.guildquest.content.items.ItemFactory;
import edu.uci.inf122.guildquest.content.items.SelfApplicable;
import edu.uci.inf122.guildquest.engine.MiniAdventure;
import edu.uci.inf122.guildquest.entities.Entity;
import edu.uci.inf122.guildquest.entities.domain_primitives.*;
import edu.uci.inf122.guildquest.entities.interfaces.CanHealOther;
import edu.uci.inf122.guildquest.entities.interfaces.CanHealSelf;
import edu.uci.inf122.guildquest.entities.npcs.*;
import edu.uci.inf122.guildquest.entities.playablecharacters.Assassin;
import edu.uci.inf122.guildquest.entities.playablecharacters.Cleric;
import edu.uci.inf122.guildquest.entities.playablecharacters.Move;
import edu.uci.inf122.guildquest.entities.playablecharacters.PlayableCharacter;
import edu.uci.inf122.guildquest.entities.nonlivings.Chest;
import edu.uci.inf122.guildquest.ui.Page;
import edu.uci.inf122.guildquest.ui.TerminalGrid;
import edu.uci.inf122.guildquest.ui.playablecharacteruis.PlayableCharacterUI;
import edu.uci.inf122.guildquest.ui.playablecharacteruis.PartyStatsUI;

// The rule: 
// starting point: (0, 0) with an NPC that needs to be escorted to the destination that the NPC knows. (The player does not know the destination)
// The player can ask the NPC for hints about the destination, but the NPC will give clear hints only if the destination is close (EX: steps). 
// => Which is the actual Grid for the destination 
// NPC still give hints even if the destination is far, but the hints are more vague (EX: direction).
// the player can move in four directions (up, down, left, right) and can move 1-3 spaces at a time 
//
// EX) player1 can move up 2 spaces, then right 1 space. (2, 1)
// EX) player2 can move down 3 spaces. (-3, 0)
// EX) player1 can move left 2 spaces. (0, -2) 
//
// The initial information the players have: 
// - current grid state for players 
//
// more information for later: 
// - if the player succeed in attacking an enemy, they will know the enemy's location. 
// - if the player with no NPC reaches the destination first, they will know the location of the destination. 
// - if the player with the NPC is within 2 spaces of the destination, they will get clear hints about the destination (ex: steps).
//
//
// The maximum number of moves for each turn will be 3. (1, 2) or (2, 1) or (-2, 1), etc. 
//
// Enemies are able to attack the player if they are adjacent to the player with the NPC (2 space away in any direction). (NO MOVE)
// If the player who is with the NPC is at the same grid cell as an enemy, the player will lose and the adventure will end.
// 
// if the player wants to attack, they can only attack enemies (No ohter npcs) (2 space away in any direction).]
//
// Chests and Keys will be placed on the grid and can be picked up by the player if they are adjacent to them. (1 space away in any direction)
// Chests can be opened by any keys. (3 keys and 3 chests on the grid)
// 
// OTHER-NPCs on Grid: 
// there are other NPCs on the grid that can give the player some items or hints. 
// the player enters the grid, then they interact with the NPCs.
//
// Win Condition: The player with the NPC reaches the destination. 
// Lose Condition: The player with the NPC is on the same grid cell as an enemy.
// 
// Starting point should be the same for both players (maybe) 
// The distance between Starting point and Destination must be at least 5 spaces away. 
// 
// Nice to have: 
// - If the player with no NPC reaches the destination first, they will know the location of the destination.

/**
 * The type Escort adventure.
 */
public class EscortAdventure extends MiniAdventure { // extends MiniAdventure {
    // private GridUI gridUI;
    // save/serialize
    private final TerminalGrid gridState;
    private final GetToTargetXYWithPrincessCondition condition;
    private List<Entity> enemies;
    private List<Entity> chests;
    private List<Entity> items;
    private List<Entity> npcs;
    private final static Page page = Page.getPage();
    private PlayableCharacter currentPlayer;
    private List<PlayableCharacter> playableCharacters;

    public static List<Entity> defaultEntities() {
        Item item1 = ItemFactory.createHealingPotion("hp potion", 1, "a blue sword", null, 10);
        Item item2 = ItemFactory.createWeapon("red stick", 1, "a flimsy red stick", 1);
        Item item3 = ItemFactory.createTool("green pickaxe", 1, "a green pickaxe");
        Princess princess = new Princess(new Name("princess"), new Health(10), new Amount(2));

        return List.of(
                princess,
                new Goblin(new Name("Goblin 1"), new Health(10), new Level(1)),
                new Goblin(new Name("Goblin 2"), new Health(12), new Level(1)),
                new Goblin(new Name("Goblin 3"), new Health(15), new Level(2)),
                item1, item2, item3,
                new Chest(new Name("chest1"), item1, new Text("a chest with a blue sword")),
                new Chest(new Name("chest2"), item2, new Text("a chest with a red stick")),
                new Chest(new Name("chest3"), item3, new Text("a chest with a green pickaxe")),
                new Ferryman(new Name("John Ferryman"), new XYPlace(new Name("Destination"),4,4), new Amount(10)),
                new Ferryman(new Name("Expensive Ferryman"), new XYPlace(new Name("Destination"),4,4), new Amount(100))
        );
    }
    // nice to have: difficulty levels that change the # of enemies and the size of
    // the grid, etc.

    /**
     * Instantiates a new Escort adventure.
     *
     * @param realms    the realms
     * @param entities  the entities
     * @param condition the condition
     * @param players   the players
     */
    public EscortAdventure(List<Realm> realms,
                           List<Entity> entities,
                           GetToTargetXYWithPrincessCondition condition,
                           List<User> players,
                           List<PlayableCharacter> playerCharacters)
    {
        super(  realms,
                entities,
                List.of(condition),
                players  );
        this.condition=condition;
        this.playableCharacters=playerCharacters;
        gridState = new TerminalGrid(6, 6);
    }

    public void play() {
        // game logic to run the adventure
        // initialize the grid, place entities (randomly), and start the game loop
        // pick a character and tools for the player, initialize the grid with entities,
        // etc.

        initializeUser(); // set up who's with the NPC, etc. if we want to ask
        initializeGrid();
        gridState.render();
        while (true) {

            Status res = advanceCycle();
            page.print(res.getMsg());
            if (res.isDone()){
                break;
            }
        }
    }

    @Override
    public Status advanceCycle() {
        playerTurns(); // will render
        allEnemyTurns();
        return checkRunningCondition();
    }

    /**
     * Accepts input and acts for all players
     * Can perform actions specific to each player's class.
     */
    public void playerTurns() {
        PartyStatsUI partyStats = new PartyStatsUI(playableCharacters);

        boolean done = false;
        int i = 0;
        currentPlayer = playableCharacters.get(i);
        while (!done) {
            Status temp = acceptInput();
            String input = temp.getMsg();

            Status status = inputToAction(input);

            page.print(status.getMsg() + '\n');
            if (status.isFail()){
                continue;
            }

            partyStats.display();

            gridState.render();
            if (i==playableCharacters.size()-1){
                done=true;
            }
            else
                currentPlayer=playableCharacters.get(++i);
        }
        // if (player location is within 2 spaces of the destination) {
        // // give clear hints about the destination (ex: steps)
        // }
        // if (player location is closer to enemy) {
        // // let user know that they are close to an enemy.
        // }
        // // if the enemy attacks the player, the player will know the enemy's
        // location.

        // if player with no NPC is on the same grid cell as the destination, then the
        // destination location is known.
        // if the player with the NPC is on the same grid cell as the destination, then
        // end the game with a win.
    }

    private Status inputToAction(String input) {
        if (input.contains("move")) {
            return attemptMovePlayer(currentPlayer, input.charAt(input.length() - 1));
        }
        else if (input.contains("use item")){
            return attemptUseItem(currentPlayer);
        }
        else if (input.contains("attack")) {
            return attemptPlayerAttack(currentPlayer, input.charAt(input.length() - 1));
        }
        else if (input.contains("heal self")) {
            return attemptPlayerHealSelf(currentPlayer);
        }
        else if (input.contains("heal")) {
            return attemptPlayerHealOther(currentPlayer, input.charAt(input.length() - 1));
        }
        else if (input.equals("r")) {
            return makeHintRequest(); // check to see if npc is nearby
        }
        else if (input.equals("0")) {
            return new Status(Status.Option.SUCCESS, "skipped turn"); // check to see if npc is nearby
        }
        else
            throw new IllegalStateException("Something went wrong with input, cannot parse");
    }

    private Status attemptUseItem(PlayableCharacter currentPlayer) {
        if (currentPlayer.getInventory().isEmpty()){
            return new Status(Status.Option.FAIL, "Inventory is empty, do something else.");
        }
        Item item = currentPlayer.getUI().promptInventory();
        if (item==null)
            return new Status(Status.Option.FAIL, "We could not get this item.");
        if (item instanceof SelfApplicable i){
            int prev = currentPlayer.getHealth().getHealth();
            boolean success = i.use(currentPlayer);
            if (!success){
                return new Status(Status.Option.FAIL, "something went wrong, not healed.");
            }
            int cur = currentPlayer.getHealth().getHealth();
            return new Status(Status.Option.SUCCESS, "heal increased from "+prev+" to "+cur);
        } else{
            return new Status(Status.Option.FAIL, "have not implemented items that can be applied to others.");
        }
    }

    private Status attemptPlayerHealSelf(PlayableCharacter p) {
        if (p instanceof CanHealSelf healer) {
            if (p.isDead()) {
                return new Status(Status.Option.FAIL, "You cannot heal yourself. You're dead");
            }
            if (p.getHealth().isFull()) {
                return new Status(Status.Option.FAIL, "You're already at full health! Nothing to heal :)");
            }
            if (p instanceof Cleric c) {
                c.heal(c.getHealingPower());
                return new Status(Status.Option.SUCCESS, c.getName() + " healed to " + c.getHealth());
            }
            throw new IllegalStateException("Error in PlayerHeal, something went wrong");
        } else {
            page.print("This character cannot heal\n");
            return new Status(Status.Option.FAIL, "This character cannot heal");
        }
    }

    private Status attemptPlayerHealOther(PlayableCharacter p, char direction) {
        if (!(p instanceof CanHealOther healer)) {
            return new Status(Status.Option.FAIL, "This character cannot others!");
        }
        else if (p.isDead()) {
            return new Status(Status.Option.FAIL, "You cannot heal yourself. You're dead");
        }
        else if (!gridState.isValidAdjacent(p, direction)) {
            return new Status(Status.Option.FAIL, "You cannot heal in that direction");
        }

        GridCell targetCell = gridState.getCellAdjacent(p, direction);

        if (targetCell.isEmpty()) {
            return new Status(Status.Option.FAIL, "Target cell is empty, cannot heal here");
        }

        Entity target = targetCell.getContent().get(0);

        if (!(target instanceof PlayableCharacter otherPlayer)) {
            return new Status(Status.Option.FAIL, "can only heal other players for now.");
        }
        else if (otherPlayer.getHealth().isFull()) {
            return new Status(Status.Option.FAIL, "Already at full health. Do something else");
        }

        healer.heal(healer.getHealAmount(), otherPlayer);
        return new Status(Status.Option.SUCCESS);
    }

    @Override
    public Status acceptInput() {
        return TerminalGrid.acceptPlayerInput(currentPlayer);
    }

    private Status attemptPlayerAttack(PlayableCharacter p, char direction) {
        GridCell attackTarget = gridState.getCellAdjacent(currentPlayer, direction);
        if (attackTarget.isEmpty() ||
                !(attackTarget.getContent().get(0) instanceof Hostile)) {
            return new Status(Status.Option.FAIL, "Cannot attack that cell");
        }
        GridCell targetCell = gridState.cellAdjacent(p, direction);

        p.attack(targetCell.getContent().get(0)); // assume only on 1st layer
        if (targetCell.getContent().get(0) instanceof Goblin g) {
            if (g.isDead()) {
                gridState.removeEntity(g);
                enemies.remove(g);
                gridState.render();
            }
        }
        return new Status(Status.Option.SUCCESS);
    }

    /**
     * All enemies act here
     */
    public void allEnemyTurns() {
        for (Entity h : enemies) {
            enemyTurn(h);
        }
    }

    /**
     * One enemy turn;
     *
     * @param enemy the enemy
     */
    public void enemyTurn(Entity enemy) {
        if (enemy instanceof Hostile h) {
            List<Entity> nearbyEntities = gridState.nearbyEntities(gridState.getLocation(enemy), 2);
            Amount c = h.getSimultaneousTargetCount();
            List<Entity> targets = h.prioritizeAttack(nearbyEntities, c);

            if (targets == null || targets.isEmpty())
                return;
            for (Entity target : targets){
                h.attack(target);
            }
        }

    }


    public AdventureSnapshot saveSnapshot() {
        // logic to save the current state of the adventure
        return null; // placeholder
    }

    /**
     * Initialize grid with the information given
     */
    public void initializeGrid() {
        // logic to initialize the grid with entities, place the NPC to be escorted,
        // items, tresures, and enemies
        // Starting point: (0, 0) with an NPC that needs to be escorted to the

        // they are all placed randomly, but the distance between the starting point and
        // enemies should be at least 6 spaces away

        int i = 0;
        enemies = new ArrayList<>();
        items = new ArrayList<>();
        npcs = new ArrayList<>();
        chests = new ArrayList<>();
        for (Entity e : entities){
            if (e instanceof Princess){
                gridState.setCell(0, i++, e);
            }
            else if (e instanceof PlayableCharacter){
                gridState.setCell(0, i++, e);
            }
            else if (e instanceof Goblin){
                enemies.add(e);
            }
            else if (e instanceof Item) {
                items.add(e);
            }
            else if (e instanceof Chest) {
                chests.add(e);
            }
            else if (e instanceof NPC){
                npcs.add(e);
            }
        }

//        gridState.setCell(0, 0, princess_npc);
        for (int j = 1; j < playableCharacters.size()+1; ++j){
            gridState.setCell(0, j, playableCharacters.get(j-1));
        }

        gridState.initializeGrid(enemies);
//        gridState.initializeGrid(items);
        gridState.initializeGrid(chests);
        gridState.initializeGrid(npcs);
    }

    /**
     * Initialize player character.
     */
    void initializePlayerCharacter() {
        // logic to let the player choose their character and tools, etc.
    }

    /**
     * Initialize user to be either Assassin or Cleric
     */
    public void initializeUser() {

        PartyStatsUI partyStats = new PartyStatsUI(playableCharacters);
        partyStats.display();

        currentPlayer = playableCharacters.get(0);

    }

    /**
     * Moves the player in the direction desired.
     *
     * @param p         the p
     * @param direction the direction
     * @return the status
     */
    public Status attemptMovePlayer(PlayableCharacter p, char direction) {
        if (!gridState.canMove(currentPlayer, direction)) {
            return new Status(Status.Option.FAIL, "Cannot move in that direction");
        }

        GridCell targetCell = gridState.cellAdjacent(p, direction);

        gridState.removeEntity(p);
        if (targetCell.hasContent()) {
            if (targetCell.getTop() instanceof Chest chest){
                Item item = chest.take();
                if (item != null) {
                    p.addToInventory(item);
                }
                gridState.removeEntity(chest);
            }
            else if (targetCell.getTop() instanceof Item item) {
                currentPlayer.addToInventory(item);
                page.print("added "+item.getName()+" to inventory\n");
                gridState.removeEntity(item);
            }
            else if (targetCell.getTop() instanceof Princess) {
                currentPlayer.pickUpPrincess();
                page.print("You are now escorting the princess\n");
            }
        }
        Status status =  gridState.setCellWithChecking(targetCell, p);
        if (!status.isFail()){
            int[] pos = gridState.getLocationCords(targetCell);
            condition.updateCondition(pos[0], pos[1], currentPlayer);
        }
        return status;
    }

    /**
     * Character requests a hint.
     *
     * @return the status. Always true currently.
     */
    public Status makeHintRequest() {
        // logic to let the player ask for hints

        // check the distance between the player with the NPC and the destination
        int[] playerPosition = gridState.getLocationCords(currentPlayer);
        if (gridState.checkDistance(playerPosition[0], playerPosition[1], 2)) {
            // give clear hints about the destination (ex: steps)
            // tell drirection
        } else {
            // give vague hints about the destination (ex: direction)
            // tell "Far....."
//            page.print("destination is far...\n");
        }
        return new Status(Status.Option.FAIL, "Unimplemented as of yet.");
    }

    /**
     * Checks the win conditions to see if the game should end
     *
     * @return return status. Either done or continue
     */
    public Status checkRunningCondition() {
        // learn to use actual WinCondition object here.
        for (WinCondition wc : getWinConditions()){
            if (wc.isLost()){
                return new Status(Status.Option.DONE, wc.loseMessage().toString());
            }
            if (wc.isWon()){
                return new Status(Status.Option.DONE, wc.winMessage().toString());
            }
        }
        if (playableCharacters.stream().anyMatch(PlayableCharacter::isDead)) {
            return new Status(Status.Option.DONE, "You died! You lost.");
        } else
            return new Status(Status.Option.CONTINUE);
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {

        List<User> users= List.of(
                User.getNewRandomUser(),
                User.getNewRandomUser(),
                User.getNewRandomUser()
        );
        EscortAdventure adventure = new EscortAdventure(
                new ArrayList<>(),
                defaultEntities(),
                new GetToTargetXYWithPrincessCondition(4, 4),
                users,
                queryForClasses(users)
                );

        adventure.play();
    }

}
