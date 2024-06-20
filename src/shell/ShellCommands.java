package shell;

import java.util.ArrayList;

public class ShellCommands {
    private static ArrayList<String> commandList = new ArrayList<>();
    private static int iter;

    public static void readCommand(String command) {
        commandList.add(command);
        iter = commandList.size();
        command = command.toLowerCase();
        String commands[];
        commands = command.split(" ");

        switch (commands[0]) {
            case "ls":
                if (commands.length == 1) {
                    ShellExe.ls();
                } else
                    errorWithParameters();
                break;

            case "cd":
                if (commands.length == 2) {
                    String parameter = commands[1];
                    ShellExe.cd(parameter);
                } else
                    errorWithParameters();
                break;

            case "md":
                if (commands.length == 2) {
                    String parameter = commands[1];
                    ShellExe.md(parameter);
                } else
                    errorWithParameters();
                break;

            case "dd":
                if (commands.length == 2) {
                    String parameter = commands[1];
                    ShellExe.dd(parameter);
                } else if (commands.length == 3) {
                    String parameter1 = commands[1];
                    String parameter2 = commands[2];
                    if (parameter1.equals("-f")) {
                        ShellExe.df(parameter2);
                    } else
                        errorWithParameters();
                } else
                    errorWithParameters();
                break;

            case "rd":
                if (commands.length == 3) {
                    String parameter1 = commands[1];
                    String parameter2 = commands[2];
                    ShellExe.rd(parameter1, parameter2);
                } else
                    errorWithParameters();
                break;

            case "mem":
                if (commands.length == 1) {
                    ShellExe.mem();
                } else if (commands.length == 2) {
                    String parameter = commands[1];
                    if (parameter.equals("-m")) {
                        ShellExe.memM();
                    } else if (parameter.equals("-r")) {
                        ShellExe.memR();
                    } else if (parameter.equals("-s")) {
                        ShellExe.memS();
                    } else
                        errorWithParameters();
                } else
                    errorWithParameters();
                break;

            case "load":
                if (commands.length == 2) {
                    String parameter = commands[1];
                    ShellExe.load(parameter);
                } else
                    errorWithParameters();
                break;

            case "exe":
                if (commands.length == 2) {
                    String parameter = commands[1];
                    ShellExe.exe(parameter);
                } else {
                    errorWithParameters();
                }
                break;

            case "pr":
                if (commands.length == 1) {
                    ShellExe.pr();
                } else
                    errorWithParameters();
                break;

            case "trm":
                if (commands.length == 2) {
                    String parameter = commands[1];
                    ShellExe.trm(parameter);
                } else
                    errorWithParameters();
                break;


            case "clear":
                if (commands.length == 1) {
                    ShellExe.clear();
                } else
                    errorWithParameters();
                break;

            case "exit":
                if (commands.length == 1) {
                    ShellExe.exit();
                } else
                    errorWithParameters();
                break;

            case "help":
                if (commands.length == 1) {
                    ShellExe.help();
                } else
                    errorWithParameters();
                break;

            case "cf":
                if (commands.length == 2) {
                    String fileName = commands[1];
                    ShellExe.cf(fileName);
                } else {
                    errorWithParameters();
                }
                break;

            case "write":
                if (commands.length >= 3) {
                    String fileName = commands[1];
                    String fileContent = command.substring(command.indexOf(fileName) + fileName.length()).trim();
                    ShellExe.write(fileName, fileContent);
                } else {
                    errorWithParameters();
                }
                break;

            case "rename":
                if (commands.length == 3) {
                    String oldName = commands[1];
                    String newName = commands[2];
                    ShellExe.renameFile(oldName, newName);
                } else {
                    errorWithParameters();
                }
                break;

            default:
                System.out.println("That command doesn't exist!");
        }
    }

    public static void errorWithParameters() {
        String s = "Parameters for command are incorrect!\n";
        System.out.println(s);
    }
}
