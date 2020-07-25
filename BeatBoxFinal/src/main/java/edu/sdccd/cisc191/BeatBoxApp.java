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



import java.io.*;
import java.util.*;
import java.net.*;


public class BeatBoxApp implements BeatBoxConstants {

    private String userName;
    private ObjectOutputStream out;
    private ObjectInputStream in;

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

    public void setOut(ObjectInputStream in) {
        this.in = in;
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
        try {
            Socket sock = new Socket("127.0.0.1", 4242);
            out = new ObjectOutputStream(sock.getOutputStream());
            in = new ObjectInputStream(sock.getInputStream());
            Thread remote = new Thread(new RemoteReader());
            remote.start();
        } catch (Exception ex) {
            System.out.println("couldn't connect - you'll have to play alone.");
        }

        // Creating BeatBoxMIDI object
        BeatBoxMIDI midi = new BeatBoxMIDI();
        midi.setUpMidi();

        // Creating BeatBoxGUI object
        BeatBoxGUI gui = new BeatBoxGUI();
        gui.buildGUI();

    } // close startUP
} // close class