package edu.uci.inf122.guildquest.engine;

import edu.uci.inf122.guildquest.api.AdventureSnapshot;
import edu.uci.inf122.guildquest.api.Status;
import edu.uci.inf122.guildquest.api.state.State;
import edu.uci.inf122.guildquest.api.win_conditions.WinCondition;
import edu.uci.inf122.guildquest.content.Realm;
import edu.uci.inf122.guildquest.content.User;
import edu.uci.inf122.guildquest.entities.Entity;
import edu.uci.inf122.guildquest.entities.domain_primitives.Name;
import edu.uci.inf122.guildquest.entities.playablecharacters.Assassin;
import edu.uci.inf122.guildquest.entities.playablecharacters.Cleric;
import edu.uci.inf122.guildquest.entities.playablecharacters.Knight;
import edu.uci.inf122.guildquest.entities.playablecharacters.PlayableCharacter;
import edu.uci.inf122.guildquest.ui.Page;

import java.util.ArrayList;
import java.util.List;

public abstract class MiniAdventure {
    private List<Realm> realms;
    protected List<Entity> entities; // placeholder for now - will be replaced with actual Entity objects
    private List<WinCondition> winCondition;
    private List<User> users;
    private State state;
    private AdventureSnapshot previousSave = null;

    protected List<User> getUsers() {
        return users;
    }

    public abstract void play();

    public abstract Status acceptInput();

    public abstract Status advanceCycle();

    public abstract AdventureSnapshot saveSnapshot();

    public MiniAdventure(List<Realm> realms, List<Entity> entities, List<WinCondition> winCondition,
            List<User> users) {
        this.realms = realms;
        this.entities = entities;
        this.winCondition = winCondition;
        this.users = users;

    }

    public User getPlayer(int index) {
        return users.get(index);
    }

    public List<WinCondition> getWinConditions() {
        return winCondition;
    }

    public static List<PlayableCharacter> queryForClasses(List<User> users){
        List<Boolean> classTaken = new ArrayList<>(List.of(false, false, false));
        List<PlayableCharacter> raidCharacters = new ArrayList<>();
        for (int p = 0; p < users.size(); p++) {
            String classPrompt = users.get(p).getUsername()
                    + ", choose your class:\n";
            if (!classTaken.get(0)) classPrompt += "1 --- Knight\n";
            if (!classTaken.get(1)) classPrompt += "2 --- Assassin\n";
            if (!classTaken.get(2)) classPrompt += "3 --- Cleric\n";

            int classChoice = 0;
            boolean valid = false;
            while (!valid) {
                classChoice = Page.getPage().acceptIntUntil(classPrompt, 3);
                if (classChoice >= 1 && classChoice <= 3
                        && !classTaken.get(classChoice - 1)) {
                    valid = true;
                } else if (classChoice >= 1 && classChoice <= 3) {
                    System.out.println("That class is already taken.");
                } else {
                    System.out.println("Invalid choice. Please select an available class.");
                }
            }
            classTaken.set(classChoice - 1, true);
            String pcName = users.get(p).getUsername();
            switch (classChoice) {
                case 1 -> raidCharacters.add(Knight.getInstance(new Name(pcName)));
                case 2 -> raidCharacters.add(Assassin.getInstance(new Name(pcName)));
                case 3 -> raidCharacters.add(Cleric.getInstance(new Name(pcName)));
            }
        }
        return raidCharacters;
    }
}
