package ru.prolog.syntaxmodel.recognizers;

import ru.prolog.syntaxmodel.TokenKind;
import ru.prolog.syntaxmodel.TokenType;
import ru.prolog.syntaxmodel.source.CodeSource;
import ru.prolog.syntaxmodel.tree.Token;
import ru.prolog.syntaxmodel.tree.recognizers.Hint;
import ru.prolog.syntaxmodel.tree.recognizers.tokens.TokenRecognizer;
import ru.prolog.syntaxmodel.util.MappingCharSequence;

import java.util.List;

public class Lexer {
    private final CodeSource codeSource;

    /**
     * Участок кода, начинающийся после токена {@link #before}, и заканчивающийся в конце всего кода.
     */
    private CharSequence code;

    /**
     * Распознанный ранее токен, после которого идёт участок изменённого кода.
     * Может быть {@code null} если это парсинг с нуля или изменённый участок кода начинается с начала файла.
     */
    private Token before;

    /**
     * Распознанный ранее токен, следующий за изменённым участком кода.
     */
    private Token after;

    /**
     * Токен, на который указывает парсер в данный момент.
     */
    private Token pointer;

    /**
     * Длина кода между концом {@link #before} и началом {@link #after}. Участок кода, который, в идеале, нужно распознать.
     */
    private int diffLength;

    /**
     * Длина текста, который был распознан этим лексером.
     * Изначально 0; длина каждого распознанного токена прибавляется в эту переменную.
     * Когда эта переменная становится больше {@link #diffLength}, цепочка токенов должна завершиться(если это конец кода) или пытаться замкнуться на следующий токен.
     *
     * @see #closed
     */
    private int recognizedLength = 0;

    /**
     * Последний токен, распознанный этим лексером.
     * Первый распознанный токен должен содержать ссылку на {@link #before} в качестве предыдущего токена.
     * Каждый последующий распознанный токен должен устанавливать ссылку на предыдущий распознанный токен.
     */
    private Token lastRecognized;

    /**
     * Замкнута ли цепочка токенов, то есть, есть соединены ли ссылками све токены кода.
     * {@code false}, если ещё осталась часть кода, в которой токены не распознаны.
     */
    private boolean closed = false;

    public Lexer(CodeSource codeSource, CharSequence code) {
        this.codeSource = codeSource;
        this.code = code;
    }

    /**
     * @param codeSource Источник исходного кода
     * @param before     Последний токен перед изменившимся участком, которого не коснулось изменение ({@code null}) если перед изменившимся участком нет токенов.
     * @param after      Первый токен после изменившегося участка, которого не коснулось изменение.
     * @param diffLength Количество символов между концом before и началом after. Если 0 или меньше, то before и after связываются ссылками.
     */
    public Lexer(CodeSource codeSource, Token before, Token after, int diffLength) {
        this.codeSource = codeSource;
        String code = codeSource.getTreeSource();
        this.code = new MappingCharSequence(code, code.length() - diffLength, code.length());
        this.before = before;
        this.pointer = before;
        this.after = after;
        this.diffLength = diffLength;

        if (diffLength <= 0) {
            if (before != null) before.setNext(after);
            if (after != null) after.setPrev(before);
            this.closed = true;
        } else {
            before.setNext(null);
            after.setPrev(null);
        }
    }

    /**
     * Ставит указатель лексера на заданный токен. Понадобится при возврате в предыдущее правило парсера, когда правило парсера завершилось неудачно.
     *
     * @throws IllegalArgumentException если переданный токен не связан с екущим указателем
     */
    public void setPointer(Token token) {
        if (token != pointer && !token.isAfter(pointer) && !token.isBefore(pointer)) {
            throw new IllegalArgumentException("Token to point is not connected to current pointer");
        }
        this.pointer = token;
    }

    public Token getPointer() {
        return pointer;
    }

    public boolean isClosed() {
        return closed;
    }

    /**
     * Возвращает или распознаёт следующий токен указанного типа.
     *
     * @param tokenType Тип токена, которые нужны парсеру
     * @return Следующий токен, если он заданного типа, иначе {@code null}
     */
    public Token nextOfType(TokenType tokenType) {
        Token next = nextAfterPointer();
        if (next != null && tokenType.equals(next.getTokenType())) {
            pointer = next;
            return next;
        }
        Token recognized = tokenType.getRecognizer().recognize(code);
        if(recognized!=null) onTokenRecognize(recognized);
        return recognized;
    }

