package me.bonjur.utils;

import org.bukkit.Location;

public class RectRegion {
    private int minX;

    private final int minY;

    private int minZ;

    private int maxX;

    private final int maxY;

    private int maxZ;

    public RectRegion(Location location, int size) {
        this.minX = location.getBlockX() - size;
        this.maxX = location.getBlockX() + size;

        this.minY = location.getBlockY() - size;
        this.maxY = location.getBlockY() + size;

        this.minZ = location.getBlockZ() - size;
        this.maxZ = location.getBlockZ() + size;

        int temp;

        if (minX > maxX) {
            temp = minX;
            this.minX = this.maxX;
            this.maxX = temp;
        }

        if (minZ > maxZ) {
            temp = minZ;
            this.minZ = this.maxZ;
            this.maxZ = temp;
        }
    }

    public boolean contains(int x, int y, int z) {
        if (x <= this.maxX && x >= this.minX) {
            if (y <= this.maxY && y >= this.minY) {
                return z <= this.maxZ && z >= this.minZ;
            }
        }
        return false;
    }

    public boolean contains(Location location) {
        return this.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
