package shell;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Shell.boot(); // Inicijalizacija sistema
        Scanner scanner = new Scanner(System.in);
        String command;

        System.out.println("Welcome to the Shell. Type 'help' for a list of commands.");

        while (true) {
            System.out.print("shell> ");
            command = scanner.nextLine();
            ShellCommands.readCommand(command);
        }
    }
}

