package fileSystem;

public class File {
    private String name;
    private int size;
    private int startBlock;

    public File(String name, int size, int startBlock) {
        this.name = name;
        this.size = size;
        this.startBlock = startBlock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public int getStartBlock() {
        return startBlock;
    }
}
