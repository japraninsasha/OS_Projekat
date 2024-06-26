package shell;

import assembler.Operations;
import fileSystem.FileSystem;
import kernel.Process;
import kernel.ProcessScheduler;
import memory.MemoryManager;
import memory.Ram;
import memory.SSD;

import java.io.File;

public class ShellExe {
    private static ProcessScheduler scheduler = new ProcessScheduler();

    public static void ls() {
        FileSystem.listFiles();
    }

    public static void cd(String par) {
        FileSystem.changeDirectory(par);
    }

    public static void md(String par) {
        FileSystem.makeDirectory(par);
    }

    public static void dd(String par) {
        FileSystem.deleteDirectory(par);
    }

    public static void df(String par) {
        FileSystem.deleteFile(par);
    }

    public static void rd(String name, String newName) {
        FileSystem.renameDirectory(name, newName);
    }

    public static void mem() {
        MemoryManager.printMemory();
    }

    public static void cf(String fileName) {
        boolean success = FileSystem.createFile(fileName, new byte[0]);
        if (success) {
            System.out.println("File " + fileName + " created successfully.");
        } else {
            System.out.println("Failed to create file " + fileName + ".");
        }
    }

    public static void write(String fileName, String fileContent) {
        byte[] contentBytes = fileContent.getBytes();
        boolean success = FileSystem.writeFile(fileName, contentBytes);
        if (success) {
            System.out.println("Content written to file " + fileName + " successfully.");
        } else {
            System.out.println("Failed to write content to file " + fileName + ".");
        }
    }

    public static void renameFile(String oldName, String newName) {
        boolean success = FileSystem.renameFile(oldName, newName);
        if (success) {
            System.out.println("File " + oldName + " renamed to " + newName + " successfully.");
        } else {
            System.out.println("Failed to rename file " + oldName + ".");
        }
    }

    public static void load(String par) {
        String filePath = "OS_Projekat\\Programs\\" + par; //change to AbsolutePath

        File programFile = new File(filePath);
        if (!programFile.exists()) {
            System.out.println("Error reading program file: " + filePath);
            return;
        }

        int pid = scheduler.getNewPID();
        Process process = new Process(pid, "Process" + pid, 100, 1024, par);
        scheduler.addProcess(process);
    }


    public static void exe(String par) {
        try {
            int pid = Integer.parseInt(par);
            scheduler.executeSpecificProcess(pid);
        } catch (NumberFormatException e) {
            ShellCommands.errorWithParameters();
        }
    }


    public static void pr() {
        scheduler.listOfProcesses();
    }

    public static void trm(String par) {
        try {
            int pid = Integer.parseInt(par);
            scheduler.terminateProcess(pid);
        } catch (NumberFormatException e) {
            ShellCommands.errorWithParameters();
        }
    }

    public static void clear() {
        // todo
    }

    public static void help() {
        String help;

        help = "LS \t\t Displays a list of files and subdirectories in a directory.\n";
        help += "CD \t\t Changes dir.\n";
        help += "MD \t\t Make dir.\n";
        help += "DD \t\t Delete dir.\n";
        help += "RD \t\t Rename dir.\n";
        help += "MEM \t\t Show RAM, registers and memory allocation table.\n";
        help += "LOAD \t\t Load and send process in the background. \n";
        help += "EXE \t\t Start executing processes. \n";
        help += "PR \t\t List of processes.\n";
        help += "TRM \t\t Terminate process.\n";
        help += "CLEAR \t\t Clears terminal.\n";
        help += "EXIT \t\t Closes program.";

        System.out.println(help);
    }

    public static void exit() {
        System.exit(1);
    }
}
