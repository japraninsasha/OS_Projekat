package memory;

import java.util.ArrayList;
import kernel.Process;

public class MemoryPartition {
    private int[] data;
    private int positionInMemory = -1;
    private int size;
    private Process process;
    private static ArrayList<MemoryPartition> allPartitons;

    public MemoryPartition(Process process) {
        this.process = process;
        size = process.getInstructions().size();
        data = new int[size];
        for (int i = 0; i < size; i++) {
            String temp = process.getInstructions().get(i);
            data[i] = Integer.parseInt(temp, 2);
        }
        allPartitons.add(this);
    }

    public MemoryPartition(int[] data) {
        this.data = data;
    }

    public static void initialize() {
        allPartitons = new ArrayList<>();
    }

    public static MemoryPartition getPartitionByAddress(int address) {
        for (MemoryPartition partitionMemory : allPartitons) {
            if (partitionMemory.getPositionInMemory() == address)
                return partitionMemory;
        }
        return null;
    }

    public static MemoryPartition getPartitionByProcess(Process process) {
        for (MemoryPartition partitionMemory : allPartitons) {
            if (partitionMemory.getProcess().equals(process))
                return partitionMemory;
        }
        return null;
    }

    public int getPositionInMemory() {
        return positionInMemory;
    }

    public void setPositionInMemory(int positionInMemory) {
        this.positionInMemory = positionInMemory;
    }

    public int[] getData() {
        return data;
    }

    public int getSize() {
        return size;
    }

    public Process getProcess() {
        return process;
    }
}
