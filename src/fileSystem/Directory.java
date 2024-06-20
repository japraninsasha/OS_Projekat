package fileSystem;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Directory {
    private String name;
    private Directory parent;
    private List<Directory> subDirectories;
    private Map<String, File> files;

    public Directory(String name) {
        this.name = name;
        this.subDirectories = new ArrayList<>();
        this.files = new HashMap<>();
    }

    public void addDirectory(Directory directory) {
        directory.setParent(this);
        subDirectories.add(directory);
    }

    public void removeDirectory(String name) {
        subDirectories.removeIf(dir -> dir.getName().equals(name));
    }

    public Directory getSubDirectory(String name) {
        for (Directory dir : subDirectories) {
            if (dir.getName().equals(name)) {
                return dir;
            }
        }
        return null;
    }


    public void addFile(File file) {
        files.put(file.getName(), file);
    }

    public void removeFile(String name) {
        files.remove(name);
    }

    public File getFile(String name) {
        return files.get(name);
    }

    public void listFiles() {
        System.out.println("Content of: " + name);
        System.out.println("Type\tName\t\t\tSize");
        for (Directory dir : subDirectories) {
            System.out.println("Folder \t" + dir.getName());
        }
        for (File file : files.values()) {
            System.out.println("File\t" + file.getName() + "\t" + file.getSize() + " B");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Directory getParent() {
        return parent;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }

    public Path toPath() {
        if (parent == null) {
            return Paths.get(name);
        }
        return parent.toPath().resolve(name);
    }

    public String getAbsolutePath() {
        return toPath().toAbsolutePath().toString();
    }
}
