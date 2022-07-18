package me.bonjur.teams;

import me.bonjur.CastleWars;
import me.bonjur.Main;
import me.bonjur.config.GameConfig;
import me.bonjur.managers.PlayerManager;
import org.bukkit.*;
import org.bukkit.entity.Player;


public class TeamManager {

    CastleWars castleWars = Main.castleWars;

    private int kills;
    private int points;

    private Player thief;

    public void increasePoints() {
        this.points++;
    }
    public void decreaseKills() {
        this.kills--;
    }

    public void setThief(Player thief) {
        this.thief = thief;
    }

    public Player getThief() {
        return this.thief;
    }
    public boolean isThief(Player player){
        return this.thief == player;
    }
    public int getPoints() {
        return this.points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getKills() {
        return this.kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public TeamManager(TeamColors colorTeam) {
        this.kills = GameConfig.IMP.POINTS.KILLS;
    }


    public boolean isOneTeam(Player playerOne, Player playerTwo) {
        return TeamColors.getColor(playerOne) == TeamColors.getColor(playerTwo);
    }
    public int countBlue() {
        int count = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (TeamColors.getColor(player) == TeamColors.BLUE) {
                count++;
            }
        }
        return count;
    }

    public int countRed() {
        int count = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (TeamColors.getColor(player) == TeamColors.RED) {
                count++;
            }
        }
        return count;
    }

    private void teleportToBase(Player player) {
        if (TeamColors.getColor(player) == TeamColors.RED) {
            player.teleport(GameConfig.IMP.RESPAWN.RED);
        } else if (TeamColors.getColor(player) == TeamColors.BLUE) {
            player.teleport(GameConfig.IMP.RESPAWN.BLUE);
        }
    }

    public void choiceTeam(Player player) {
        PlayerManager playerManager = new PlayerManager();

        player.setGameMode(GameMode.SURVIVAL);
        playerManager.clearInventory(player);
        if (this.countBlue() > this.countRed()) {
            player.sendMessage("Вы играете за §c§lКрасную команду");
            player.setPlayerListName("§c§l" + player.getName());
        } else {
            player.sendMessage("Вы играете за §9§lСинию команду");
            player.setPlayerListName("§9§l" + player.getName());
        }
        teleportToBase(player);
        playerManager.getKit(player);
    }


}
