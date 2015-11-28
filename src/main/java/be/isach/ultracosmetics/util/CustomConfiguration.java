package be.isach.ultracosmetics.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class CustomConfiguration extends YamlConfiguration {

    private Map<String, List<String>> comments = null;
    private boolean newLineAfterHeader = false;
    private boolean newLinePerKey = false;

    private CustomConfiguration() {
        super();
        this.comments = new LinkedHashMap<>();
    }

    /*public void addDefault(String path, Object defaultValue, String... comments) {
        if (defaultValue != null && comments != null && comments.length > 0 && !this.comments.containsKey(path)) {
            List<String> commentsList = new ArrayList<>();
            for (String comment : comments) {
                if (comment != null) commentsList.add(comment);
                else commentsList.add("");
            }
            this.comments.put(path, commentsList);
        }
        super.addDefault(path, defaultValue);
    }*/

    public void addDefault(String path, Object defaultValue, String... comments) {
        if (!contains(path))
            set(path, defaultValue, comments);
    }

    public ConfigurationSection createSection(String path, String... comments) {
        if (path != null && comments != null && comments.length > 0) {
            List<String> commentsList = new ArrayList<>();
            for (String comment : comments) {
                if (comment != null) commentsList.add(comment);
                else commentsList.add("");
            }
            this.comments.put(path, commentsList);
        }
        return super.createSection(path);
    }

    public Map<String, List<String>> getComments() {
        return new LinkedHashMap<>(this.comments);
    }

    public List<String> getComments(String path) {
        return this.comments.containsKey(path) ? new ArrayList<>(this.comments.get(path)) : new ArrayList<String>();
    }

    @Override
    public void load(File file) throws IOException, InvalidConfigurationException {
        super.load(file);

        BufferedReader configReader = null;
        List<String> configLines = new ArrayList<>();
        try {
            configReader = new BufferedReader(new FileReader(file));
            String configReadLine;
            while ((configReadLine = configReader.readLine()) != null) configLines.add(configReadLine);
        } finally {
            if (configReader != null) configReader.close();
        }

        boolean hasHeader = configLines.size() < 2 || !trim(configLines.get(1)).isEmpty();

        Map<String, List<String>> configComments = new LinkedHashMap<>();
        for (int lineIndex = 0; lineIndex < configLines.size(); lineIndex++) {
            String configLine = configLines.get(lineIndex);
            String trimmedLine = trimPrefixSpaces(configLine);
            if (trimmedLine.startsWith("#") && (lineIndex > 0 || !hasHeader)) {
                String configKey = getPathToComment(configLines, lineIndex, configLine);
                if (configKey != null) {
                    List<String> keyComments = configComments.get(configKey);
                    if (keyComments == null) keyComments = new ArrayList<>();
                    keyComments.add(trimmedLine.substring(trimmedLine.startsWith("# ") ? 2 : 1));
                    configComments.put(configKey, keyComments);
                }
            }
        }
        comments = configComments;
    }

    public void load(File file, boolean loadComments) throws IOException, InvalidConfigurationException {
        if (loadComments) this.load(file);
        else super.load(file);
    }

    @Override
    public void save(File file) throws IOException {
        super.save(file);

        List<String> configContent = new ArrayList<>();
        BufferedReader configReader = null;
        try {
            configReader = new BufferedReader(new FileReader(file));
            String configReadLine;
            while ((configReadLine = configReader.readLine()) != null) configContent.add(configReadLine);
        } finally {
            if (configReader != null) configReader.close();
        }

        BufferedWriter configWriter = null;
        try {
            configWriter = new BufferedWriter(new FileWriter(file));
            configWriter.write("");
            for (int lineIndex = 0; lineIndex < configContent.size(); lineIndex++) {
                String configLine = configContent.get(lineIndex);
                String configKey = null;
                if (!configLine.startsWith("#") && configLine.contains(":"))
                    configKey = getPathToKey(configContent, lineIndex, configLine);
                if (configKey != null && this.comments.containsKey(configKey)) {
                    int numOfSpaces = getPrefixSpaceCount(configLine);
                    String spacePrefix = "";
                    for (int i = 0; i < numOfSpaces; i++) spacePrefix += " ";
                    List<String> configComments = this.comments.get(configKey);
                    if (configComments != null) {
                        for (String comment : configComments) {
                            configWriter.append(spacePrefix).append("# ").append(comment);
                            configWriter.newLine();
                        }
                    }
                }
                configWriter.append(configLine);
                configWriter.newLine();
                boolean isComment = configLine.startsWith("#");
                if (this.newLineAfterHeader && lineIndex == 0 && isComment) {
                    configWriter.newLine();
                } else if (this.newLinePerKey && lineIndex < configContent.size() - 1 && !isComment) {
                    String nextConfigLine = configContent.get(lineIndex + 1);
                    if (nextConfigLine != null && !nextConfigLine.startsWith(" ")) {
                        if (!nextConfigLine.startsWith("'") && !nextConfigLine.startsWith("-")) configWriter.newLine();
                    }
                }
            }
        } finally {
            if (configWriter != null) configWriter.close();
        }
    }

    public void set(String key, Object value, String... comments) {
        if (value != null) {
            if (comments != null) {
                if (comments.length > 0) {
                    List<String> commentsList = new ArrayList<>();
                    for (String comment : comments) {
                        if (comment != null) commentsList.add(comment);
                        else commentsList.add("");
                    }
                    this.comments.put(key, commentsList);
                } else {
                    this.comments.remove(key);
                }
            }
        } else {
            this.comments.remove(key);
        }
        super.set(key, value);
    }

    public void setNewLineAfterHeader(boolean newLineAfterHeader) {
        this.newLineAfterHeader = newLineAfterHeader;
    }

    public void setNewLinePerKey(boolean newLinePerKey) {
        this.newLinePerKey = newLinePerKey;
    }

    public boolean shouldAddNewLineAfterHeader() {
        return this.newLineAfterHeader;
    }

    public boolean shouldAddNewLinePerKey() {
        return this.newLinePerKey;
    }

    public static CustomConfiguration loadConfiguration(File file) {
        CustomConfiguration config = new CustomConfiguration();
        try {
            config.load(file);
        } catch (FileNotFoundException ignored) {
            ;
        } catch (IOException | InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
        }
        return config;
    }

    private static String getPathToComment(List<String> configContents, int lineIndex, String configLine) {
        if (configContents != null && lineIndex >= 0 && lineIndex < configContents.size() - 1 && configLine != null) {
            String trimmedConfigLine = trimPrefixSpaces(configLine);
            if (trimmedConfigLine.startsWith("#")) {
                int currentIndex = lineIndex;
                while (currentIndex < configContents.size() - 1) {
                    currentIndex++;
                    String currentLine = configContents.get(currentIndex);
                    String trimmedCurrentLine = trimPrefixSpaces(currentLine);
                    if (!trimmedCurrentLine.startsWith("#")) {
                        if (trimmedCurrentLine.contains(":")) {
                            return getPathToKey(configContents, currentIndex, currentLine);
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static String getPathToKey(List<String> configContents, int lineIndex, String configLine) {
        if (configContents != null && lineIndex >= 0 && lineIndex < configContents.size() && configLine != null) {
            if (!configLine.startsWith("#") && configLine.contains(":")) {
                int spacesBeforeKey = getPrefixSpaceCount(configLine);
                String key = trimPrefixSpaces(configLine.substring(0, configLine.indexOf(':')));
                if (spacesBeforeKey > 0) {
                    int currentIndex = lineIndex;
                    int previousSpacesBeforeCurrentLine = -1;
                    boolean atZeroSpaces = false;

                    while (currentIndex > 0) {
                        currentIndex--;
                        String currentLine = configContents.get(currentIndex);
                        int spacesBeforeCurrentLine = getPrefixSpaceCount(currentLine);

                        if (trim(currentLine).isEmpty()) break;
                        if (!trim(currentLine).startsWith("#")) {
                            if (spacesBeforeCurrentLine < spacesBeforeKey) {
                                if (currentLine.contains(":")) {
                                    if (spacesBeforeCurrentLine > 0 || !atZeroSpaces) {
                                        if (spacesBeforeCurrentLine == 0) atZeroSpaces = true;
                                        if (previousSpacesBeforeCurrentLine == -1 || spacesBeforeCurrentLine < previousSpacesBeforeCurrentLine) {
                                            previousSpacesBeforeCurrentLine = spacesBeforeCurrentLine;
                                            key = trimPrefixSpaces(currentLine.substring(0, currentLine.indexOf(":"))) + "." + key;
                                        }
                                    } else {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                return key;
            }
        }
        return null;
    }

    private static int getPrefixSpaceCount(String aString) {
        int spaceCount = 0;
        if (aString != null && aString.contains(" ")) {
            for (char aCharacter : aString.toCharArray()) {
                if (aCharacter == ' ') spaceCount++;
                else break;
            }
        }
        return spaceCount;
    }

    private static String trim(String aString) {
        return aString != null ? aString.trim().replace(System.lineSeparator(), "") : "";
    }

    private static String trimPrefixSpaces(String aString) {
        if (aString != null) {
            while (aString.startsWith(" ")) aString = aString.substring(1);
        }
        return aString;
    }

}
