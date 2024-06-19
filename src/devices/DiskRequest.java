package devices;

public class DiskRequest {
    private int position;
    private boolean isRead; // True for read, false for write
    private byte[] data; // Data to be written, null for read requests

    public DiskRequest(int position, boolean isRead, byte[] data) {
        this.position = position;
        this.isRead = isRead;
        this.data = data;
    }

    public int getPosition() {
        return position;
    }

    public boolean isRead() {
        return isRead;
    }

    public byte[] getData() {
        return data;
    }
}

