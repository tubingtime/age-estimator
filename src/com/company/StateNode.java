package com.company;

public class StateNode<T> {
    String stateCode;
    List<People> people;

    StateNode(String stateCode, List<People> people){
        this.stateCode = stateCode;
        this.people = people;
    }

}
