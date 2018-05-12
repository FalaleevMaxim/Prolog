package ru.prolog.logic.util;

import java.util.regex.Pattern;

public class NameChecker {
    private static boolean canBeName(String name) {
        return name != null &&
                !name.equals("") &&
                !isAllUnderscores(name) &&
                Pattern.matches("[a-z\\u0430-\\u044F\\u0451_][0-9a-z\\u0430-\\u044F\\u0451_A-Z\\u0410-\\u042F\\u0401]*", name);
    }

    //Check if name contains only '_'
    private static boolean isAllUnderscores(String name) {
        boolean allUnderscores = true;
        for (char c : name.toCharArray()) {
            if (c != '_') {
                allUnderscores = false;
                break;
            }
        }
        return allUnderscores;
    }

    public static boolean canBePredicateName(String name) {
        return !name.equals("not") && canBeName(name);
    }

    public static boolean canBeVariableName(String name){
        return "_".equals(name) || (name!=null && Pattern.matches("[A-Z\\u0410-\\u042F\\u0401][0-9a-z\\u0430-\\u044F\\u0451_A-Z\\u0410-\\u042F\\u0401]*", name));
    }

    public static boolean canBeFunctorName(String name){
        return canBeName(name);
    }


}
