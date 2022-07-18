package me.bonjur.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

public class AbstractConfig {

    private YamlConfiguration yml;

    public AbstractConfig() {
        save(new ArrayList<>(), getClass(), this, 0);
    }

    public void reload(File file) {
        load(file);
        save(file);
    }

    /**
     * Set the value of a specific node<br>
     * Probably throws some error if you supply non existing keys or invalid
     * values
     *
     * @param key   config node
     * @param value value
     */
    private void set(String key, Object value) {
        String[] split = key.split("\\.");
        Object instance = getInstance(split, this.getClass());
        if (instance != null) {
            Field field = getField(split, instance);
            if (field != null) {
                try {
                    if (field.getAnnotation(Final.class) != null) {
                        return;
                    }
                    if (field.getType() == String.class && !(value instanceof String)) {
                        value = value + "";
                    }
                    if (field.getType() == Location.class) {
                        value = parseLocation(value.toString());
                    }
                    field.set(instance, value);
                    return;
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    Bukkit.getLogger().log(Level.WARNING, "Error:", e);
                }
            }
        }
        Bukkit.getLogger().log(Level.WARNING,
                "[AbstractConfig] Failed to set config option: {0}: {1} | {2} ",
                new Object[]{key, value, instance});
    }

    public boolean load(File file) {
        if (!file.exists()) {
            return false;
        }
        this.yml = YamlConfiguration.loadConfiguration(file);

        set(this.yml, "");

        return true;
    }

    public void set(ConfigurationSection config, String oldPath) {
        for (String key : config.getKeys(false)) {
            Object value = config.get(key);
            String newPath = oldPath + (oldPath.isEmpty() ? "" : ".") + key;
            if (value instanceof MemorySection) {
                set((MemorySection) value, newPath);
                continue;
            }
            set(newPath, value);
        }
    }

    public Location parseLocation(String data) {
        String[] arr = data.split(";");

        String world = arr[0];

        double x = Double.parseDouble(arr[1]);
        double y = Double.parseDouble(arr[2]);
        double z = Double.parseDouble(arr[3]);

        return new Location(Bukkit.getWorld(world), x, y, z);
    }


