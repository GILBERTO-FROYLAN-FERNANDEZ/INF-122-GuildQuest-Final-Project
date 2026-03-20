package edu.uci.inf122.guildquest.content;

import java.io.SequenceInputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class User extends GameContent {
    private UUID userID;
    private String userName;
    private UUID currentRealmID;
    // private Settings userSetting;
    private List<GameCharacter> characters;
    private List<Campaign> campaigns;
    private static final AtomicInteger seq = new AtomicInteger();
    // private Time userTime;

    // constructor:
    public User(UUID id, String name, UUID realmID) {
        userID = id;
        userName = name;
        currentRealmID = realmID;
        characters = new ArrayList<>();
        campaigns = new ArrayList<>();
    }

    public static User getNewRandomUser(){
        return new User(UUID.randomUUID(), "random "+seq.incrementAndGet(), UUID.randomUUID());
    }

    // check name
    public boolean checkName(String name) {
        return name.equals(userName);
    }

    public void setName(String name) {
        userName = name;
    }

    public String getName() {
        return userName;
    }

    public void setRealm(UUID id) {
        currentRealmID = id;
    }

    public String getUsername() {
        return userName;
    }

    public UUID getUserID() {
        return userID;
    }

    public List<GameCharacter> getCharacters() {
        return characters;
    }

    public List<Campaign> getCampaigns() {
        return campaigns;
    }

    public void addCampaign(Campaign campaign) {
        campaigns.add(campaign);
    }

    public void removeCampaign(Campaign campaign) {
        campaigns.remove(campaign);
    }

    public void addCharacter(GameCharacter character) {
        characters.add(character);
    }

    public void removeCharacter(GameCharacter character) {
        characters.remove(character);
    }

    public static User createUser(String username) {
        return UserFactory.createUser(username);
    }
}
