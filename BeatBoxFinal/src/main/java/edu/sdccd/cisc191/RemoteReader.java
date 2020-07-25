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

import java.awt.*;
import javax.swing.*;
import java.io.*;
import javax.sound.midi.*;
import java.util.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.event.*;

public class RemoteReader implements Runnable {

    boolean[] checkboxState = null;
    String nameToShow = null;
    Object obj = null;

    // This is the thread job -- read in data from the server. In this code, 'data' will
    // always be two serialized objects: the String message and the beat pattern (an
    // arrayList of checkbox state values)
    //
    // When a message comes in, we read (deserialize) the two objects (the message and
    // the ArrayList of Boolean checkbox state values) and add it to the JList component.
    // Adding to a JList is a two-step thing: you keep a Vector of the lists data (Vector
    // is an old-fashioned ArrayList), and then tell the JList to use that Vector as it's
    // source for what to display in the list.

    public void run() {
        try {
            while ((obj=app.getIn().readObject()) != null) {
                System.out.println("got an object from server");
                System.out.println(obj.getClass());
                String nameToShow = (String) obj;
                checkboxState = (boolean[]) app.getIn().readObject();
                gui.getOtherSeqsMap().put(nameToShow, checkboxState);
                gui.getListVector().add(nameToShow);
                gui.getIncommingList().setListData(gui.getListVector());
            } // close while
        } catch(Exception ex) {ex.printStackTrace();}
    } // cloae run
} // close class