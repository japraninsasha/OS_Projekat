package memory;

import java.util.ArrayList;

public class SSD {
    private static int size;
    private static Block[] blocks;
    private static int numberOfBlocks;
    public static ArrayList<MemoryFile> files;

    public SSD() {
        initializeMemory(2048);
    }

    private void initializeMemory(int memorySize) {
        size = memorySize;
        numberOfBlocks = size / Block.getSize();
        blocks = new Block[numberOfBlocks];
        for (int i = 0; i < numberOfBlocks; i++) {
            blocks[i] = new Block(i);
        }
        files = new ArrayList<>();
    }

    public void save(MemoryFile file) {
        int requiredBlocks = calculateRequiredBlocks(file.getSize());
        if (numberOfFreeBlocks() >= requiredBlocks) {
            allocateBlocksToFile(file, requiredBlocks);
        } else {
            System.out.println("Not enough space, cannot create file");
        }
    }

    private int calculateRequiredBlocks(int fileSize) {
        int remainder = fileSize % Block.getSize();
        return remainder == 0 ? fileSize / Block.getSize() : (fileSize + Block.getSize() - remainder) / Block.getSize();
    }

    private void allocateBlocksToFile(MemoryFile file, int requiredBlocks) {
        int counter = 0;
        Pointer firstPointer = null;
        for (int i = 0; i < numberOfBlocks; i++) {
            if (!blocks[i].isOccupied()) {
                blocks[i].setOccupied(true);
                blocks[i].setContent(file.part(counter, Block.getSize()));  // Use instance method
                if (counter == 0) {
                    firstPointer = new Pointer(blocks[i]);
                    file.setStart(firstPointer);
                } else {
                    Pointer newPointer = new Pointer(blocks[i]);
                    firstPointer.next = newPointer;
                    firstPointer = newPointer;
                }
                counter++;
                if (counter == requiredBlocks) {
                    file.setLength(counter);
                    files.add(file);
                    return;
                }
            }
        }
    }

    public void deleteFile(MemoryFile file) {
        if (!files.contains(file)) {
            System.out.println("Your file is not in the secondary memory");
        } else {
            freeFileBlocks(file);
            files.remove(file);
        }
    }

    private void freeFileBlocks(MemoryFile file) {
        Pointer pointer = file.getStart();
        while (pointer != null) {
            pointer.block.setOccupied(false);
            pointer.block.setContent(null);
            Pointer temp = pointer;
            pointer = pointer.next;
            temp.next = null;
        }
        file.setStart(null);
    }

    public String readFile(MemoryFile file) {
        StringBuilder content = new StringBuilder();
        Pointer pointer = file.getStart();
        while (pointer != null) {
            for (byte b : pointer.block.getContent()) {
                content.append((char) b);
            }
            pointer = pointer.next;
        }
        return content.toString();
    }

    public void updateFile(MemoryFile file) {
        int requiredBlocks = calculateRequiredBlocks(file.getSize());
        int occupiedBlocks = numberOfBlocksOccupiedByFile(file);

        if (requiredBlocks > occupiedBlocks) {
            if (numberOfFreeBlocks() < requiredBlocks - occupiedBlocks) {
                System.out.println("Not enough space to update file, old version of file will be saved.");
            } else {
                deleteFile(file);
                save(file);
            }
        } else {
            deleteFile(file);
            save(file);
        }
    }

    private int numberOfFreeBlocks() {
        int freeBlocks = 0;
        for (Block block : blocks) {
            if (!block.isOccupied()) {
                freeBlocks++;
            }
        }
        return freeBlocks;
    }

    private int numberOfBlocksOccupiedByFile(MemoryFile file) {
        int count = 0;
        Pointer pointer = file.getStart();
        while (pointer != null) {
            count++;
            pointer = pointer.next;
        }
        return count;
    }

    public boolean contains(String fileName) {
        for (MemoryFile file : files) {
            if (file.getName().equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    public MemoryFile getFile(String fileName) {
        for (MemoryFile file : files) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    public static int getSize() {
        return size;
    }

    public static Block[] getBlocks() {
        return blocks;
    }

    public ArrayList<MemoryFile> getFiles() {
        return files;
    }

    public static void printMemoryAllocationTable() {
        String line = "------------------------------------------------------------------------------------------";
        System.out.println("Memory Allocation Table:");
        System.out.println(line);
        System.out.println("Name of file\t\tStart block\tLength");
        System.out.println(line);
        for (MemoryFile file : files) {
            String fileName = file.getName();
            System.out.println(fileName + (fileName.length() < 16
                    ? ("\t\t" + file.getStart().block.getAdress() + "\t\t" + file.getLength())
                    : ("\t" + file.getStart().block.getAdress() + "\t\t" + file.getLength())));
        }
    }

    protected static class Pointer {
        private Block block;
        private Pointer next;

        private Pointer(Block block) {
            this.block = block;
            this.next = null;
        }

        @Override
        public String toString() {
            return block.toString();
        }
    }
}
