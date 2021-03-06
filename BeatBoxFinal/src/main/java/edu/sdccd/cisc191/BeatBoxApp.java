package edu.sdccd.cisc191;
/** BeatBox Final Project
*   Group B - Summer 2020 - CISC 191 – Intermediate Java Programming
*
*   Group Members
*   Cesar Castillo Alonso
*   William Hammond
*   Paige Hodgkinson
*   Kevin Johnson
*   Thomas Marcoux
*
*
*   Final BeatBox Client Program
*
*   Program taken from
*   Head First Java
*   Second Edition
*   by Kathy Sierra and Bert Bates
*
*   Description of BeatBoxApp.java
*   The purpose of this file is to start the client side of the BeatBox program.
*   The person adds a screen name at the command line to use during the program.
*   The file sets up an ObjectInputStream and an ObjectOutputStream for communication
*   with the server. It also starts a thread that has the job of reading two objects,
*   the message and the ArrayList of Boolean checkbox state values.
*
*   Author(s):
*   @author Kevin Johnson
*/



import java.io.*;
import java.util.*;
import java.net.*;

import javax.swing.*;


public class BeatBoxApp implements BeatBoxConstants {

    private String userName;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private RemoteReader rr;
    private Vector<String> listVector;
    private JList incommingList;
    private HashMap<String, boolean[]> otherSeqsMap;

    // Getters and Setters

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

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


    public static void main(String[] args) {
        BeatBoxApp app = new BeatBoxApp();
        app.startUp(args[0]);    // args[0] is your user ID/screen name
        // Add a command-line argument for your
        // screen name.
        // Example: %java BeatBoxApp theFlash

    }

    public void startUp(String name) {
        userName = name;
        // open connection to the server
        // Set up the networking, I/O, and make (and start) the reader thread.

        // Creating BeatBoxGUI object
        BeatBoxGUI gui = new BeatBoxGUI();

        // Set the userName in BeatBoxGUI
        gui.setUserName(userName);

        // Calling the buildGUI method of BeatBoxGUI
        gui.buildGUI();

        try {
            Socket sock = new Socket("127.0.0.1", 4242);
            out = new ObjectOutputStream(sock.getOutputStream());
            // Set the out in BeatBoxGUI
            gui.setOut(out);
            in = new ObjectInputStream(sock.getInputStream());
            rr = new RemoteReader();
            Thread remote = new Thread(rr);
            // Get the ListVector from BeatBoxGUI
            listVector = gui.getListVector();

            // Set the listVector in RemoteReader
            rr.setListVector(listVector);

            // Get the incommingList from BeatBoxGUI
            incommingList = gui.getIncommingList();

            // Set the incommingList in RemoteReader
            rr.setIncommingList(incommingList);

            // Get the otherSeqsMap from BeatBoxGUI
            otherSeqsMap = gui.getOtherSeqsMap();

            // Set the otherSeqsMap in RemoteReader
            rr.setOtherSeqsMap(otherSeqsMap);

            // Set the in in RemoteReader
            rr.setIn(in);

            remote.start();

        } catch (Exception ex) {
            System.out.println("couldn't connect - you'll have to play alone.");
        }

    } // close startUP
} // close class