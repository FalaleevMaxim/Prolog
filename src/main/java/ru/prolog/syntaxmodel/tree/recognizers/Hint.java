package ru.prolog.syntaxmodel.tree.recognizers;

import java.util.ArrayList;
import java.util.List;

public class Hint {
    /**
     * Текст ошибки
     */
    public String errorText;

    /**
     * Текст предложения по автодополнению
     */
    private List<String> completions;

    public Hint(String errorText, List<String> completions) {
        this.errorText = errorText;
        this.completions = completions;
    }

    public Hint(String errorText) {
        this.errorText = errorText;
    }

    public List<String> getCompletions() {
        if (completions == null) completions = new ArrayList<>();
        return completions;
    }
}
