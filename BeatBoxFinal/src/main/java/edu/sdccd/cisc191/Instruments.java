package edu.sdccd.cisc191;

public enum Instruments {
    PIANO("Acoustic Grand Piano"),
    ACOUSTIC("Acoustic Guitar (steel)"),
    ELECTRIC("Overdriven Guitar"),
    BASS("Electric Bass (finger)"),
    VIOLIN("Violin"),
    TRUMPET("Trumpet"),
    SAX("Alto Sax");

    public final String name;
    Instruments(String name) {
        this.name = name;
    }
}
