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

public class BeatBoxGUI implements BeatBoxConstants {

    private JFrame theFrame;
    private JPanel mainPanel;
    private JList incommingList;
    private JTextField userMessage;
    private ArrayList<JCheckBox> checkboxList;
    private static JComboBox cb;
    private int nextNum;
    private Vector<String> listVector = new Vector<String>();
    private HashMap<String, boolean[]> otherSeqsMap = new HashMap<String, boolean[]>();


    private final String[] availableInstruments = new String[] {"Acoustic Grand Piano",
            "Acoustic Guitar (steel)", "Overdriven Guitar", "Electric Bass (finger)", "Violin",
            "Trumpet", "Alto Sax"};


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


    // Getters and Setters

    public JList getIncommingList() {
        return incommingList;
    }

    public void setIncommingList(JList incommingList) {
        this.incommingList = incommingList;
    }

    public ArrayList<JCheckBox> getCheckboxList() {
        return checkboxList;
    }

    public void setCheckboxList(ArrayList<JCheckBox> checkboxList) {
        this.checkboxList = checkboxList;
    }

    public Vector<String> getListVector() {
        return listVector;
    }

    public void setListVector(Vector<String> listVector) {
        this.listVector = listVector;
    }

    public HashMap<String, boolean[]> getOtherSeqsMap() {
        return otherSeqsMap;
    }

    public void setOtherSeqsMap(HashMap<String, boolean[]> otherSeqsMap) {
        this.otherSeqsMap = otherSeqsMap;
    }



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
        //JComboBox cb; initialized in class to be accessable
        cb = new JComboBox<String>(availableInstruments);
        cb.addActionListener(new MyInstrumentSelectionListener());
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
        for (int i = 0; i < TOTALINSTRUMENTS; i++) {
            nameBox.add(new Label(instrumentNames[i]));
        }

        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        theFrame.getContentPane().add(background);
        GridLayout grid = new GridLayout(TOTALINSTRUMENTS, TOTALBEATS);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel);

        for (int i = 0; i < TOTALCHECKBOXES; i++) {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkboxList.add(c);
            mainPanel.add(c);
        } // end loop

        theFrame.setBounds(50, 50, 300, 300);
        theFrame.pack();
        theFrame.setVisible(true);


    } // close buildGUI


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
            boolean[] checkboxState = new boolean[TOTALCHECKBOXES];
            for (int i = 0; i < TOTALCHECKBOXES; i++) {
                JCheckBox check = (JCheckBox) checkboxList.get(i);
                if (check.isSelected()) {
                    checkboxState[i] = true;
                }
            } // close loop
            String messageToSend = null;
            try {
                app.getOut().writeObject(app.getUserName() + nextNum++ + ": " + userMessage.getText());
                app.getOut().writeObject(checkboxState);
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


    public class MyInstrumentSelectionListener implements ActionListener {
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
            midi.setSelectedInstrument(selectedInstrument);
            instruments = midi.getInstruments();
            for (int i = 16; i < instruments.length; i++) {
                instruments[i] = selectedInstrument;
            }

            // Stopping
            sequencer.stop();

            //Starting
            buildTrackAndStart();

        } // close actionPerformed
    } // close inner class

    // The MyPlayMineListener is not currently being used.
    // However, it might be used in the future so it was left in.
    public class MyPlayMineListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            if (mySequence != null) {
                sequence = mySequence; // restore to my original
            }
        } // close actionPerformed
    } // close inner class

} // close class