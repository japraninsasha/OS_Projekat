package memory;
import memory.SSD.Pointer;

public class MemoryFile {
    private String name;
    private int size;
    private Pointer start;
    private int length;
    private byte[] contentFile;

    public MemoryFile(String name, byte[] content) {
        this.name = name;
        this.contentFile = content;
        this.size = contentFile.length;
    }

    public byte[] part(int index, int blockSize) {
        byte[] part = new byte[blockSize];
        int start = index * blockSize;

        for (int i = 0; i < blockSize; i++) {
            if (start + i < contentFile.length) {
                part[i] = contentFile[start + i];
            } else {
                part[i] = 0; // Padding with zero bytes instead of spaces
            }
        }
        return part;
    }

    public Pointer getStart() {
        return start;
    }

    public void setStart(Pointer start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public byte[] getContentFile() {
        return contentFile;
    }

    public void setContentFile(byte[] contentFile) {
        this.contentFile = contentFile;
        this.size = contentFile.length;
    }
}