    private Token nextAfterPointer() {
        if (pointer == null) return null;
        return pointer.getNext();
    }

    /**
     * Возвращает первый неигнорируемый токен. Пропускает все игнорируемые токены перед ним.
     *
     * @return Возвращает первый неигнорируемый токен, пропуская игнорируемые. Если такого токена не найдено, возвращает {@code null}
     */
    public Token nextNonIgnored() {
        while (nextAfterPointer() != null) {
            Token next = pointer.getNext();
            pointer = next;
            if (next.getTokenType() != null && !TokenKind.IGNORED.equals(next.getTokenKind())) {
                return next;
            }
        }

        Token token;
        do {
            skipIgnored();
            token = nextToken();
        } while (token != null && token.getTokenType() == null && code.length() != 0); //Цикл продолжается, если у найденного токена нет типа (неизвеситный символ).
        return token;
    }

    /**
     * Распознаёт идущие подряд игнорируемые токены.
     *
     * @return последний из распознанных игнорируемых токенов.
     */
    public Token skipIgnored() {
        Token lastIgnored = null;

        while (nextAfterPointer() != null) {
            Token next = pointer.getNext();
            if (next.getTokenType() == null || TokenKind.IGNORED.equals(next.getTokenKind())) {
                pointer = next;
                lastIgnored = next;
            } else {
                return lastIgnored;
            }
        }

        List<TokenType> ignoredTypes = TokenType.getTokenTypes(TokenKind.IGNORED);
        boolean found = true;
        while (found) {
            found = false;
            for (TokenType type : ignoredTypes) {
                Token token = type.getRecognizer().recognize(code);
                if (token != null) {
                    lastIgnored = token;
                    onTokenRecognize(token);
                    found = true;
                    break;
                }
            }
        }
        return lastIgnored;
    }

    /**
     * Распознаёт один токен любого типа с начала ввода
     *
     * @return следующий токен (берёт уже распозныанный или распознаёт новый)
     */
    public Token nextToken() {
        if(nextAfterPointer()!=null) {
            Token token = nextAfterPointer();
            pointer = nextAfterPointer();
            return token;
        }
        if (code.length() == 0) return null;
        for (TokenType tokenType : TokenType.values()) {
            TokenRecognizer recognizer = tokenType.getRecognizer();
            Token token = recognizer.recognize(code);
            if (token != null) {
                onTokenRecognize(token);
                return token;
            }
        }
        char c = code.charAt(0);
        Token token = new Token(null, Character.toString(c), null, true);
        token.setHint(new Hint("Unknown character " + c));
        onTokenRecognize(token);
        return token;
    }

    /**
     * Когда распознан новый токен, добавляет ему ссылку на предыдущий, устанавливает его последним распознанным и устанавливает на него указатель.
     *
     * @param token распознанный токен
     */
    private void onTokenRecognize(Token token) {
        //Распознанный участок удаляется из начала кода.
        code = code.subSequence(token.length(), code.length());
        //Токен соединяется ссылками с предыдущим
        token.setPrev(pointer);
        if(pointer != null) pointer.setNext(token);
        //Токен записывается как последний распознанный
        lastRecognized = token;
        //Указатель устанавливается на новый токен
        pointer = token;
        //Длина распознанного участка кода увеличивается на длину распознанного токена.
        recognizedLength += token.length();

        //Если распознанный текст зацепил конечный токен, то конечным выбирается становится токен, следующий после распознанного участка.
        while (recognizedLength > diffLength && after != null) {
            diffLength += after.length();
            after = after.getNext();
        }

        //Если длина распознанного участка идеально совпадает с расстоянием между начальным и конечным токенами, то можно замкнуть цепояку токенов.
        if (recognizedLength == diffLength) {
            pointer.setNext(after);
            if(after != null) after.setPrev(pointer);
            closed = true;
        }
    }

    public CodeSource getCodeSource() {
        return codeSource;
    }
}
