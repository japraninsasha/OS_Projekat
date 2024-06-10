package memory;

import java.util.Arrays;

public class Ram {

    private static final int CAPACITY = 128;
    private static final int EMPTY_CELL = -1;
    private static int[] ram = new int[CAPACITY];
    private static int occupied = 0;

    public static void initialize() {
        // Inicijaliziraj sve ćelije na EMPTY_CELL
        Arrays.fill(ram, EMPTY_CELL);
    }

    public static boolean setAt(int index, int value) {
        if (isOccupied(index))
            return false;
        ram[index] = value;
        occupied++;
        return true;
    }

    public static boolean removeSequence(int start, int size) {
        if (size + start > CAPACITY)
            return false;

        long nonEmptyCount = Arrays.stream(ram, start, start + size).filter(val -> val != EMPTY_CELL).count();

        if (nonEmptyCount == 0) {
            Arrays.fill(ram, start, start + size, EMPTY_CELL);
            occupied -= size;
            return true;
        }

        return false;
    }

    public static boolean setSequence(int start, int[] data) {
        if (start + data.length > CAPACITY)
            return false;
        for (int i = start; i < data.length + start; i++) {
            if (!isOccupied(i)) {
                setAt(i, data[i - start]);
            } else
                return false;
        }
        return true;
    }

    public static int getAt(int i) {
        return ram[i];
    }

    public static boolean removeAt(int i) {
        if (isOccupied(i)) {
            ram[i] = EMPTY_CELL;
            occupied--;
            return true;
        }
        return false;
    }

    public static void printRAM() {
        if (occupied == 0)
            System.out.println("RAM memory isn't occupied");
        else {
            System.out.println("RAM memory:");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < CAPACITY; i++) {
                sb.append(ram[i]).append("\t");
                if ((i + 1) % 10 == 0) {
                    sb.append("\n");
                }
            }
            System.out.println(sb.toString());
        }
    }

    public static boolean isOccupied(int i) {
        return ram[i] != EMPTY_CELL;
    }

    public static int getOccupiedSpace() {
        return occupied;
    }

    public static int getFreeSpace() {
        return CAPACITY - occupied;
    }

    public static int getCapacity() {
        return CAPACITY;
    }
}

