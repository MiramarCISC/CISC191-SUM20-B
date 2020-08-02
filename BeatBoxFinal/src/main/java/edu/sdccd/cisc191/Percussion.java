package edu.sdccd.cisc191;

public enum Percussion {
    BASS("Bass Drum", 35),
    CLOSEDHIHAT("Closed Hi-Hat", 42),
    OPENHIHAT("Open Hi-Hat", 46),
    SNARE("Acoustic Snare", 38),
    CRASH("Crash Cymbal", 49),
    CLAP("Hand Clap", 39),
    HIGHTOM("High Tom", 50),
    BONGO("Hi Bongo", 60),
    MARACAS("Maracas", 70),
    WHISTLE("Whistle", 72),
    CONGA("Low Conga", 64),
    COWBELL("Cowbell", 56),
    VIBRASLAP("Vibraslap", 58),
    MIDTOM("Low-mid Tom", 47),
    AGOGO("High Agogo", 67),
    HICONGA("Open Hi Conga",63);
    public final String name;
    public final int drumKey;
    Percussion(String name, int drumKey) {
        this.name = name;
        this.drumKey = drumKey;
    }
}
