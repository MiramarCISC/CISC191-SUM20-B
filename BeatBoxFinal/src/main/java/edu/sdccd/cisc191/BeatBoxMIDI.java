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

public class BeatBoxMIDI implements BeatBoxConstants {

    private Sequencer sequencer;
    private Sequence sequence;
    private Sequence mySequence = null;
    private Track track;

    private int selectedInstrument = 1;
    private ArrayList<JCheckBox> checkboxList;
    private Map<Percussion, Set<Integer>> percussionBeats = new HashMap<>();
    private Map<MIDINotes, Set<Integer>> instrumentNoteBeats = new HashMap<>();
    private boolean isPercussion = false;
    private boolean keyInMap = false;


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
        ArrayList<Integer> trackList = null;    // This will hold the percussion instrument or selected
                                                // instrument note for each beat
        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for (Percussion percussion : Percussion.values()) {
            trackList = new ArrayList<Integer>();
            if (percussionBeats.containsKey(percussion)) {
                keyInMap = true;
            } else {
                keyInMap = false;
                for (int i = 0; i < TOTALBEATS; i++) {
                        trackList.add(null);
                }
            }
            if (keyInMap) {
                Set<Integer> setOfBeats = percussionBeats.get(percussion);
                for (int i = 0; i < TOTALBEATS; i++) {
                    boolean isEqualtoBeat = false;
                    if (setOfBeats != null || !(setOfBeats.isEmpty())) {
                        for (Integer value : setOfBeats) {
                            if (value == i) {
                                isEqualtoBeat = true;
                                break;
                            }
                        }
                    }
                    if (isEqualtoBeat) {
                        int key = percussion.drumKey;
                        trackList.add(new Integer(key));

                    } else {
                        trackList.add(null);
                    }
                }
            }
            isPercussion = true;
            makeTracks(trackList, isPercussion);
        }

        for (MIDINotes notes : MIDINotes.values()) {
            trackList = new ArrayList<Integer>();
            if (instrumentNoteBeats.containsKey(notes)) {
                keyInMap = true;
            } else {
                keyInMap = false;
                for (int i = 0; i < TOTALBEATS; i++) {
                    trackList.add(null);
                }
            }
            if (keyInMap) {
                Set<Integer> setOfBeats = instrumentNoteBeats.get(notes);
                for (int i = 0; i < TOTALBEATS; i++) {
                    boolean isEqualtoBeat = false;
                    if (setOfBeats != null || !(setOfBeats.isEmpty())) {
                        for (Integer value : setOfBeats) {
                            if (value == i) {
                                isEqualtoBeat = true;
                                break;
                            }
                        }
                    }
                    if (isEqualtoBeat) {
                        int key = notes.midiNote;
                        trackList.add(new Integer(key));

                    } else {
                        trackList.add(null);
                    }
                }
            }
            isPercussion = false;
            makeTracks(trackList, isPercussion);
        }

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
    public void changeSequence(boolean[] checkboxState, Map<Percussion, Set<Integer>> percussionBeats, Map<MIDINotes, Set<Integer>> instrumentNoteBeats, int selectedInstrument) {
        for (int i = 0; i < TOTALCHECKBOXES; i++) {
            JCheckBox check = (JCheckBox) checkboxList.get(i);
            if (checkboxState[i]) {
                check.setSelected(true);
            } else {
                check.setSelected(false);
            }
        } // close loop

        this.percussionBeats = percussionBeats;
        this.instrumentNoteBeats = instrumentNoteBeats;
        this.selectedInstrument = selectedInstrument;

    } // close changeSequence

    public void makeTracks(ArrayList<Integer> list, boolean isPercussion) {
        //System.out.println("list: " + list.toString());
        Iterator it = list.iterator();
        for (int i = 0; i < TOTALBEATS; i++) {
            Integer num = (Integer) it.next();
            if (num != null) {
                int numKey = num.intValue();
                if (isPercussion) {
                    track.add(makeEvent(144, PERCUSSIONNUMBER, numKey, 100, i));
                    track.add(makeEvent(128, PERCUSSIONNUMBER, numKey, 100, i + 1));
                    System.out.println("numKey: " + numKey);
                } else {
                    track.add(makeEvent(192,1,selectedInstrument,0,TOTALBEATS - 1)); // - so we always go to full 16 beats
                    track.add(makeEvent(144, PIANOCHANNEL, numKey, 100, i));
                    track.add(makeEvent(128, PIANOCHANNEL, numKey, 100, i + 1));
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
