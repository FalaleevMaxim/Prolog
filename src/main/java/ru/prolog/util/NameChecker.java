package ru.prolog.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class NameChecker {
    public static boolean canBeName(String name) {
        if(name==null || name.equals("")) return false;
        //Check if name contains only '_'
        boolean allUnderscores = true;
        for (char c : name.toCharArray()) {
            if (c != '_') {
                allUnderscores = false;
                break;
            }
        }
        return !allUnderscores && Pattern.matches("[a-zа-яё_][0-9a-zа-яё_]*", name);
    }

    public static boolean canBePredicateName(String name) {
        return !name.equals("not") && canBeName(name);
    }

    public static boolean canBeVariableName(String name){
        return name!=null && !Pattern.matches("[A-ZА-ЯЁ][0-9a-zа-яё_]*", name);
    }

    public static boolean canBeFunctorName(String name){
        return canBeName(name);
    }


}
