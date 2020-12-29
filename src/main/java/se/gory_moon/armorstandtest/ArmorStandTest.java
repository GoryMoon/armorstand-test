package se.gory_moon.armorstandtest;

import org.bukkit.plugin.java.JavaPlugin;

public final class ArmorStandTest extends JavaPlugin {

    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
            getLogger().severe("Disabled due to no ProtocolLib dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getCommand("test").setExecutor(new Command());
    }

    @Override
    public void onDisable() {
        getLogger().info(String.format("Disabled version %s", getDescription().getVersion()));
    }
}
