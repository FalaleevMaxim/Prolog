package ru.prolog.syntaxmodel.source;

import ru.prolog.syntaxmodel.tree.Node;

/**
 * Источник исходного кода
 */
public abstract class CodeSource {
    /**
     * Имя файла исходного кода. Может быть null.
     */
    private String fileName;

    /**
     * Синтаксическое дерево, построенное по исходному коду.
     */
    private Node treeRoot;

    /**
     * Актуально ли состояние дерева.
     * Если код открыт в редакторе, в нём могут происхождить изменения, и на обновление дерева требуется время.
     * Если дерево полностью соответствует исходному коду, возвращает {@code true}.
     * Если в коде произошли изменения, ещё не отображённые в дереве, возвращает {@code false}.
     */
    public abstract boolean actual();

    public CodeSource() {
    }

    public CodeSource(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Возвращает текст исходного кода.
     */
    public abstract CharSequence getSourceText();

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


}
