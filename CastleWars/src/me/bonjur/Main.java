package me.bonjur;

import me.bonjur.config.GameConfig;
import me.bonjur.listeners.ChatListener;
import me.bonjur.listeners.PlayerListener;
import me.bonjur.managers.ScoreboardManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


public class Main extends JavaPlugin {

    public static CastleWars castleWars;

    @Override
    public void onEnable() {
        ScoreboardManager scoreboard = new ScoreboardManager();
        castleWars = new CastleWars();

        GameConfig.IMP.reload(new File(this.getDataFolder(), "config.yml"));
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        castleWars.redTeam.setKills(GameConfig.IMP.POINTS.KILLS);
        castleWars.blueTeam.setKills(GameConfig.IMP.POINTS.KILLS);
        castleWars.redTeam.setPoints(0);
        castleWars.blueTeam.setPoints(0);

        scoreboard.update();
    }
}
