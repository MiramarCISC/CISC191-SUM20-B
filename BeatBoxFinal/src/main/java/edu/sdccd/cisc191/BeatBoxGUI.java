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
*   Description of BeatBoxGUI.java
*   The purpose of this file is to create the graphical user interface of the
*   client side of the BeatBox program. This is where the user selects the
*   beats a percussion instrument should play on or the notes of a selected
*   instrument the instrument should play on. The user is able to start and
*   stop the beats or the music using the Start and Stop buttons. The person
*   is also able to speed up the tempo or slow down the tempo using the
*   Tempo Up and Temp Down buttons. The person is able to write a message
*   that gets sent with the music pattern, to all the other players connected
*   to the server, by using the SendIt button. The person is able to change
*   the instrument that is playing the notes in the pattern by selecting the
*   instrument from the combo box.
*
*  Author(s):
*  @author Kevin Johnson
*  @author William Hammond
*/

import java.awt.*;
import javax.swing.*;
import java.io.*;
import javax.sound.midi.*;
import java.util.*;
import java.awt.event.*;
import java.net.*;
import java.util.List;
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
    private String userName;
    private ObjectOutputStream out;
    private int selectedInstrument = 1;
    private int[] instruments;
    private Map<String, JCheckBox> checkboxesPercussion = new HashMap<>();
    private Map<String, JCheckBox> checkboxesMIDINotes = new HashMap<>();
    private Map<Percussion, Set<Integer>> percussionBeats = new HashMap<>();
    private Map<MIDINotes, Set<Integer>> instrumentNoteBeats = new HashMap<>();
    private JCheckBox checkboxSelected;

    private final String[] availableInstruments = new String[] {"Acoustic Grand Piano",
            "Acoustic Guitar (steel)", "Overdriven Guitar", "Electric Bass (finger)", "Violin",
            "Trumpet", "Alto Sax"};



    // Creating BeatBoxMIDI object
    BeatBoxMIDI midi = new BeatBoxMIDI();



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

    public Map<Percussion, Set<Integer>> getPercussionBeats() {
        return percussionBeats;
    }

    public void setPercussionBeats(Map<Percussion, Set<Integer>> percussionBeats) {
        this.percussionBeats = percussionBeats;
    }

    public Map<MIDINotes, Set<Integer>> getInstrumentNoteBeats() {
        return instrumentNoteBeats;
    }

    public void setInstrumentNoteBeats(Map<MIDINotes, Set<Integer>> instrumentNoteBeats) {
        this.instrumentNoteBeats = instrumentNoteBeats;
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


    public void buildGUI() {

        theFrame = new JFrame("Cyber MusicBox");
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

        JButton clear = new JButton("Clear");
        clear.addActionListener(new MyClearListener());
        buttonBox.add(clear);

//        JButton save = new JButton("Save");
//        save.addActionListener(new MySaveListener());
//        buttonBox.add(save);

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
//        JComboBox cb = new JComboBox();
//        cb.setModel(new DefaultComboBoxModel(Instruments.values()));

//        ArrayList<String> al = new ArrayList<String>();
//        for (Instruments instrument : Instruments.values()) {
//            al.add(instrument.name);
//        }
//
//        String[] items = new String[al.size()];
//        al.toArray(items);
//        cb.setModel(new DefaultComboBoxModel<String>(items));

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

        // Adding Labels for Percussion Instruments
        for (Percussion percussion : Percussion.values()) {
            nameBox.add(new Label(percussion.name));
        }

        // Adding Labels for MIDINotes
        for (MIDINotes notes : MIDINotes.values()) {
            nameBox.add(new Label(String.valueOf(notes)));
        }


        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        theFrame.getContentPane().add(background);
        GridLayout grid = new GridLayout(TOTALINSTRUMENTS, TOTALBEATS);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel);


        // Adding Checkboxes for Percussion Instruments
        for (Percussion percussion : Percussion.values()) {
            for (int i = 0; i < TOTALBEATS; i++) {
                JCheckBox c = new JCheckBox();
                c.setSelected(false);
                String name = String.valueOf(percussion) + "-" + i;
                c.setName(name);
                c.addItemListener(new MyPercussionCheckboxListener());
                checkboxesPercussion.put(name, c);
                checkboxList.add(c);
                mainPanel.add(c);
            } // end for loop
        } // end enhanced for loop

        // Adding Checkboxes for MIDINotes
        for (MIDINotes notes : MIDINotes.values()) {
            for (int i = 0; i < TOTALBEATS; i++) {
                JCheckBox c = new JCheckBox();
                c.setSelected(false);
                String name = String.valueOf(notes) + "-" + i;
                c.setName(name);
                c.addItemListener(new MyMIDINotesCheckboxListener());
                checkboxesMIDINotes.put(name, c);
                checkboxList.add(c);
                mainPanel.add(c);
            } // end for loop
        } // end enhanced for loop


        //Set the values for checkboxList in BeatBoxMIDI
        midi.setCheckboxList(checkboxList);

        //Set the values for percussionBeats in BeatBoxMIDI
        midi.setPercussionBeats(percussionBeats);

        //Set the values for instrumentNoteBeats in BeatBoxMIDI
        midi.setInstrumentNoteBeats(instrumentNoteBeats);

        theFrame.setBounds(50, 50, 300, 300);
        theFrame.pack();
        theFrame.setVisible(true);

        // Calling the setUpMidi method of BeatBoxMIDI
        midi.setUpMidi();

    } // close buildGUI


    public class MyPercussionCheckboxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            Object source = e.getItem();
            String cbName = ((JCheckBox) e.getItem()).getName();
            String[] tokens = cbName.split("-");
            String percussionStr = tokens[0];
            String beatStr = tokens[1];

            List<Integer> selectedList = new ArrayList<>();

            for (int i = 0; i < TOTALBEATS; i++) {
                String name = percussionStr + "-" + i;
                checkboxSelected = checkboxesPercussion.get(name);
                if (checkboxSelected.isSelected()) {
                    selectedList.add(i);
                }

            }

            Set<Integer> set = new HashSet<Integer>(selectedList);
            for (Percussion percussion : Percussion.values()) {
                String percussionValueStr = String.valueOf(percussion);
                if (percussionValueStr.equals(percussionStr)) {
                    percussionBeats.put(percussion, set);
                }
            }

        } // close itemStateChanged
    } // close inner class

    public class MyMIDINotesCheckboxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            Object source = e.getItem();
            String cbName = ((JCheckBox) e.getItem()).getName();
            String[] tokens = cbName.split("-");
            String MIDINotesStr = tokens[0];
            String beatStr = tokens[1];

            List<Integer> selectedList = new ArrayList<>();

            for (int i = 0; i < TOTALBEATS; i++) {
                String name = MIDINotesStr + "-" + i;
                checkboxSelected = checkboxesMIDINotes.get(name);

                if (checkboxSelected.isSelected()) {
                    selectedList.add(i);
                }
            }

            Set<Integer> set = new HashSet<Integer>(selectedList);
            for (MIDINotes notes : MIDINotes.values()) {
                String notesValueStr = String.valueOf(notes);
                if (notesValueStr.equals(MIDINotesStr)) {
                    instrumentNoteBeats.put(notes, set);
                }
            }

        } // close itemStateChanged
    } // close inner class


    public class MyStartListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            midi.buildTrackAndStart();
        } // close actionPerformed
    } // close inner class

    public class MyStopListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            midi.getSequencer().stop();
        } // close actionPerformed
    } // close inner class

    public class MyClearListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            midi.getSequencer().stop();
            for (int i = 0; i < TOTALCHECKBOXES; i++) {
                JCheckBox check = (JCheckBox) checkboxList.get(i);
                if (check.isSelected()) {
                    check.doClick();
                }
            }
        } // close actionPerformed
    } // close inner class

    public class MyUpTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = midi.getSequencer().getTempoFactor();
            midi.getSequencer().setTempoFactor((float) (tempoFactor * 1.03));
        } // close actionPerformed
    } // close inner class

    public class MyDownTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = midi.getSequencer().getTempoFactor();
            midi.getSequencer().setTempoFactor((float) (tempoFactor * .97));
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
                    midi.changeSequence(selectedState);
                    midi.getSequencer().stop();
                    midi.buildTrackAndStart();
                }

            }
        } // close valueChanged
    } // close inner class


    public class MyInstrumentSelectionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String selectedInstrumString = cb.getSelectedItem().toString();
            //Instruments selectedInstrum = (Instruments)cb.getSelectedItem();
            //int selectedIndex = cb.getSelectedIndex();
            //System.out.println("selectedIndex: " + selectedIndex);

            //Instruments selectedInstrum = (Instruments)cb.getSelectedItem();

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

            // Stopping
            midi.getSequencer().stop();

            //Starting
            midi.buildTrackAndStart();

        } // close actionPerformed
    } // close inner class

    // The MyPlayMineListener is not currently being used.
    // However, it might be used in the future so it was left in.
    public class MyPlayMineListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            if (midi.getMySequence() != null) {
                midi.setSequence(midi.getMySequence()); // restore to my original
            }
        } // close actionPerformed
    } // close inner class


} // close class