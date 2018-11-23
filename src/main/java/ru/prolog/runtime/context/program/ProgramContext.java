package ru.prolog.runtime.context.program;

import ru.prolog.model.ModelObject;
import ru.prolog.model.program.Program;
import ru.prolog.runtime.RuntimeObject;
import ru.prolog.runtime.database.Database;
import ru.prolog.util.io.ErrorListenerHub;
import ru.prolog.util.io.InputDevice;
import ru.prolog.util.io.OutputDeviceHub;
import ru.prolog.util.window.PrologWindowManager;

/**
 * Контекст вызова программы. Из него запускается целевой предикат;
 * в нём можно хранить данные, которые нужны предикатам во время выполнения программы;
 * в нём хранятся устройства ввода и вывода, используемые программой;
 * в нём хранится изменяемая база данных для предикатов бд;
 * через этот объект можно наладить взаимодействие между предикатами программы и вызывающей Пролог-программу средой.
 */
public interface ProgramContext extends RuntimeObject {
    String KEY_DEBUG_FILE = "DebugFile";
    String KEY_DEBUG_OUTPUT_DEVICE = "DebugOutput";

    /**
     * Возвращает объект программы, для которого создан этот вызов.
     * Объект класса {@link Program} неизменяемый и является корнем модели программы.
     * Объект {@code ProgramContext} хранит состояние, нужное и изменяемое при выполнении программы.
     *
     * @return Объект программы, для которого создан этот вызов.
     */
    Program program();

    /**
     * Возвращает изменяемую базу данных для хранения фактов для предикатов БД.
     *
     * @return Изменяемая база данных для хранения фактов для предикатов БД.
     */
    Database database();

    @Override
    default ModelObject model() {
        return program();
    }

    /**
     * Возвращает данные, хранящиеся в контексте программы по ключу.
     *
     * @return Возвращает данные, сохранённые в контексте программы по ключу, или {@code null}.
     * @see #putContextData(String, Object)
     */
    Object getContextData(String key);

    /**
     * Сохраняет объект в контексте программы по ключу. Если объект по такому ключу ужет существует, старое значение будет перезаписано.
     * <p>
     * Любой предикат программы может получить доступ к этим данным используя метод {@link #getContextData(String) getContextData}.
     * Таким образом можно обмениваться данными между разными вызовами предиката, между разными предикатами или даже между предикатами и вызывающей Пролог-программу средой.
     *
     * @param key  Ключ, по которому будет сохранён объект. Должен быть уникальным, чтобы не допускать коллизий с другими предикатами в проекте, которые могут использовать такой же ключ.
     * @param data Любой объект, который нужно сохранить в контексте программы для обмена данными.
     */
    void putContextData(String key, Object data);

    /**
     * Запускает целевой предикат.
     */
    boolean execute();

    /**
     * Возвращает устройство ввода, которое в данный момент установлено для ввода символов и строк от пользователя.
     * <p>
     * Обычно используется предикатами {@link ru.prolog.std.io.ReadCharPredicate}, {@link ru.prolog.std.io.ReadLnPredicate}
     * и другими предикатами для чтения.
     *
     * @return Устройство ввода, которое в данный момент установлено для ввода символов и строк от пользователя.
     * @see #setInputDevice(InputDevice)
     */
    InputDevice getInputDevice();

    /**
     * Устанавливает устройство ввода, которое будет использоваться для чтения пользовательского ввода символов и строк.
     * <p>
     * В один момент времени в программе может быть установлено только одно устройство ввода, но устройство может меняться в процессе раюоты программы.
     *
     * @param device Новое устройство ввода.
     * @see #getInputDevice()
     */
    void setInputDevice(InputDevice device);

    /**
     * Возвращает объект, хранящий устройства вывода программы и направляющий вывод на устройства.
     * <p>
     * Используется предикатами вывода {@link ru.prolog.std.io.WritePredicate}, {@link ru.prolog.std.io.NlPredicate}.
     * Может использоваться другими предикатами.
     * <p>
     * Можно добавлять и удалять устройства, используя методы {@link OutputDeviceHub#add(Object)} и {@link OutputDeviceHub#remove(Object)}.
     *
     * @return Объект, управляющий устройствами вывода программы.
     */
    OutputDeviceHub getOutputDevices();

    /**
     * Возвращает объект, хранящий устройства вывода ошибок программы и направляющий вывод на устройства.
     * <p>
     * Может использоваться любыми предикатами для сообщения пользователю об ошибке.
     * <p>
     * Можно добавлять и удалять устройства, используя методы {@link ErrorListenerHub#add(Object)} и {@link ErrorListenerHub#remove(Object)}.
     *
     * @return Объект, управляющий устройствами вывода ошибок программы.
     */
    ErrorListenerHub getErrorListeners();

    /**
     * Возвращает менеджер окон, используемый в программе.
     * Может быть {@code null} если окна не поддерживаются в данном исполнителе.
     *
     * @return Используемый менеджер окон или {@code null} если его нет.
     */
    PrologWindowManager getWindowManager();

    /**
     * Устанавливает менеджер окон.
     * Менеджер окон устанавливается исполнителем, использующим библиотеку интерпретатора, перед запуском программы.
     *
     * @param prologWindowManager Менеджер окон.
     */
    void setWindowManager(PrologWindowManager prologWindowManager);
}
