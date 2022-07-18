package me.bonjur.config;

import me.bonjur.utils.AbstractConfig;
import org.bukkit.Location;

public class GameConfig extends AbstractConfig {
    @Ignore
    public static final GameConfig IMP = new GameConfig();

    @Create
    public POINTS POINTS;

    public Location MONUMENT = GameConfig.emptyLocation();

    @Create
    public RESPAWN RESPAWN;

    public Location LOBBY_LEAVE = GameConfig.emptyLocation();

    public Location PORTAL_START = GameConfig.emptyLocation();

    @Create
    public FLAGS FLAGS;

    @Comment("KILLS - это сколько киллов нужно сделать противополодной команде для победы, FLAGS - кол-во принесенных флагов для побед")
    public static class POINTS {
        public int KILLS = 5;
        public int FLAGS = 3;
    }

    @Comment("Точки возраждения синих и красных")
    public static class RESPAWN {
        public Location RED = GameConfig.emptyLocation();
        public Location BLUE = GameConfig.emptyLocation();
    }

    @Comment("Точки флагов")
    public static class FLAGS {
        public Location RED = GameConfig.emptyLocation();
        public Location BLUE = GameConfig.emptyLocation();
    }
}
