package memory;

import java.util.*;

public class BuddyAllocator {
    private final int minBlockSize;
    private final int maxBlockSize;
    private final int totalSize;
    private final TreeMap<Integer, LinkedList<Integer>> freeLists;
    private final int[] memory; // Simulating memory space

    public BuddyAllocator(int minBlockSize, int maxBlockSize, int totalSize) {
        if (Integer.bitCount(minBlockSize) != 1 || Integer.bitCount(maxBlockSize) != 1 || Integer.bitCount(totalSize) != 1) {
            throw new IllegalArgumentException("Block sizes and total size must be power of two.");
        }
        this.minBlockSize = minBlockSize;
        this.maxBlockSize = maxBlockSize;
        this.totalSize = totalSize;
        this.memory = new int[totalSize]; // Initializing memory space
        this.freeLists = new TreeMap<>();
        initializeFreeLists();
    }

    private void initializeFreeLists() {
        int size = minBlockSize;
        while (size <= maxBlockSize) {
            freeLists.put(size, new LinkedList<>());
            size *= 2;
        }
        freeLists.get(maxBlockSize).add(0); // Add the whole memory as a free block initially
    }

    public int allocate(int size) {
        if (size <= 0 || size > maxBlockSize) {
            throw new IllegalArgumentException("Invalid size.");
        }

        int allocSize = minBlockSize;
        while (allocSize < size) {
            allocSize <<= 1;
        }

        while (!freeLists.containsKey(allocSize) || freeLists.get(allocSize).isEmpty()) {
            allocSize <<= 1;
            if (allocSize > maxBlockSize) {
                return -1; // No available block
            }
        }

        int addr = freeLists.get(allocSize).removeFirst();
        while (allocSize > size) {
            allocSize >>= 1;
            freeLists.get(allocSize).add(addr + allocSize);
        }
        return addr;
    }

    public void deallocate(int addr, int size) {
        if (addr < 0 || addr >= totalSize || size <= 0 || size > maxBlockSize || (addr % size != 0)) {
            throw new IllegalArgumentException("Invalid address or size.");
        }

        while (size <= maxBlockSize) {
            LinkedList<Integer> list = freeLists.get(size);
            int buddyAddr = addr ^ size;
            if (!list.remove(Integer.valueOf(buddyAddr))) {
                list.add(addr);
                break;
            }
            addr = Math.min(addr, buddyAddr);
            size <<= 1;
        }
    }

    public int[] getMemory(int startAddress, int size) {
        if (startAddress < 0 || startAddress + size > totalSize) {
            throw new IllegalArgumentException("Invalid start address or size.");
        }
        int[] data = new int[size];
        System.arraycopy(memory, startAddress, data, 0, size);
        return data;
    }

    public void printMemory() {
        System.out.println("Memory Allocation Status:");
        for (Map.Entry<Integer, LinkedList<Integer>> entry : freeLists.entrySet()) {
            System.out.println("Block size: " + entry.getKey() + ", Free blocks: " + entry.getValue().size());
        }
    }
}
