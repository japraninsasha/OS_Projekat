package devices;

public class DiskRequest {
    private int startBlock;
    private boolean isRead; // Assuming this is to distinguish between read and write operations
    private byte[] content;

    public DiskRequest(int startBlock, boolean isRead, byte[] content) {
        this.startBlock = startBlock;
        this.isRead = isRead;
        this.content = content;
    }

    public int getStartBlock() {
        return startBlock;
    }

    public byte[] getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "DiskRequest[startBlock=" + startBlock + ", isRead=" + isRead + "]";
    }
}
