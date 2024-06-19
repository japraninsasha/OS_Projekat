package fileSystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import assembler.Operations;
import kernel.Process;
import memory.MemoryFile;
import shell.Shell;

public class FileSystem {
    private static File rootFolder;
    private static File currentFolder;

    public FileSystem(File path) {
        rootFolder = path;
        currentFolder = rootFolder;
        loadFilesIntoMemory(currentFolder);
    }

    private void loadFilesIntoMemory(File folder) {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folder.toPath())) {
            for (Path path : directoryStream) {
                if (Files.isDirectory(path)) {
                    loadFilesIntoMemory(path.toFile());
                } else {
                    byte[] content = Files.readAllBytes(path);
                    MemoryFile newFile = new MemoryFile(path.getFileName().toString(), content);
                    if (!Shell.memory.contains(path.getFileName().toString())) {
                        Shell.memory.save(newFile);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void listFiles() {
        System.out.println("Content of: " + currentFolder.getName());
        System.out.println("Type\tName\t\t\tSize");
        File[] files = currentFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                byte[] fileContent = null;
                try {
                    if (!file.isDirectory())
                        fileContent = Files.readAllBytes(file.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(file.isDirectory() ? ("Folder \t" + file.getName())
                        : ("File" + "\t" + file.getName()
                        + (file.getName().length() < 16 ? "\t\t" + fileContent.length + " B"
                        : "\t" + fileContent.length + " B")));
            }
        }
    }

    public static void changeDirectory(String directory) {
        if (directory.equals("..") && !currentFolder.equals(rootFolder)) {
            currentFolder = currentFolder.getParentFile();
        } else {
            File newDir = new File(currentFolder, directory);
            if (newDir.isDirectory()) {
                currentFolder = newDir;
            } else {
                System.out.println("No such directory: " + directory);
            }
        }
    }

    public static void makeDirectory(String directory) {
        File folder = new File(currentFolder, directory);
        if (!folder.exists()) {
            folder.mkdir();
        } else {
            System.out.println("Directory already exists: " + directory);
        }
    }

    public static void deleteDirectory(String directory) {
        File folder = new File(currentFolder, directory);
        if (folder.exists() && folder.isDirectory()) {
            deleteRecursively(folder);
        } else {
            System.out.println("No such directory: " + directory);
        }
    }

    private static void deleteRecursively(File file) {
        File[] allContents = file.listFiles();
        if (allContents != null) {
            for (File content : allContents) {
                deleteRecursively(content);
            }
        }
        file.delete();
    }

    public static void renameDirectory(String oldName, String newName) {
        File oldDir = new File(currentFolder, oldName);
        File newDir = new File(currentFolder, newName);
        if (oldDir.exists() && oldDir.isDirectory()) {
            oldDir.renameTo(newDir);
        } else {
            System.out.println("No such directory: " + oldName);
        }
    }

    public static void createFile(Process process) {
        String processName = process.getProcessName();
        int dotIndex = processName.indexOf('.');
        String name;
        if (dotIndex != -1) {
            name = processName.substring(0, dotIndex) + "_output";
        } else {
            name = processName + "_output";
        }

        File newFile = new File(process.getFilePath().getParent().toFile(), name + ".txt");
        try {
            if (newFile.createNewFile()) {
                try (FileWriter fw = new FileWriter(newFile)) {
                    fw.write("Rezultat izvrsavanja: " + Operations.R4.value);
                }
            } else {
                System.out.println("File already exists: " + newFile.getName());
            }
        } catch (IOException e) {
            System.out.println("Error while creating file");
        }
    }


    public static void deleteFile(String name) {
        File file = new File(currentFolder, name);
        if (file.exists() && !file.isDirectory()) {
            file.delete();
            if (Shell.memory.contains(name)) {
                Shell.memory.deleteFile(Shell.memory.getFile(name));
            }
        } else {
            System.out.println("No such file: " + name);
        }
    }

    public static File getCurrentFolder() {
        return currentFolder;
    }
}
