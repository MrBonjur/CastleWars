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
        scoreboard.update();
    }
}
