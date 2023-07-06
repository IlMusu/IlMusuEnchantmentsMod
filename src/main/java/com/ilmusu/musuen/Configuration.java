package com.ilmusu.musuen;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Configuration
{
    public static class ConfigData
    {
        protected String dataString;
        protected Object dataValue;
        protected String description = "";

        public ConfigData()
        {
        }
    }

    // The configuration File related to this configuration
    private final String configurationFileName;
    private final File configurationFile;
    // The data that has been currently parsed from the configuration File
    private final HashMap<String, ConfigData> configuration = new LinkedHashMap<>();
    // A fixer for making data compatible with different versions
    private final BiFunction<String, String, String> fixer;

    public Configuration(String mod_id, String configurationFileName)
    {
        this(mod_id, configurationFileName, (name, value) -> value);
    }

    public Configuration(String mod_id, String configurationFileName, BiFunction<String, String, String> fixer)
    {
        // Gets the directory containing all the configuration files from Fabric
        Path path = Path.of(FabricLoader.getInstance().getConfigDir()+"/"+mod_id);
        // Creates the configuration File in memory
        this.configurationFileName = configurationFileName+".properties";
        this.configurationFile = path.resolve(this.configurationFileName).toFile();
        this.fixer = fixer;
    }

    public void load()
    {
        // Creates the configuration file on disk (only if not already existing)
        this.createConfigurationFile();
        // Loads the current file configuration on the memory
        this.reloadConfigurationFromFile();
        // Rewrite the file in case the fixed applied changes
        this.writeConfigurationFile();
    }

    private void createConfigurationFile()
    {
        if(this.configurationFile.exists())
            return;

        try
        {
            this.configurationFile.getParentFile().mkdirs();
            this.configurationFile.createNewFile();
        }
        catch (IOException exception)
        {
            Resources.LOGGER.error("Could not create configuration file ("+configurationFileName+") !");
            Resources.LOGGER.error(exception.getLocalizedMessage());
        }
    }

    public void writeConfigurationFile()
    {
        if(!this.configurationFile.exists())
            return;

        try
        {
            FileWriter writer = new FileWriter(this.configurationFile);
            // Resetting the file to an empty file
            writer.write("");
            // Setting all the current configurations
            for(String key : this.configuration.keySet())
            {
                ConfigData data = this.configuration.get(key);
                if(!data.description.isEmpty())
                    writer.append(data.description).append("\n");
                writer.append(key).append("=").append(data.dataString).append("\n\n");
            }
            // Closing the file because there is noting more to do
            writer.close();
        }
        catch (IOException exception)
        {
            Resources.LOGGER.error("Could not write configuration file ("+configurationFileName+") !");
            Resources.LOGGER.error(exception.getLocalizedMessage());
        }
    }

    private void reloadConfigurationFromFile()
    {
        Scanner reader;
        try
        {
            reader = new Scanner(this.configurationFile);
        }
        catch (FileNotFoundException exception)
        {
            Resources.LOGGER.error("Could not load configuration file ("+configurationFileName+") !");
            Resources.LOGGER.error(exception.getLocalizedMessage());
            return;
        }

        for(int line = 1; reader.hasNextLine(); line ++)
            parseConfigurationEntry(reader.nextLine(), line);
    }

    private void parseConfigurationEntry(String entry, int line)
    {
        // The entry is not considered if it is an empty line or a comment
        if(entry.isEmpty() || entry.startsWith("#"))
            return;

        // The configuration is composed of two parts separated by "="
        String[] parts = entry.split("=", 2);
        // So, if there are not two, parts, it is a syntax error
        if(parts.length != 2)
        {
            Resources.LOGGER.error("Syntax error in configuration file ("+configurationFileName+") at line "+line+"!");
            return;
        }

        String key = parts[0].trim();
        ConfigData data = new ConfigData();
        data.dataString = this.fixer.apply(key, parts[1].trim());
        this.configuration.put(key, data);
    }

    private void setConfig(String key, Object value, String description, Function<Object, String> writer)
    {
        // The name and the value are trimmed just to be sure
        key = key.trim();
        // Setting the new configuration
        ConfigData config = this.configuration.getOrDefault(key, new ConfigData());
        config.description = description;
        config.dataString = writer.apply(value);
        config.dataValue = value;
        this.configuration.put(key, config);
    }

    public void setConfigIfAbsent(String key, Object value, Function<Object, String> writer, Function<String, Object> reader)
    {
        setConfigIfAbsent(key, value, "", writer, reader);
    }

    public void setConfigIfAbsent(String key, Object value, String description, Function<Object, String> writer, Function<String, Object> reader)
    {
        // The name and the value are trimmed just to be sure
        key = key.trim();
        // Getting the existing configuration data
        ConfigData data = this.configuration.get(key);
        // If different from null, updating only the description
        if(data != null)
        {
            // Updating description and value
            data.description = description;
            data.dataValue = reader.apply(data.dataString);
            return;
        }
        // Otherwise setting the configuration
        setConfig(key, value, description, writer);
    }

    public Object getConfigValue(String key)
    {
        // The name and the value are trimmed just to be sure
        key = key.trim();
        return this.configuration.get(key).dataValue;
    }
}