    /**
     * Set all values in the file (load first to avoid overwriting)
     *
     * @param file file
     */
    public void save(File file) {
        try {
            File parent = file.getParentFile();
            if (parent != null) {
                file.getParentFile().mkdirs();
            }
            Path configFile = file.toPath();
            Path tempCfg = new File(file.getParentFile(), "__tmpcfg").toPath();
            List<String> lines = new ArrayList<>();
            save(lines, getClass(), this, 0);

            Files.write(tempCfg, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            try {
                Files.move(tempCfg, configFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tempCfg, configFile, StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "Error:", e);
        }
    }

    public String getString(String key) {
        return this.yml.getString(key);
    }

    private String toYamlString(Object value, String spacing) {
        if (value instanceof Location) {
            Location tLocation = (Location) value;
            value = tLocation.getWorld().getName() + ";" + tLocation.getBlockX() + ";" + tLocation.getBlockY() + ";" + tLocation.getBlockZ();
        }
        if (value instanceof List) {
            Collection<?> listValue = (Collection<?>) value;
            if (listValue.isEmpty()) {
                return "[]";
            }
            StringBuilder m = new StringBuilder();
            for (Object obj : listValue) {
                m.append(System.lineSeparator()).append(spacing).append("- ").append(toYamlString(obj, spacing));
            }
            return m.toString();
        }
        if (value instanceof String) {
            String stringValue = (String) value;
            if (stringValue.isEmpty()) {
                return "''";
            }
            return "\"" + stringValue + "\"";
        }
        return value != null ? value.toString() : "null";
    }

    private void save(List<String> lines, Class clazz, final Object instance, int indent) {
        try {
            String spacing = repeat(" ", indent);
            for (Field field : clazz.getFields()) {
                if (field.getAnnotation(Ignore.class) != null) {
                    continue;
                }
                Class<?> current = field.getType();
                if (field.getAnnotation(Ignore.class) != null) {
                    continue;
                }
                Comment comment = field.getAnnotation(Comment.class);
                if (comment != null) {
                    for (String commentLine : comment.value()) {
                        lines.add(spacing + "# " + commentLine);
                    }
                }
                Create create = field.getAnnotation(Create.class);
                if (create != null) {
                    Object value = field.get(instance);
                    setAccessible(field);
                    if (indent == 0) {
                        lines.add("");
                    }
                    comment = current.getAnnotation(Comment.class);
                    if (comment != null) {
                        for (String commentLine : comment.value()) {
                            lines.add(spacing + "# " + commentLine);
                        }
                    }
                    lines.add(spacing + toNodeName(current.getSimpleName()) + ":");
                    if (value == null) {
                        field.set(instance, value = current.newInstance());
                    }
                    save(lines, current, value, indent + 2);
                } else {
                    lines.add(spacing + toNodeName(field.getName() + ": ") + toYamlString(field.get(instance), spacing));
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Error:", e);
        }
    }

    /**
     * Get the field for a specific config node and instance<br>
     * Note: As expiry can have multiple blocks there will be multiple instances
     *
     * @param split    the node (split by period)
     * @param instance the instance
     * @return Field field
     */
    private Field getField(String[] split, Object instance) {
        try {
            Field field = instance.getClass().getField(toFieldName(split[split.length - 1]));
            setAccessible(field);
            return field;
        } catch (IllegalAccessException | NoSuchFieldException | SecurityException | NoSuchMethodException |
                 InvocationTargetException e) {
            Bukkit.getLogger().log(Level.WARNING, "[AbstractConfig] Invalid config field: {0} for {1}", new Object[]
                    {
                            String.join(".", split), toNodeName(instance.getClass().getSimpleName())
                    });
            return null;
        }
    }

    /**
     * Get the instance for a specific config node
     *
     * @param split the node (split by period)
     * @param root  the root class
     * @return The instance or null
     */
    private Object getInstance(String[] split, Class root) {
        try {
            Class<?> clazz = root == null ? MethodHandles.lookup().lookupClass() : root;
            Object instance = this;
            while (split.length > 0) {
                if (split.length == 1) {
                    return instance;
                }
                Class found = null;
                assert clazz != null;
                Class<?>[] classes = clazz.getDeclaredClasses();
                for (Class current : classes) {
                    if (current.getSimpleName().equalsIgnoreCase(toFieldName(split[0]))) {
                        found = current;
                        break;
                    }
                }
                try {
                    Field instanceField = clazz.getDeclaredField(toFieldName(split[0]));
                    setAccessible(instanceField);
                    Object value = instanceField.get(instance);
                    if (value == null) {
                        assert found != null;
                        value = found.newInstance();
                        instanceField.set(instance, value);
                    }
                    clazz = found;
                    instance = value;
                    split = Arrays.copyOfRange(split, 1, split.length);
                    continue;
                } catch (NoSuchFieldException | NoSuchMethodException | InvocationTargetException ignore) {
                }
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Translate a node to a java field name
     *
     * @param node node to translate
     * @return java field name
     */
    private String toFieldName(String node) {
        return node.toUpperCase().replaceAll("-", "_");
    }

    /**
     * Translate a field to a config node
     *
     * @param field to translate
     * @return config node name
     */
    private String toNodeName(String field) {
        return field.toLowerCase().replace("_", "-");
    }

    /**
     * Set some field to be accesible
     *
     * @param field to be accesible
     * @throws NoSuchFieldException   ...
     * @throws IllegalAccessException ...
     */
    private void setAccessible(Field field) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        field.setAccessible(true);
        int modifiers = field.getModifiers();
        if (Modifier.isFinal(modifiers)) {
            try {
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, modifiers & ~Modifier.FINAL);
            } catch (NoSuchFieldException e) {
                // Java 12 compatibility *this is fine*
                Method getDeclaredFields0 = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
                getDeclaredFields0.setAccessible(true);
                Field[] fields = (Field[]) getDeclaredFields0.invoke(Field.class, false);
                for (Field classField : fields) {
                    if ("modifiers".equals(classField.getName())) {
                        classField.setAccessible(true);
                        classField.set(field, modifiers & ~Modifier.FINAL);
                        break;
                    }
                }
            }
        }
    }

    private String repeat(final String s, final int n) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static Location emptyLocation() {
        return new Location(Bukkit.getWorld("world"), 1, 1, 1);
    }

    /**
     * Indicates that a field should be instantiated / created
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface Create {
    }

    /**
     * Indicates that a field cannot be modified
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface Final {
    }

    /**
     * Creates a comment
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.TYPE})
    public @interface Comment {

        String[] value();
    }

    /**
     * Any field or class with is not part of the config
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.TYPE})
    public @interface Ignore {
    }
}

