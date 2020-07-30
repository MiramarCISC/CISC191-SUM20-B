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

    private boolean[] checkboxState = null;
    private String nameToShow = null;
    private Object obj = null;
    private ObjectInputStream in;
    private Vector<String> listVector;
    private JList incommingList;
    private HashMap<String, boolean[]> otherSeqsMap;

    public ObjectInputStream getIn() {
        return in;
    }

    public void setIn(ObjectInputStream in) {
        this.in = in;
    }

    public Vector<String> getListVector() {
        return listVector;
    }

    public void setListVector(Vector<String> listVector) {
        this.listVector = listVector;
    }

    public JList getIncommingList() {
        return incommingList;
    }

    public void setIncommingList(JList incommingList) {
        this.incommingList = incommingList;
    }

    public HashMap<String, boolean[]> getOtherSeqsMap() {
        return otherSeqsMap;
    }

    public void setOtherSeqsMap(HashMap<String, boolean[]> otherSeqsMap) {
        this.otherSeqsMap = otherSeqsMap;
    }

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
            while ((obj=in.readObject()) != null) {
                System.out.println("got an object from server");
                System.out.println(obj.getClass());
                String nameToShow = (String) obj;
                checkboxState = (boolean[]) in.readObject();
                otherSeqsMap.put(nameToShow, checkboxState);
                listVector.add(nameToShow);
                incommingList.setListData(listVector);
            } // close while
        } catch(Exception ex) {ex.printStackTrace();}
    } // close run
} // close class