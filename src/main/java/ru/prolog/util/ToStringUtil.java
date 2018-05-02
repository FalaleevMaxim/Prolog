package ru.prolog.util;

import java.util.List;

public class ToStringUtil {
    public static String funcToString(String name, List<?> args){
        if(args.isEmpty()) return name;
        StringBuilder sb = new StringBuilder(name).append('(');
        sb.append(args.get(0));
        for(int i=1;i<args.size();i++){
            sb.append(", ").append(args.get(i));
        }
        sb.append(')');
        return sb.toString();
    }
}
