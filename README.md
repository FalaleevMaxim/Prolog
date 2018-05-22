# Prolog
Интерпретатор Turbo Prolog на Java.

Интерпретатор готов и работает из командной строки. Чтобы собрать проект, используйте `mvn compile assembly:single.` В папке target появится Prolog.jar, который можно запускать из командной строки.  
Аргументом командной строки нужно передать путь к текстовому файлу с кодом программы на Turbo Prolog.  
Для записи лога можно дополнительно указать аргументы -d и путь к файлу, куда писать лог.  
Примеры запуска:  
`java -jar Prolog.jar code.txt`  
`java -jar Prolog.jar "C:\Users\Admin\IdeaProjects\Prolog\code.txt"`  
`java -jar Prolog.jar code.txt -d log.txt`  
