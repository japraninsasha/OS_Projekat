package memory;

import java.util.ArrayList;
import assembler.Operations;
import kernel.Process;
import kernel.ProcessState;

public class MemoryManager {

    private static BuddyAllocator buddyAllocator;
    public static ArrayList<MemoryPartition> partitionsInRam;

    public MemoryManager(int minBlockSize, int maxBlockSize, int totalSize) {
        MemoryPartition.initialize();
        this.buddyAllocator = new BuddyAllocator(minBlockSize, maxBlockSize, totalSize);
        partitionsInRam = new ArrayList<>();
    }

    public int loadProcess(Process process) {
        MemoryPartition partitionMemory = MemoryPartition.getPartitionByProcess(process);
        if (!partitionsInRam.contains(partitionMemory)) { // proverava da li je već korišćen taj proces
            return loadPartition(new MemoryPartition(process));
        } else {
            // Adjust logic here to return position of loaded partition
            MemoryPartition loadedPartition = partitionsInRam.stream()
                    .filter(p -> p.getProcess().getProcessId() == process.getProcessId())
                    .findFirst()
                    .orElse(null);
            if (loadedPartition != null) {
                return loadedPartition.getPositionInMemory();
            } else {
                return -1; // Handle error or return appropriate value
            }
        }
    }

    public int loadPartition(MemoryPartition partition) {
        int position = buddyAllocator.allocate(partition.getSize());
        if (position != -1) {
            partition.setPositionInMemory(position);
            partitionsInRam.add(partition);
            return position;
        } else {
            System.out.println("Not enough memory to load partition: " + partition.getProcess().getProcessId());
            return -1; // Nema dovoljno memorije
        }
    }

    public int[] readProcess(Process process) {
        return readPartition(MemoryPartition.getPartitionByProcess(process));
    }

    public int[] readPartition(MemoryPartition partition) {
        if (partitionsInRam.contains(partition)) {
            return buddyAllocator.getMemory(partition.getPositionInMemory(), partition.getSize());
        }
        return null;
    }

    public static boolean removeProcess(Process process) {
        return removePartition(MemoryPartition.getPartitionByProcess(process));
    }

    public static boolean removePartition(MemoryPartition partition) {
        if (partitionsInRam.contains(partition)) {
            buddyAllocator.deallocate(partition.getPositionInMemory(), partition.getSize());
            partition.setPositionInMemory(-1);
            partitionsInRam.remove(partition);
            System.out.println("Partition for process " + partition.getProcess().getProcessId() + " removed from memory.");
            return true;
        }
        return false;
    }


    public static int memoryOccupiedByProcess(Process process) {
        for (MemoryPartition partition : partitionsInRam) {
            if (partition.getProcess().getProcessId() == process.getProcessId()) {
                return partition.getSize();
            }
        }
        return 0;
    }

    public static void printMemory() {
        buddyAllocator.printMemory(); // Assuming BuddyAllocator has this method
        Operations.printRegisters();
        SSD.printMemoryAllocationTable();
    }

    public static ArrayList<MemoryPartition> getPartitionsInRam() {
        return partitionsInRam;
    }
}
