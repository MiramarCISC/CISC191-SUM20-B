package edu.sdccd.cisc191;

public enum MIDINotes {
    C5(72),
    B4(71),
    AS4(70),
    A4(69),
    GS4(68),
    G4(67),
    FS4(66),
    F4(65),
    E4(64),
    DS4(63),
    D4(62),
    CS4(61),
    C4(60),
    B3(59),
    A3S(58),
    A3(57),
    GS3(56),
    G3(55),
    FS3(54),
    F3(53),
    E3(52),
    DS3(51),
    D3(50),
    CS3(49),
    C3(48);
    public final int midiNote;
    MIDINotes(int midiNote) {
        this.midiNote = midiNote;
    }
}
