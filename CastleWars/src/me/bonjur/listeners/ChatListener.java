package me.bonjur.listeners;

import me.bonjur.CastleWars;
import me.bonjur.Main;
import me.bonjur.config.GameConfig;
import me.bonjur.managers.PlayerManager;
import me.bonjur.managers.ScoreboardManager;
import me.bonjur.teams.TeamColors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatListener implements Listener {

    PlayerManager playerManager = new PlayerManager();
    ScoreboardManager scoreboard = new ScoreboardManager();
    CastleWars castleWars = Main.castleWars;



    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String nick = player.getName();
        String message = event.getMessage();

        TeamColors playerTeam = TeamColors.SPECTATOR;
        if (TeamColors.getColor(player) != TeamColors.SPECTATOR) {
            playerTeam = TeamColors.getColor(player);
        }

        if (TeamColors.getColor(player) == TeamColors.SPECTATOR) {
            Bukkit.broadcastMessage("§8[§dЗритель§8] §7" + nick + "§7: §f" + message);
            event.setCancelled(true);
            return;
        }

        if (message.startsWith("!")) {
            if (TeamColors.getColor(player) == TeamColors.RED) {
                Bukkit.broadcastMessage("§8[§d§lG§8] §c§l" + nick + "§7: §f" + message.substring(1));
            } else {
                Bukkit.broadcastMessage("§8[§d§lG§8] §9§l" + nick + "§7: §f" + message.substring(1));
            }
        } else {
            for (Player playerTeam1 : Bukkit.getOnlinePlayers()) {
                if (TeamColors.getColor(playerTeam1) == playerTeam) {
                    if (playerTeam == TeamColors.RED) {
                        playerTeam1.sendMessage("§8[§d§lL§8] §c§l" + nick + "§7: §f" + message);
                    } else {
                        playerTeam1.sendMessage("§8[§d§lL§8] §9§l" + nick + "§7: §f" + message);
                    }
                }
            }
        }

        event.setCancelled(true);
    }


    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/leave")) {

            Player player = event.getPlayer();
            event.setCancelled(true);
            castleWars.checkDropFlag(player);
            playerManager.resetPlayer(player);
            player.teleport(GameConfig.IMP.LOBBY_LEAVE);
            player.sendMessage("Вы покинули бой.");
            scoreboard.update();
        }
        if (event.getMessage().startsWith("/help")) {
            Player player = event.getPlayer();
            event.setCancelled(true);
            player.sendMessage("§c§lМеню помощи CastleWars:\n§f§l/leave - Покинуть бой.");
        }
    }


}
