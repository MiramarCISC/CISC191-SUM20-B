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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;

public class BeatBoxFinal {

    JFrame theFrame;
    JPanel mainPanel;
    JList incommingList;
    JTextField userMessage;
    ArrayList<JCheckBox> checkboxList;
    int nextNum;
    Vector<String> listVector = new Vector<String>();
    String userName;
    ObjectOutputStream out;
    ObjectInputStream in;
    HashMap<String, boolean[]> otherSeqsMap = new HashMap<String, boolean[]>();

    Sequencer sequencer;
    Sequence sequence;
    Sequence mySequence = null;
    Track track;

    final int totalBeats = 16;
    final int totalInstruments = 41;
    final int totalCheckBoxes = totalBeats * totalInstruments;
    final int totalListElements = 2;
    final int percussionNumber = 9;
    final int pianoChannel = 1;
    final String[] availableInstruments = new String[] {"Acoustic Grand Piano",
            "Acoustic Guitar (steel)", "Overdriven Guitar", "Electric Bass (finger)", "Violin",
            "Trumpet", "Alto Sax"};
    int selectedInstrument = 1;

    String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare",
            "Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo", "Maracas",
            "Whistle", "Low Conga", "Cowbell", "Vibraslap", "Low-mid Tom",
            "High Agogo", "Open Hi Conga", "C above Middle C", "B above Middle C",
            "A# above Middle C", "A above Middle C", "G# above Middle C",
            "G above Middle C", "F# above Middle C", "F above Middle C",
            "E above Middle C", "D# above Middle C", "D above Middle C",
            "C# above Middle C", "Middle C", "B below Middle C",
            "A# below Middle C", "A below Middle C", "G# below Middle C",
            "G below Middle C", "F# below Middle C", "F below Middle C",
            "E below Middle C", "D# below Middle C", "D below Middle C",
            "C# below Middle C", "C below Middle C"};

    int[] instruments = {percussionNumber, percussionNumber, percussionNumber, percussionNumber, percussionNumber,
            percussionNumber, percussionNumber, percussionNumber, percussionNumber, percussionNumber,
            percussionNumber,percussionNumber, percussionNumber, percussionNumber, percussionNumber,
            percussionNumber, selectedInstrument, selectedInstrument, selectedInstrument, selectedInstrument,
            selectedInstrument, selectedInstrument, selectedInstrument, selectedInstrument, selectedInstrument,
            selectedInstrument, selectedInstrument, selectedInstrument, selectedInstrument, selectedInstrument,
            selectedInstrument, selectedInstrument, selectedInstrument, selectedInstrument, selectedInstrument,
            selectedInstrument, selectedInstrument, selectedInstrument, selectedInstrument, selectedInstrument,
            selectedInstrument};

    int[] instrumentsNotes = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63,
            72, 71, 70, 69, 68, 67, 66, 65, 64, 63, 62, 61, 60,
            59, 58, 57, 56, 55, 54, 53, 52, 51, 50, 49, 48};

    public static void main(String[] args) {
        new BeatBoxFinal().startUp(args[0]);    // args[0] is your user ID/screen name
        // Add a command-line argument for your
        // screen name.
        // Example: %java BeatBoxFinal theFlash
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
        setUpMidi();
        buildGUI();
    } // close startUP

    public void buildGUI() {

        theFrame = new JFrame("Cyber BeatBox");
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        checkboxList = new ArrayList<JCheckBox>();

        Box buttonBox = new Box(BoxLayout.Y_AXIS);
        JButton start = new JButton("Start");
        start.addActionListener(new MyStartListener());
        buttonBox.add(start);

        JButton stop = new JButton("Stop");
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);

        JButton upTempo = new JButton("Tempo Up");
        upTempo.addActionListener(new MyUpTempoListener());
        buttonBox.add(upTempo);

        JButton downTempo = new JButton("Tempo Down");
        downTempo.addActionListener(new MyDownTempoListener());
        buttonBox.add(downTempo);

        JButton sendIt = new JButton("sendIt");
        sendIt.addActionListener(new MySendListener());
        buttonBox.add(sendIt);


        // Create combobox
        JLabel instrumJLabel;
        JComboBox cb;
        cb = new JComboBox<String>(availableInstruments);
        cb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedInstrumString = cb.getSelectedItem().toString();

                switch(selectedInstrumString)
                {
                    case "Acoustic Grand Piano":
                        selectedInstrument = 1;
                        break;
                    case "Acoustic Guitar (steel)":
                        selectedInstrument = 26;
                        break;
                    case "Overdriven Guitar":
                        selectedInstrument = 30;
                        break;
                    case "Electric Bass (finger)":
                        selectedInstrument = 34;
                        break;
                    case "Violin":
                        selectedInstrument = 41;
                        break;
                    case "Trumpet":
                        selectedInstrument = 57;
                        break;
                    case "Alto Sax":
                        selectedInstrument = 66;
                        break;
                    default:
                        selectedInstrument = 1;
                }

                // Updating the instruments array for the new value of selectedInstrument

                for (int i = 16; i < instruments.length; i++) {
                    instruments[i] = selectedInstrument;
                }

                // Stopping
                sequencer.stop();

                //Starting
                buildTrackAndStart();

            }
        });

        buttonBox.add(cb);
        instrumJLabel = new JLabel("Select Instrument");


        userMessage = new JTextField();
        buttonBox.add(userMessage);

        incommingList = new JList();
        incommingList.addListSelectionListener(new MyListSelectionListener());
        incommingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane theList = new JScrollPane(incommingList);
        buttonBox.add(theList);
        incommingList.setListData(listVector); // no data to start with

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (int i = 0; i < totalInstruments; i++) {
            nameBox.add(new Label(instrumentNames[i]));
        }

        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        theFrame.getContentPane().add(background);
        GridLayout grid = new GridLayout(totalInstruments, totalBeats);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel);

        for (int i = 0; i < totalCheckBoxes; i++) {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkboxList.add(c);
            mainPanel.add(c);
        } // end loop

        theFrame.setBounds(50, 50, 300, 300);
        theFrame.pack();
        theFrame.setVisible(true);


    } // close buildGUI

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

        ArrayList<ArrayList<ArrayList<Integer>>> trackList = new ArrayList<>(totalInstruments);

        // Initialize each element of ArrayList with ArrayList<ArrayList<Integer>>

        for(int i = 0; i < totalInstruments; i++) {
            trackList.add(new ArrayList<ArrayList<Integer>>(totalBeats));
            for (int j = 0; j < totalBeats; j++) {
                trackList.get(i).add(new ArrayList<Integer>(totalListElements));
            }
        }

        sequence.deleteTrack(track);
        track = sequence.createTrack();

        // Build a track by walking through the checkboxes to get their state, and mapping
        // that to an instrument (and making the MidiEvent for it).


        for (int i = 0; i < totalInstruments; i++) {

            for (int j = 0; j < totalBeats; j++) {
                JCheckBox jc = (JCheckBox) checkboxList.get(j + (totalBeats*i));
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

    public class MyStartListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            buildTrackAndStart();
        } // close actionPerformed
    } // close inner class

    public class MyStopListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            sequencer.stop();
        } // close actionPerformed
    } // close inner class

    public class MyUpTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * 1.03));
        } // close actionPerformed
    } // close inner class

    public class MyDownTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * .97));
        } // close actionPerformed
    } // close inner class

    public class MySendListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            // make an arraylist of just the STATE of the checkboxes
            boolean[] checkboxState = new boolean[totalCheckBoxes];
            for (int i = 0; i < totalCheckBoxes; i++) {
                JCheckBox check = (JCheckBox) checkboxList.get(i);
                if (check.isSelected()) {
                    checkboxState[i] = true;
                }
            } // close loop
            String messageToSend = null;
            try {
                out.writeObject(userName + nextNum++ + ": " + userMessage.getText());
                out.writeObject(checkboxState);
            } catch (Exception ex) {
                System.out.println("Sorry dude. Could not send it to the server.");
            }
            userMessage.setText("");
        } // close actionPerformed
    } // close inner class

    public class MyListSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent le) {
            if (!le.getValueIsAdjusting()) {
                String selected = (String) incommingList.getSelectedValue();
                if (selected != null) {
                    // now go to the map, and change the sequence
                    boolean[] selectedState = (boolean[]) otherSeqsMap.get(selected);
                    changeSequence(selectedState);
                    sequencer.stop();
                    buildTrackAndStart();
                }

            }
        } // close valueChanged
    } // close inner class



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
        } // cloae run
    } // close inner class

    public class MyPlayMineListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            if (mySequence != null) {
                sequence = mySequence; // restore to my original
            }
        } // close actionPerformed
    } // close inner class

    // This method is called when the user selects something from the list. We
    // IMMEDIATELY change the pattern to the one they selected.
    //
    public void changeSequence(boolean[] checkboxState) {
        for (int i = 0; i < totalCheckBoxes; i++) {
            JCheckBox check = (JCheckBox) checkboxList.get(i);
            if (checkboxState[i]) {
                check.setSelected(true);
            } else {
                check.setSelected(false);
            }
        } // close loop
    } // close changeSequence

    public void makeTracks(ArrayList<ArrayList<ArrayList<Integer>>> list, int currentInstrum) {

        for (int i = 0; i < totalBeats; i++) {

            Integer num1 = (Integer) list.get(currentInstrum).get(i).get(0);
            Integer num2 = (Integer) list.get(currentInstrum).get(i).get(1);

            if (num1 != null && num2 != null ) {

                int numKey = num1.intValue();
                int numNote = num2.intValue();
                if (numKey == percussionNumber) {
                    track.add(makeEvent(144, percussionNumber, numNote, 100, i));
                    track.add(makeEvent(128, percussionNumber, numNote, 100, i + 1));
                } else {
                    track.add(makeEvent(192,1,numKey,0,totalBeats - 1)); // - so we always go to full 16 beats
                    track.add(makeEvent(144, pianoChannel, numNote, 100, i));
                    track.add(makeEvent(128, pianoChannel, numNote, 100, i + 1));
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

} // close class