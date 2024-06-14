package shell;

import java.io.File;
import assembler.Constants;
import assembler.Operations;
import fileSystem.FileSystem;
import kernel.Process;
import kernel.ProcessScheduler;
import memory.MemoryManager;
import memory.SSD;

public class Shell {

    public static FileSystem tree;
    public static MemoryManager manager;
    public static Process currentlyExecuting = null;
    public static SSD memory;
    public static int PC; // Program counter
    public static String IR; // Instruction register
    public static int base;
    public static int limit;

    public static void boot() {
        new ProcessScheduler();
        memory = new SSD();
        manager = new MemoryManager();
        tree = new FileSystem(new File("Programs"));
    }

    public static String assemblerToMachineInstruction(String command) {
        String[] parts = command.split("[ ,]");
        String instruction = getOperationCode(parts[0]);

        switch (parts[0]) {
            case "HALT":
                return instruction;
            case "JMP":
                instruction += toBinary(parts[1]);
                break;
            case "JMPL":
            case "JMPG":
            case "JMPE":
            case "JMPD":
                instruction += getRegisterCode(parts[1]) + toBinary(parts[2]) + toBinary(parts[3]);
                break;
            case "INC":
            case "DEC":
                instruction += getRegisterCode(parts[1]);
                break;
            default:
                instruction += getRegisterCode(parts[1]);
                if (isRegister(parts[2])) {
                    instruction += getRegisterCode(parts[2]);
                } else {
                    instruction += toBinary(parts[2]);
                }
                break;
        }

        return instruction;
    }

    private static String getOperationCode(String operation) {
        switch (operation) {
            case "HALT": return Operations.halt;
            case "STORE": return Operations.store;
            case "ADD": return Operations.add;
            case "SUB": return Operations.sub;
            case "MUL": return Operations.mul;
            case "JMP": return Operations.jmp;
            case "INC": return Operations.inc;
            case "DEC": return Operations.dec;
            case "LOAD": return Operations.load;
            default: return "";
        }
    }

    private static boolean isConditionalJump(String operation) {
        return operation.equals("JMPL") || operation.equals("JMPG") || operation.equals("JMPE") || operation.equals("JMPD");
    }

    private static boolean isRegister(String value) {
        return value.equals("R1") || value.equals("R2") || value.equals("R3") || value.equals("R4");
    }

    private static String getRegisterCode(String register) {
        switch (register) {
            case "R1": return Constants.R1;
            case "R2": return Constants.R2;
            case "R3": return Constants.R3;
            case "R4": return Constants.R4;
            default: return "";
        }
    }

    private static String toBinary(String value) {
        int num = Integer.parseInt(value);
        String binary = Integer.toBinaryString(num);
        return String.format("%8s", binary).replace(' ', '0');
    }

    public static void executeMachineInstruction() {
        String operation = IR.substring(0, 4);
        boolean programCounterChanged = false;

        switch (operation) {
            case Operations.halt:
                Operations.halt();
                break;
            case Operations.store:
                Operations.store(IR.substring(4, 8), IR.substring(8, 16));
                break;
            case Operations.add:
                executeArithmeticOperation(Operations.add);
                break;
            case Operations.sub:
                executeArithmeticOperation(Operations.sub);
                break;
            case Operations.mul:
                executeArithmeticOperation(Operations.mul);
                break;
            case Operations.jmp:
                Operations.jmp(IR.substring(4, 12));
                programCounterChanged = true;
                break;
            case Operations.load:
                Operations.load(IR.substring(4, 8), IR.substring(8, 16));
                break;
            case Operations.inc:
                Operations.inc(IR.substring(4, 8));
                break;
            case Operations.dec:
                Operations.dec(IR.substring(4, 8));
                break;
        }

        if (!programCounterChanged) {
            PC++;
        }
    }

    private static void executeArithmeticOperation(ArithmeticOperation operation) {
        String r1 = IR.substring(4, 8);
        if (IR.length() == 12) {
            String r2 = IR.substring(8, 12);
            operation.execute(r1, r2);
        } else if (IR.length() == 16) {
            String value = IR.substring(8, 16);
            operation.execute(r1, value);
        }
    }

    public static String fromIntToInstruction(int value) {
        String binary = Integer.toBinaryString(value);
        while (binary.length() < 24) {
            binary = "0" + binary;
        }
        return binary;
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

    @FunctionalInterface
    private interface ArithmeticOperation {
        void execute(String r1, String operand);
    }
}
