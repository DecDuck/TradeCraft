package com.decduck3.tradecraft.utils;

import java.util.ArrayList;
import java.util.List;

public class SuperList {
    public static <T> List<T> superlist(List<T>... lists) {
        List<T> l = new ArrayList<>();
        for (List<T> i : lists) {
            l.addAll(i);
        }
        return l;
    }
}
