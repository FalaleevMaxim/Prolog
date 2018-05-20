package ru.prolog.compiler;

import ru.prolog.compiler.position.CodeInterval;
import ru.prolog.compiler.position.CodePos;

public class CompileException extends RuntimeException {
    private CodePos pos;
    private CodeInterval interval;

    public CompileException(CodePos pos, String message) {
        super(pos!=null?(pos.toString()+':'+message):message);
        this.pos = pos;
    }

    public CompileException(CodePos pos, String message, Throwable cause) {
        super(pos!=null?(pos.toString()+':'+message):message, cause);
        this.pos = pos;
    }

    public CompileException(CodeInterval interval, String message) {
        super(interval!=null?(interval.getStartPos().toString()+':'+message):message);
        this.interval = interval;
        if(interval!=null)
            pos = interval.getStartPos();
    }

    public CompileException(CodeInterval interval, String message, Throwable cause) {
        super(interval!=null?(interval.getStartPos().toString()+':'+message):message, cause);
        this.interval = interval;
        if(interval!=null)
            pos = interval.getStartPos();
    }

    public CodeInterval getInterval(){
        return interval;
    }

    public CodePos getPos() {
        return pos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompileException)) return false;

        CompileException that = (CompileException) o;

        if (pos != null ? !pos.equals(that.pos) : that.pos != null) return false;
        return interval != null ? interval.equals(that.interval) : that.interval == null;
    }

    @Override
    public int hashCode() {
        int result = pos != null ? pos.hashCode() : 0;
        result = 31 * result + (interval != null ? interval.hashCode() : 0);
        return result;
    }
}
