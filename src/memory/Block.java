package memory;

public class Block {
    private final static int SIZE = 4;
    private byte[] content = new byte[SIZE];
    private final int address;
    private boolean occupied;

    public Block(int address) {
        this.address = address;
        setOccupied(false);
    }

    public int getAddress() {
        return address;
    }

    public static int getSize() {
        return SIZE;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public String toString() {
        return "Block adress: " + address;
    }
}
