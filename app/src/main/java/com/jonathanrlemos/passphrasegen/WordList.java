package com.jonathanrlemos.passphrasegen;

import android.content.Context;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WordList implements Serializable {
    private static final long serialVersionUID = -377505250272605531L;
    private ArrayList<String> adjectiveList;
    private ArrayList<String> nounList;
    private ArrayList<String> adverbList;
    private ArrayList<String> verbList;
    private int adjectiveCounter = 0;
    private int nounCounter = 0;
    private int adverbCounter = 0;
    private int verbCounter = 0;

    public static final String ADJECTIVE_LIST_FILENAME = "adj.txt";
    public static final String NOUN_LIST_FILENAME      = "noun.txt";
    public static final String ADVERB_LIST_FILENAME    = "adv.txt";
    public static final String VERB_LIST_FILENAME      = "verb.txt";

    public WordList() {}

    public WordList(ArrayList<String> adjectiveList, ArrayList<String> nounList, ArrayList<String> adverbList, ArrayList<String> verbList){
        this();
        setAdjectiveList(adjectiveList);
        setNounList(nounList);
        setAdverbList(adverbList);
        setVerbList(verbList);
    }

    public WordList setAdjectiveList(ArrayList<String> list){
        adjectiveList = list;
        return this;
    }
    public WordList setNounList(ArrayList<String> list){
        nounList = list;
        return this;
    }
    public WordList setAdverbList(ArrayList<String> list){
        adverbList = list;
        return this;
    }
    public WordList setVerbList(ArrayList<String> list){
        verbList = list;
        return this;
    }
    public ArrayList<String> getAdjectiveList(){
        return adjectiveList;
    }
    public ArrayList<String> getNounList(){
        return nounList;
    }
    public ArrayList<String> getAdverbList(){
        return adverbList;
    }
    public ArrayList<String> getVerbList(){
        return verbList;
    }

    private static int randInt(int min, int max){
        return (int)(Math.random() * (max - min + 1)) + min;
    }

    private String getRandom(ArrayList<String> list, int excludeCounter){
        int randIndex = randInt(0, list.size() - 1 - excludeCounter);
        String ret = list.get(randIndex);
        list.set(randIndex, list.get(list.size() - 1 - excludeCounter));
        list.set(list.size() - 1 - excludeCounter, ret);
        return ret;
    }

    public String getRandomAdjective(){
        String ret = getRandom(adjectiveList, adjectiveCounter);
        adjectiveCounter++;
        return ret;
    }

    public String getRandomNoun(){
        String ret = getRandom(nounList, nounCounter);
        nounCounter++;
        return ret;
    }

    public String getRandomAdverb(){
        String ret = getRandom(adverbList, adverbCounter);
        adverbCounter++;
        return ret;
    }

    public String getRandomVerb(){
        String ret = getRandom(verbList, verbCounter);
        verbCounter++;
        return ret;
    }
}
