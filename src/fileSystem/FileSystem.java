package fileSystem;

import devices.DiskRequest;
import devices.SimulatedDisk;
import memory.MemoryFile;
import shell.Shell;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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
                    folder.addFile(new File(path.getFileName().toString(), content.length, 0));
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
        Directory dirToDelete = currentFolder.getSubDirectory(directory);
        if (dirToDelete != null) {
            Path dirPath = Paths.get(currentFolder.getAbsolutePath(), directory);
            try {
                deleteRecursively(dirPath);
                currentFolder.removeDirectory(directory);
                System.out.println("Directory " + directory + " deleted successfully.");
            } catch (IOException e) {
                System.out.println("Failed to delete directory " + directory + ": " + e.getMessage());
            }
        } else {
            System.out.println("No such directory: " + directory);
        }
    }

    private static void deleteRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteRecursively(entry);
                }
            }
        }
        Files.delete(path);
    }

    public static void renameDirectory(String oldName, String newName) {
        Directory dirToRename = currentFolder.getSubDirectory(oldName);
        if (dirToRename != null) {
            Path oldDirPath = Paths.get(currentFolder.getAbsolutePath(), oldName);
            Path newDirPath = Paths.get(currentFolder.getAbsolutePath(), newName);
            try {
                Files.move(oldDirPath, newDirPath);
                dirToRename.setName(newName);
                System.out.println("Directory renamed successfully.");
            } catch (IOException e) {
                System.out.println("Failed to rename directory: " + e.getMessage());
            }
        } else {
            System.out.println("No such directory: " + oldName);
        }
    }

    public static boolean createFile(String fileName, byte[] content) {
        if (content.length == 0) {
            content = new byte[1];
        }
        int startBlock = diskManager.allocate(content.length);
        if (startBlock != -1) {
            DiskRequest writeRequest = new DiskRequest(startBlock, false, content);
            simulatedDisk.addRequest(writeRequest);
            MemoryFile newFile = new MemoryFile(fileName, content);
            currentFolder.addFile(new File(fileName, content.length, startBlock));
            Shell.memory.save(newFile);
            processDiskRequests();

            Path filePath = Paths.get(currentFolder.getAbsolutePath(), fileName);
            try {
                Files.write(filePath, content, StandardOpenOption.CREATE);
                System.out.println("File " + fileName + " created successfully.");
                return true;
            } catch (IOException e) {
                System.out.println("Failed to create file " + fileName + ": " + e.getMessage());
                return false;
            }
        } else {
            System.out.println("Not enough space to create file");
            return false;
        }
    }

    public static boolean writeFile(String fileName, byte[] content) {
        File file = currentFolder.getFile(fileName);
        if (file != null) {
            Path filePath = Paths.get(currentFolder.getAbsolutePath(), fileName);
            try {
                Files.write(filePath, content, StandardOpenOption.APPEND);
                System.out.println("Content written to file " + fileName + " successfully.");
                return true;
            } catch (IOException e) {
                System.out.println("Failed to write content to file " + fileName + ": " + e.getMessage());
                return false;
            }
        } else {
            System.out.println("No such file: " + fileName);
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

            Path filePath = Paths.get(currentFolder.getAbsolutePath(), fileName);
            try {
                Files.delete(filePath);
                System.out.println("File " + fileName + " deleted successfully.");
            } catch (IOException e) {
                System.out.println("Failed to delete file " + fileName + ": " + e.getMessage());
            }
        } else {
            System.out.println("No such file: " + fileName);
        }
    }

    public static boolean renameFile(String oldName, String newName) {
        File file = currentFolder.getFile(oldName);
        if (file != null) {
            Path oldFilePath = Paths.get(currentFolder.getAbsolutePath(), oldName);
            Path newFilePath = Paths.get(currentFolder.getAbsolutePath(), newName);
            try {
                Files.move(oldFilePath, newFilePath);
                file.setName(newName);
                System.out.println("File renamed successfully.");
                return true;
            } catch (IOException e) {
                System.out.println("Failed to rename file: " + e.getMessage());
                return false;
            }
        } else {
            System.out.println("No such file: " + oldName);
            return false;
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
