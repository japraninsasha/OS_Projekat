package fileSystem;

import java.util.BitSet;

public class DiskManager {
    private BitSet bitVector;
    private int diskSize;
    private int blockSize;

    public DiskManager(int diskSize) {
        this.diskSize = diskSize;
        this.blockSize = 1; // Assume block size is 1 byte
        this.bitVector = new BitSet(diskSize);
    }

    public int allocate(int size) {
        int numBlocks = (int) Math.ceil((double) size / blockSize);
        int startBlock = findFreeBlocks(numBlocks);
        if (startBlock != -1) {
            for (int i = startBlock; i < startBlock + numBlocks; i++) {
                bitVector.set(i);
            }
            System.out.println("Allocated " + numBlocks + " blocks starting at block " + startBlock);
        } else {
            System.out.println("Not enough space to allocate " + numBlocks + " blocks.");
        }
        return startBlock;
    }

    public void deallocate(int startBlock, int size) {
        int numBlocks = (int) Math.ceil((double) size / blockSize);
        for (int i = startBlock; i < startBlock + numBlocks; i++) {
            bitVector.clear(i);
        }
        System.out.println("Deallocated " + numBlocks + " blocks starting at block " + startBlock);
    }

    private int findFreeBlocks(int numBlocks) {
        int freeBlockCount = 0;
        for (int i = 0; i < diskSize; i++) {
            if (!bitVector.get(i)) {
                freeBlockCount++;
                if (freeBlockCount == numBlocks) {
                    return i - numBlocks + 1;
                }
            } else {
                freeBlockCount = 0;
            }
        }
        return -1; // No sufficient free blocks
    }

    public void printBitVector() {
        for (int i = 0; i < diskSize; i++) {
            System.out.print(bitVector.get(i) ? "1" : "0");
        }
        System.out.println();
    }
}
