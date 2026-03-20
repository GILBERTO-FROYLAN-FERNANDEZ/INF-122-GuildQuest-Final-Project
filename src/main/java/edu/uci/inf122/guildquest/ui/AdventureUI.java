package edu.uci.inf122.guildquest.ui;

import edu.uci.inf122.guildquest.api.win_conditions.GetToTargetXYWithPrincessCondition;
import edu.uci.inf122.guildquest.content.User;
import edu.uci.inf122.guildquest.content.UserFactory;
import edu.uci.inf122.guildquest.content.RealmFactory;
import edu.uci.inf122.guildquest.content.Realm;
import edu.uci.inf122.guildquest.content.TimeRule;

import java.util.ArrayList;
import java.util.List;

import edu.uci.inf122.guildquest.adventures.EscortAdventure;
import edu.uci.inf122.guildquest.api.win_conditions.RaidClearCondition;
import edu.uci.inf122.guildquest.api.win_conditions.TimeLimitCondition;
import edu.uci.inf122.guildquest.api.win_conditions.WinCondition;
import edu.uci.inf122.guildquest.adventures.RaidGridState;
import edu.uci.inf122.guildquest.adventures.TimedRaidAdventure;
import edu.uci.inf122.guildquest.content.items.Item;
import edu.uci.inf122.guildquest.content.items.ItemFactory;
import edu.uci.inf122.guildquest.engine.MiniAdventure;
import edu.uci.inf122.guildquest.entities.Entity;
import edu.uci.inf122.guildquest.entities.domain_primitives.*;
import edu.uci.inf122.guildquest.entities.nonlivings.Chest;
import edu.uci.inf122.guildquest.entities.npcs.Ferryman;
import edu.uci.inf122.guildquest.entities.npcs.Goblin;
import edu.uci.inf122.guildquest.entities.npcs.Princess;
import edu.uci.inf122.guildquest.entities.playablecharacters.*;

import edu.uci.inf122.guildquest.ui.Page;

public class AdventureUI {
    private MiniAdventure adventure;

    public AdventureUI(MiniAdventure adventure) {
        this.adventure = adventure;
    }

    public void start() {
        adventure.play();
    }

    public static void displayOptions(User user) {
        Page page = Page.getPage();
        page.nextScreen();
        String prompt = """
                What adventure would you like to play?

                1 --- Play Escort Adventure
                2 --- Play Timed Raid Adventure

                0 --- exit
                """;

        int i = -1;
        while (i != 0) {
            i = page.acceptIntUntil(prompt, 2);
            switch (i) {
                case 0 -> System.out.print("");
                case 1 -> playEscortAdventure(user);
                case 2 -> playTimedRaid(user);
            }
        }
    }

    private static void playEscortAdventure(User user) {
        Page page = Page.getPage();
        page.nextScreen();
        String player2Name = page.acceptStr("Enter Player 2 username: ");
        User player2 = UserFactory.createUser(player2Name);
        List<User> players = List.of(user, player2);

        List<WinCondition> winConditions = new ArrayList<>();

//        winConditions.add(new GetToTargetXYWithPrincessCondition(4,4));
//        winConditions.add(new TimeLimitCondition(10));

        Realm realm = RealmFactory.createRealm(
                "Escort Realm", "A dangerous escort mission",
                new TimeRule(0, 1.0), 6, 6);

        EscortAdventure adventure = new EscortAdventure(
                List.of(realm),
                EscortAdventure.defaultEntities(),
                new GetToTargetXYWithPrincessCondition(4,4),
                players);

        page.nextScreen();
        adventure.play();
    }



    private static void playTimedRaid(User user) {
        Page page = Page.getPage();
        page.nextScreen();
        int playerCount = page.acceptIntUntil(
                "How many players? (1-3)\n", 3);
        if (playerCount == 0) return;

        List<User> raidPlayers = new ArrayList<>();
        raidPlayers.add(user);
        for (int p = 2; p <= playerCount; p++) {
            String name = page.acceptStr("Enter Player " + p + " username: ");
            raidPlayers.add(UserFactory.createUser(name));
        }

        List<Boolean> classTaken = new ArrayList<>(List.of(false, false, false));
        List<PlayableCharacter> raidCharacters = new ArrayList<>();
        for (int p = 0; p < playerCount; p++) {
            String classPrompt = raidPlayers.get(p).getUsername()
                    + ", choose your class:\n";
            if (!classTaken.get(0)) classPrompt += "1 --- Knight\n";
            if (!classTaken.get(1)) classPrompt += "2 --- Assassin\n";
            if (!classTaken.get(2)) classPrompt += "3 --- Cleric\n";

            int classChoice = 0;
            boolean valid = false;
            while (!valid) {
                classChoice = page.acceptIntUntil(classPrompt, 3);
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
            String pcName = raidPlayers.get(p).getUsername();
            switch (classChoice) {
                case 1 -> raidCharacters.add(Knight.getInstance(new Name(pcName)));
                case 2 -> raidCharacters.add(Assassin.getInstance(new Name(pcName)));
                case 3 -> raidCharacters.add(Cleric.getInstance(new Name(pcName)));
            }
        }

        Goblin g1 = new Goblin(new Name("Grak"), new Health(30, 30), new Level(1));
        Goblin g2 = new Goblin(new Name("Snik"), new Health(20, 20), new Level(1));
        Goblin g3 = new Goblin(new Name("Brug"), new Health(40, 40), new Level(2));
        List<Entity> raidEntities = new ArrayList<>(List.of(g1, g2, g3));

        RaidGridState raidGrid = new RaidGridState(10, 10);
        raidGrid.setCell(3, 4, g1);
        raidGrid.setCell(5, 7, g2);
        raidGrid.setCell(7, 2, g3);

        Realm raidRealm = RealmFactory.createRealm(
                "Raid Dungeon", "A dark dungeon",
                new TimeRule(0, 1.0), 10, 10);

        List<WinCondition> raidWinConditions = new ArrayList<>();
        raidWinConditions.add(new RaidClearCondition(raidEntities));

        TimedRaidAdventure raid = new TimedRaidAdventure(
                List.of(raidRealm), raidEntities, raidWinConditions,
                raidPlayers, raidGrid, 60, raidCharacters);

        page.nextScreen();
        raid.play();
    }
}