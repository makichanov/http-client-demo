package model;

public class PackedResource {
    private String name;
    private byte[] data;

    public PackedResource(String name, byte[] data) {
        this.name = name;
        this.data = data;
    }

    public PackedResource() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
