package ml.guru.barcodescanner;

public class Code {


    public Long _id; // for cupboard
    public String name; // bunny name
    public String type;


    public Code() {
        this.name = "noName";
        this.type = "noType";
    }

    public Code(String name, String type) {
        this.name = name;
        this.type = type;
    }

}
