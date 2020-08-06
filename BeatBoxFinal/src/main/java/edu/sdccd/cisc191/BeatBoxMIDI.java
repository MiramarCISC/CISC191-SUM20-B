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
// Description of BeatBoxMIDI.java
// The purpose of this file is to handle the Musical Instrument Digital
// Interface or MIDI. It creates the instructions for a MIDI reading
// instrument to play back. The synthesizer (a software synthesizer) provided
// with Java is used to create the sound. It takes the information provided
// by the check boxes and turns it into a tune.
//
//  Author(s):
//  Kevin Johnson
//
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

public class BeatBoxMIDI implements BeatBoxConstants {

    private Sequencer sequencer;
    private Sequence sequence;
    private Sequence mySequence = null;
    private Track track;

    private int selectedInstrument = 1;
    private ArrayList<JCheckBox> checkboxList;

    private int[] instruments = {PERCUSSIONNUMBER, PERCUSSIONNUMBER, PERCUSSIONNUMBER, PERCUSSIONNUMBER, PERCUSSIONNUMBER,
            PERCUSSIONNUMBER, PERCUSSIONNUMBER, PERCUSSIONNUMBER, PERCUSSIONNUMBER, PERCUSSIONNUMBER,
            PERCUSSIONNUMBER,PERCUSSIONNUMBER, PERCUSSIONNUMBER, PERCUSSIONNUMBER, PERCUSSIONNUMBER,
            PERCUSSIONNUMBER, selectedInstrument, selectedInstrument, selectedInstrument, selectedInstrument,
            selectedInstrument, selectedInstrument, selectedInstrument, selectedInstrument, selectedInstrument,
            selectedInstrument, selectedInstrument, selectedInstrument, selectedInstrument, selectedInstrument,
            selectedInstrument, selectedInstrument, selectedInstrument, selectedInstrument, selectedInstrument,
            selectedInstrument, selectedInstrument, selectedInstrument, selectedInstrument, selectedInstrument,
            selectedInstrument};

    private int[] instrumentsNotes = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63,
            72, 71, 70, 69, 68, 67, 66, 65, 64, 63, 62, 61, 60,
            59, 58, 57, 56, 55, 54, 53, 52, 51, 50, 49, 48};


    // Getters and Setters

    public Sequencer getSequencer() {
        return sequencer;
    }

    public void setSequencer(Sequencer sequencer) {
        this.sequencer = sequencer;
    }

    public Sequence getSequence() {
        return sequence;
    }

    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
    }

    public Sequence getMySequence() {
        return mySequence;
    }

    public void setMySequence(Sequence mySequence) {
        this.mySequence = mySequence;
    }

    public int getSelectedInstrument() {
        return selectedInstrument;
    }

    public void setSelectedInstrument(int selectedInstrument) {
        this.selectedInstrument = selectedInstrument;
    }

    public int[] getInstruments() {
        return instruments;
    }

    public void setInstruments(int[] instruments) {
        this.instruments = instruments;
    }

    public ArrayList<JCheckBox> getCheckboxList() {
        return checkboxList;
    }

    public void setCheckboxList(ArrayList<JCheckBox> checkboxList) {
        this.checkboxList = checkboxList;
    }


    public void setUpMidi() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        } catch(Exception e) {e.printStackTrace();}
    } // close setUpMidi


    public void buildTrackAndStart() {
        // Initialize the 3-D ArrayList

        ArrayList<ArrayList<ArrayList<Integer>>> trackList = new ArrayList<>(TOTALINSTRUMENTS);

        // Initialize each element of ArrayList with ArrayList<ArrayList<Integer>>

        for(int i = 0; i < TOTALINSTRUMENTS; i++) {
            trackList.add(new ArrayList<ArrayList<Integer>>(TOTALBEATS));
            for (int j = 0; j < TOTALBEATS; j++) {
                trackList.get(i).add(new ArrayList<Integer>(TOTALLISTELEMENTS));
            }
        }

        sequence.deleteTrack(track);
        track = sequence.createTrack();

        // Build a track by walking through the checkboxes to get their state, and mapping
        // that to an instrument (and making the MidiEvent for it).


        for (int i = 0; i < TOTALINSTRUMENTS; i++) {

            for (int j = 0; j < TOTALBEATS; j++) {
                JCheckBox jc = (JCheckBox) checkboxList.get(j + (TOTALBEATS*i));
                if (jc.isSelected()) {
                    int key = instruments[i];
                    int keyNotes = instrumentsNotes[i];
                    trackList.get(i).get(j).add(new Integer(key));
                    trackList.get(i).get(j).add(new Integer(keyNotes));
                } else {
                    trackList.get(i).get(j).add(null); // because this slot should be empty in the track
                    trackList.get(i).get(j).add(null);
                }
            } // close inner loop
            makeTracks(trackList, i);

        } // close outer loop

        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        } catch(Exception e) {e.printStackTrace();}
    } // close method


    // This method is called when the user selects something from the list. We
    // IMMEDIATELY change the pattern to the one they selected.
    //
    public void changeSequence(boolean[] checkboxState) {
        for (int i = 0; i < TOTALCHECKBOXES; i++) {
            JCheckBox check = (JCheckBox) checkboxList.get(i);
            if (checkboxState[i]) {
                check.setSelected(true);
            } else {
                check.setSelected(false);
            }
        } // close loop
    } // close changeSequence

    public void makeTracks(ArrayList<ArrayList<ArrayList<Integer>>> list, int currentInstrum) {

        for (int i = 0; i < TOTALBEATS; i++) {

            Integer num1 = (Integer) list.get(currentInstrum).get(i).get(0);
            Integer num2 = (Integer) list.get(currentInstrum).get(i).get(1);

            if (num1 != null && num2 != null ) {

                int numKey = num1.intValue();
                int numNote = num2.intValue();
                if (numKey == PERCUSSIONNUMBER) {
                    track.add(makeEvent(144, PERCUSSIONNUMBER, numNote, 100, i));
                    track.add(makeEvent(128, PERCUSSIONNUMBER, numNote, 100, i + 1));
                } else {
                    track.add(makeEvent(192,1,numKey,0,TOTALBEATS - 1)); // - so we always go to full 16 beats
                    track.add(makeEvent(144, PIANOCHANNEL, numNote, 100, i));
                    track.add(makeEvent(128, PIANOCHANNEL, numNote, 100, i + 1));
                }
            }
        } // close loop
    } // close makeTracks

    public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, one, two);
            event = new MidiEvent(a, tick);
        } catch (Exception e) { }
        return event;
    } // close makeEvent



} //close class
