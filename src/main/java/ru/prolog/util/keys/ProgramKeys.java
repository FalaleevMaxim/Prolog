package ru.prolog.util.keys;

public class ProgramKeys {
    private ProgramKeys() {}

    /**
     * Используется логирующими обёртками предикатов и правил для сохранения уровня вызова в стеке
     */
    public static final String LOG_LEVEL = "log.level";

    /**
     * Используется логирующими обёртками предикатов и правил для получения файла лога
     */
    public static final String LOG_FILE = "log.file";

    /**
     * Используется логирующими обёртками предикатов и правил для получения устройств вывода лога
     */
    public static final String LOG_DEVICE = "log.devices";
}
