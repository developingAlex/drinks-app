package com.example.user1.bevreq;

import java.util.ArrayList;

public interface Calculator {
    public int[] getAmounts(ArrayList<Drink> globaldrinks, int pax, float duration, String functionType, ArrayList<String>ads);
}
