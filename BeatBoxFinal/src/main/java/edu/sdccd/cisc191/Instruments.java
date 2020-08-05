package edu.sdccd.cisc191;
// BeatBox Final Project
// Group B - Summer 2020 - CISC 191 – Intermediate Java Programming
//
// Group Members
// Cesar Castillo Alonso
// William Hammond
// Paige Hodgkinson
// Kevin Johnson
// Thomas Marcoux
//
//
// Final BeatBox Client Program
//
// Program taken from
// Head First Java
// Second Edition
// by Kathy Sierra and Bert Bates
//
//

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
