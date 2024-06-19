package fileSystem;

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
import devices.DiskRequest;
import devices.SimulatedDisk;

public class FileSystem {
    private static Directory rootFolder;
    private static Directory currentFolder;
    private static DiskManager diskManager;
    private static SimulatedDisk simulatedDisk;

    public FileSystem(String rootPath, int diskSize) {
        rootFolder = new Directory(rootPath);
        currentFolder = rootFolder;
        diskManager = new DiskManager(diskSize);
        simulatedDisk = new SimulatedDisk();
        if (Files.exists(Paths.get(rootPath))) {
            loadFilesIntoMemory(rootFolder);
        } else {
            System.out.println("Root directory does not exist: " + rootPath);
        }
    }

    private void loadFilesIntoMemory(Directory folder) {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folder.toPath())) {
            for (Path path : directoryStream) {
                if (Files.isDirectory(path)) {
                    Directory subDir = new Directory(path.getFileName().toString());
                    folder.addDirectory(subDir);
                    loadFilesIntoMemory(subDir);
                } else {
                    byte[] content = Files.readAllBytes(path);
                    MemoryFile newFile = new MemoryFile(path.getFileName().toString(), content);
                    folder.addFile(new File(path.getFileName().toString(), content.length, 0)); // Assuming start block is 0 for simplicity
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
        currentFolder.listFiles();
    }

    public static void changeDirectory(String directory) {
        Directory newDir = currentFolder.getSubDirectory(directory);
        if (newDir != null) {
            currentFolder = newDir;
        } else if (directory.equals("..") && currentFolder.getParent() != null) {
            currentFolder = currentFolder.getParent();
        } else {
            System.out.println("No such directory: " + directory);
        }
    }

    public static void makeDirectory(String directory) {
        Directory newDir = new Directory(directory);
        currentFolder.addDirectory(newDir);
        Path newDirPath = Paths.get(currentFolder.getAbsolutePath(), directory);
        try {
            Files.createDirectory(newDirPath);
            System.out.println("Directory " + directory + " created successfully.");
        } catch (IOException e) {
            System.out.println("Failed to create directory " + directory + ": " + e.getMessage());
        }
    }


    public static void deleteDirectory(String directory) {
        currentFolder.removeDirectory(directory);
    }

    public static void renameDirectory(String oldName, String newName) {
        currentFolder.renameDirectory(oldName, newName);
    }

    public static boolean createFile(String fileName, byte[] content) {
        int startBlock = diskManager.allocate(content.length);
        if (startBlock != -1) {
            DiskRequest writeRequest = new DiskRequest(startBlock, false, content);
            simulatedDisk.addRequest(writeRequest);
            MemoryFile newFile = new MemoryFile(fileName, content);
            currentFolder.addFile(new File(fileName, content.length, startBlock));
            Shell.memory.save(newFile);
            processDiskRequests(); // Pozivanje obrade diskovnih zahteva odmah nakon dodavanja
            System.out.println("File " + fileName + " created successfully.");
            return true;
        } else {
            System.out.println("Not enough space to create file");
            return false;
        }
    }



    public static void deleteFile(String fileName) {
        File file = currentFolder.getFile(fileName);
        if (file != null) {
            diskManager.deallocate(file.getStartBlock(), file.getSize());
            DiskRequest deleteRequest = new DiskRequest(file.getStartBlock(), false, null);
            simulatedDisk.addRequest(deleteRequest);
            currentFolder.removeFile(fileName);
            Shell.memory.deleteFile(Shell.memory.getFile(fileName));
        } else {
            System.out.println("No such file: " + fileName);
        }
    }

    public static void processDiskRequests() {
        simulatedDisk.processNextRequest();
    }

    public static void printRequestQueue() {
        simulatedDisk.printRequestQueue();
    }

    public static Directory getCurrentFolder() {
        return currentFolder;
    }
}
