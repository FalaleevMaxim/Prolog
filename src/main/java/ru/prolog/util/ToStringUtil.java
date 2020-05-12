package ru.prolog.util;

import ru.prolog.model.type.Type;
import ru.prolog.runtime.values.Value;

import java.util.List;

public class ToStringUtil {
    private ToStringUtil() {
    }

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

    public static String ordinal(int num){
        String numStr = Integer.toString(++num);
        switch (num%10){
            case 1:
                return numStr+"st";
            case 2:
                return numStr+"nd";
            case 3:
                return numStr+"rd";
            default:
                return numStr+"th";
        }
    }

    /**
     * Returns given string in quotes and with escape characters
     * @param str string value
     * @return given string in quotes and with escape characters
     */
    public static String stringValue(String str){
        str = str.replace("\\","\\\\")
                .replace("\n","\\n")
                .replace("\r","\\r")
                .replace("\t","\\t")
                .replace("\"","\\\"");
        return "\""+str+"\"";
    }

    public static String charValue(char c){
        String str;
        switch (c){
            case '\n':
                str = "\\n";
                break;
            case '\r':
                str = "\\r";
                break;
            case '\t':
                str = "\\t";
                break;
            case '\'':
                str = "\\\'";
                break;
            case '\\':
                str = "\\\\";
                break;
            default: str = String.valueOf(c);
        }
        return "'"+str+"'";
    }

    public static String simpleToString(Type type, Object value){
        switch (type.getPrimitiveType().getName()){
            case "symbol":
                if(NameChecker.canBeName((String)value))
                    return (String)value;
                return stringValue((String) value);
            case "string":
                return stringValue((String) value);
            case "char":
                return charValue((char) value);
            default:
                return value.toString();
        }
    }

    public static String stringTokenValue(String tokenText) {
        String str = tokenText.substring(1, tokenText.length()-1);
        StringBuilder sb = new StringBuilder();
        boolean backslash = false;
        char u = 0;
        char uPos = 0;
        boolean unicode = false;
        for(char c : str.toCharArray()) {
            //Reading 4 hex to unicode character
            if(unicode){
                u*=16;
                u+=Character.forDigit(c,16);
                if(uPos<3)
                    uPos++;
                else{
                    uPos=0;
                    unicode=false;
                    sb.append(u);
                    u=0;
                }
            }
            //If this or previous character is not backslash, just write it;
            if(c!='\\' && !backslash){
                sb.append(c);
                continue;
            }
            switch (c){
                case '\\':
                    if(backslash){
                        //If it is second backslash, write it
                        sb.append(c);
                        backslash = false;
                    }else {
                        backslash = true;
                    }
                    break;
                case '\"':
                    sb.append('\"');
                    backslash = false;
                    break;
                case 'n':
                    sb.append('\n');
                    backslash = false;
                    break;
                case 't':
                    sb.append('\t');
                    backslash = false;
                    break;
                case 'r':
                    sb.append('\r');
                    backslash = false;
                    break;
                case 'u':
                    unicode = true;
                    backslash = false;
                    break;
            }
        }
        return sb.toString();
    }

    public static String prologFormat(String format, List<Value> args){
        StringBuilder sb = new StringBuilder();
        int i=0;
        for (char c : format.toCharArray()) {
            if(c=='%'){
                if(i>=args.size()){
                    sb.append(c);
                    continue;
                }
                Value arg = args.get(i++);
                if(arg.getType().isPrimitive())
                    sb.append(simpleToString(arg.getType(), arg.getContent()));
                else sb.append(arg);
            }else{
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
