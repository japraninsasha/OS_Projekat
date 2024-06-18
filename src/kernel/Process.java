package kernel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import shell.Shell;

public class Process {
    private int processId;
    private String processName;
    private int totalBurstTime;
    private int remainingBurstTime;
    private int memoryRequirement;
    private ProcessState processState;
    private Path filePath;
    private ArrayList<String> instructions = new ArrayList<>();
    private int[] registerValues = new int[4];
    private int pcValue;

    public Process(int processId, String processName, int totalBurstTime, int memoryRequirement, String program) {
        this.processId = processId;
        this.processName = processName;
        this.totalBurstTime = totalBurstTime;
        this.remainingBurstTime = totalBurstTime;
        this.memoryRequirement = memoryRequirement;
        this.processState = ProcessState.NEW;
        //this.filePath = Paths.get(Shell.tree.getCurrentFolder().getAbsolutePath() + "\\" + program);
        loadInstructions();
    }

    // Getters and setters
    public int getProcessId() { return processId; }
    public String getProcessName() { return processName; }
    public int getTotalBurstTime() { return totalBurstTime; }
    public int getRemainingBurstTime() { return remainingBurstTime; }
    public void setRemainingBurstTime(int remainingBurstTime) { this.remainingBurstTime = remainingBurstTime; }
    public int getMemoryRequirement() { return memoryRequirement; }
    public ProcessState getProcessState() { return processState; }
    public void setProcessState(ProcessState processState) { this.processState = processState; }
    public ArrayList<String> getInstructions() { return instructions; }
    public void setInstructions(ArrayList<String> instructions) { this.instructions = instructions; }
    public Path getFilePath() { return filePath; }

    public int[] getValuesOfRegisters() {
        return registerValues;
    }

    public void setValuesOfRegisters(int[] registerValues) {
        this.registerValues = registerValues;
    }

    public int getPcValue() {
        return pcValue;
    }

    public void setPcValue(int pcValue) {
        this.pcValue = pcValue;
    }

    private void loadInstructions() {
        try {
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                String machineInstruction = Shell.asemblerToMachineInstruction(line);
                if (machineInstruction != null) {
                    instructions.add(machineInstruction);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading program file: " + e.getMessage());
        }
    }

    // Function to convert assembler instructions to machine instructions
    public void convertAssemblerInstructionsToMachineInstructions() {
        ArrayList<String> machineInstructions = new ArrayList<>();
        for (String instruction : instructions) {
            String machineInstruction = Shell.asemblerToMachineInstruction(instruction);
            if (machineInstruction != null) {
                machineInstructions.add(machineInstruction);
            }
        }
        setInstructions(machineInstructions);
    }
}
