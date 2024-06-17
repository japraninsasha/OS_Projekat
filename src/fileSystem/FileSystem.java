package fileSystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

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
    }

    public void listFiles() {
        System.out.println("Content of: " + currentFolder.getName());
        System.out.println("Type\tName\t\t\tSize");
        File[] files = currentFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                System.out.println(file.isDirectory() ? "Folder \t" + file.getName()
                        : "File \t" + file.getName() + "\t" + file.length() + " bytes");
            }
        }
    }

    public void changeDirectory(String directory) {
        if (directory.equals("..") && !currentFolder.equals(rootFolder)) {
            currentFolder = currentFolder.getParentFile();
        } else {
            File newDir = new File(currentFolder, directory);
            if (newDir.exists() && newDir.isDirectory()) {
                currentFolder = newDir;
            } else {
                System.out.println("Directory not found: " + directory);
            }
        }
    }

    public void makeDirectory(String directory) {
        File newDir = new File(currentFolder, directory);
        if (!newDir.exists()) {
            if (newDir.mkdir()) {
                System.out.println("Directory created: " + newDir.getName());
            } else {
                System.out.println("Failed to create directory: " + newDir.getName());
            }
        } else {
            System.out.println("Directory already exists: " + newDir.getName());
        }
    }

    public void deleteDirectory(String directory) {
        File dirToDelete = new File(currentFolder, directory);
        if (dirToDelete.exists() && dirToDelete.isDirectory()) {
            if (dirToDelete.delete()) {
                System.out.println("Directory deleted: " + dirToDelete.getName());
            } else {
                System.out.println("Failed to delete directory: " + dirToDelete.getName());
            }
        } else {
            System.out.println("Directory not found: " + directory);
        }
    }

    public void renameDirectory(String oldName, String newName) {
        File oldDir = new File(currentFolder, oldName);
        File newDir = new File(currentFolder, newName);
        if (oldDir.exists() && oldDir.isDirectory()) {
            if (oldDir.renameTo(newDir)) {
                System.out.println("Directory renamed from " + oldName + " to " + newName);
            } else {
                System.out.println("Failed to rename directory: " + oldName);
            }
        } else {
            System.out.println("Directory not found: " + oldName);
        }
    }

    public void createFile(Process process) {
        String fileName = process.getProcessName().substring(0, process.getProcessName().indexOf('.')) + "_output.txt";
        File newFile = new File(process.getFilePath().getParent() + File.separator + fileName);
        try {
            if (newFile.createNewFile()) {
                FileWriter fw = new FileWriter(newFile);
                fw.write("Execution result: " + Operations.R4.value);
                fw.close();
                System.out.println("File created: " + newFile.getName());
            } else {
                System.out.println("Failed to create file: " + newFile.getName());
            }
        } catch (IOException e) {
            System.out.println("Error while creating file: " + e.getMessage());
        }
    }

    public void deleteFile(String fileName) {
        File fileToDelete = new File(currentFolder, fileName);
        if (fileToDelete.exists() && fileToDelete.isFile()) {
            if (fileToDelete.delete()) {
                System.out.println("File deleted: " + fileName);
            } else {
                System.out.println("Failed to delete file: " + fileName);
            }
        } else {
            System.out.println("File not found: " + fileName);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FileSystem fileSystem = new FileSystem(new File("path_to_root_folder"));

        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine().trim();
            String[] parts = command.split(" ");

            switch (parts[0]) {
                case "ls":
                    fileSystem.listFiles();
                    break;
                case "cd":
                    if (parts.length > 1) {
                        fileSystem.changeDirectory(parts[1]);
                    } else {
                        System.out.println("Usage: cd <directory>");
                    }
                    break;
                case "mkdir":
                    if (parts.length > 1) {
                        fileSystem.makeDirectory(parts[1]);
                    } else {
                        System.out.println("Usage: mkdir <directory>");
                    }
                    break;
                case "rmdir":
                    if (parts.length > 1) {
                        fileSystem.deleteDirectory(parts[1]);
                    } else {
                        System.out.println("Usage: rmdir <directory>");
                    }
                    break;
                case "mv":
                    if (parts.length > 2) {
                        fileSystem.renameDirectory(parts[1], parts[2]);
                    } else {
                        System.out.println("Usage: mv <old_name> <new_name>");
                    }
                    break;
                case "create":
                    // Assuming 'create' is a command to create a file (implementation needed)
                    // Example: create myprocess.java
                    break;
                case "delete":
                    // Assuming 'delete' is a command to delete a file (implementation needed)
                    // Example: delete myfile.txt
                    break;
                case "exit":
                    System.out.println("Exiting...");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Unknown command: " + parts[0]);
            }
        }
    }
}
