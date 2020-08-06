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
// Description of Instruments.java
// The purpose of this file is to create an enum Type for the
// instruments to be selected to be played (played one at a time).
//
//  Author(s):
//  Kevin Johnson
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

    @Override
    public String toString() {
        return name;
    }
}
