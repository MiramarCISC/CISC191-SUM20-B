package edu.sdccd.cisc191;
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

    String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare",
            "Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo", "Maracas",
            "Whistle", "Low Conga", "Cowbell", "Vibraslap", "Low-mid Tom",
            "High Agogo", "Open Hi Conga"};

    int[] instruments = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};

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
        System.out.println("Starting GUI...");
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

        userMessage = new JTextField();
        buttonBox.add(userMessage);

        incommingList = new JList();
        incommingList.addListSelectionListener(new MyListSelectionListener());
        incommingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane theList = new JScrollPane(incommingList);
        buttonBox.add(theList);
        incommingList.setListData(listVector); // no data to start with

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (int i = 0; i < 16; i++) {
            nameBox.add(new Label(instrumentNames[i]));
        }

        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        theFrame.getContentPane().add(background);
        GridLayout grid = new GridLayout(16, 16);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel);

        for (int i = 0; i < 256; i++) {
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
        ArrayList<Integer> trackList = null; // this will hold the instruments for each
        sequence.deleteTrack(track);
        track = sequence.createTrack();

        // Build a track by walking through the checkboxes to get their state, and mapping
        // that to an instrument (and making the MidiEvent for it).

        for (int i = 0; i < 16; i++) {

            trackList = new ArrayList<Integer>();


            for (int j = 0; j < 16; j++) {
                JCheckBox jc = (JCheckBox) checkboxList.get(j + (16*i));
                if (jc.isSelected()) {
                    int key = instruments[i];
                    trackList.add(new Integer(key));
                } else {
                    trackList.add(null);  // because this slot should be empty in the track
                }
            } // close inner loop
            makeTracks(trackList);

        } // close outer loop

        track.add(makeEvent(192,9,1,0,15)); // - so we always go to full 16 beats
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
            boolean[] checkboxState = new boolean[256];
            for (int i = 0; i < 256; i++) {
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
        for (int i = 0; i < 256; i++) {
            JCheckBox check = (JCheckBox) checkboxList.get(i);
            if (checkboxState[i]) {
                check.setSelected(true);
            } else {
                check.setSelected(false);
            }
        } // close loop
    } // close changeSequence

    public void makeTracks(ArrayList list) {
        Iterator it = list.iterator();
        for (int i = 0; i < 16; i++) {
            Integer num = (Integer) it.next();
            if (num != null) {
                int numKey = num.intValue();
                track.add(makeEvent(144, 9, numKey, 100, i));
                track.add(makeEvent(128, 9, numKey, 100, i + 1));
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