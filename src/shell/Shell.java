package shell;

import assembler.Constants;
import assembler.Operations;
import kernel.Process;
import kernel.ProcessScheduler;
import memory.SSD;
import memory.MemoryManager;
import fileSystem.FileSystem;

import java.io.File;

public class Shell {

    public static SSD memory;
    public static FileSystem tree;
    public static Process currentlyExecuting = null;
    public static MemoryManager manager; // Dodali smo polje manager
    public static int PC; // Program counter
    public static String IR; // Instruction register
    public static int base;
    public static int limit;

    public static void boot() {
        memory = new SSD();
        tree = new FileSystem("C:\\Users\\sasaj\\Desktop\\ostest\\OS_Projekat\\Programs", 2048); // Pretpostavka da se disk veličina definiše kao 2048 blokova
        manager = new MemoryManager(4, 1024, 2048); // Inicijalizacija MemoryManager-a
    }

    public static String asemblerToMachineInstruction(String command) {
        String instruction = "";
        String arr[] = command.split("[ |,]");

        // prevodjenje operacije
        switch (arr[0].toUpperCase()) {
            case "HALT":
                instruction += Operations.halt;
                break;
            case "LOAD":
                instruction += Operations.load;
                break;
            case "STORE":
                instruction += Operations.store;
                break;
            case "ADD":
                instruction += Operations.add;
                break;
            case "SUB":
                instruction += Operations.sub;
                break;
            case "MUL":
                instruction += Operations.mul;
                break;
            case "DIV":
                instruction += Operations.div;
                break;
            case "JMP":
                instruction += Operations.jmp;
                break;
            case "JMPZ":
                instruction += Operations.jmpz;
                break;
            case "JMPN":
                instruction += Operations.jmpn;
                break;
            case "INC":
                instruction += Operations.inc;
                break;
            case "DEC":
                instruction += Operations.dec;
                break;
            default:
                System.out.println("Unknown command: " + arr[0]);
                return null;
        }

        if (arr.length > 1) {
            if (arr[0].equalsIgnoreCase("HALT")) {
                return instruction;
            } else if (arr[0].equalsIgnoreCase("JMP")) {
                instruction += toBinary(arr[1]);
            } else {
                instruction += getRegisterBinary(arr[1]);
                if (arr.length == 3) {
                    instruction += toBinary(arr[2]);
                }
            }
        }

        return instruction;
    }

    private static String toBinary(String s) {
        int num = Integer.parseInt(s);
        String bin = Integer.toBinaryString(num);
        return String.format("%8s", bin).replace(' ', '0'); // pad to 8 bits
    }

    private static String getRegisterBinary(String reg) {
        switch (reg.toUpperCase()) {
            case "R1":
                return Constants.R1;
            case "R2":
                return Constants.R2;
            case "R3":
                return Constants.R3;
            case "R4":
                return Constants.R4;
            default:
                return "";
        }
    }

    public static void executeMachineInstruction() {
        String operation = IR.substring(0, 4);

        switch (operation) {
            case Operations.halt:
                Operations.halt();
                break;
            case Operations.load:
                Operations.load(IR.substring(4, 8));
                break;
            case Operations.store:
                Operations.store(IR.substring(4, 8));
                break;
            case Operations.add:
                Operations.add(IR.substring(4));
                break;
            case Operations.sub:
                Operations.sub(IR.substring(4));
                break;
            case Operations.mul:
                Operations.mul(IR.substring(4));
                break;
            case Operations.div:
                Operations.div(IR.substring(4));
                break;
            case Operations.jmp:
                Operations.jmp(IR.substring(4, 12));
                break;
            case Operations.jmpz:
                Operations.jmpz(IR.substring(4, 12));
                break;
            case Operations.jmpn:
                Operations.jmpn(IR.substring(4, 12));
                break;
            case Operations.inc:
                Operations.inc();
                break;
            case Operations.dec:
                Operations.dec();
                break;
            default:
                System.out.println("Unknown operation code: " + operation);
                break;
        }

        PC++;
    }

    public static String fromIntToInstruction(int temp) {
        String bin = Integer.toBinaryString(temp);
        return String.format("%16s", bin).replace(' ', '0'); // pad to 16 bits
    }

    public static void saveValues() {
        int[] registers = {
                Operations.R1.value,
                Operations.R2.value,
                Operations.R3.value,
                Operations.R4.value
        };
        currentlyExecuting.setValuesOfRegisters(registers);
        currentlyExecuting.setPcValue(Shell.PC);
    }

    public static void loadValues() {
        int[] registers = currentlyExecuting.getValuesOfRegisters();
        Operations.R1.value = registers[0];
        Operations.R2.value = registers[1];
        Operations.R3.value = registers[2];
        Operations.R4.value = registers[3];
        Shell.PC = currentlyExecuting.getPcValue();
    }
}
