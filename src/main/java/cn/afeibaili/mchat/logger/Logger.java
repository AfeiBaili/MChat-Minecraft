package cn.afeibaili.mchat.logger;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 日志类
 *
 * @author AfeiBaili
 * @version 2026/3/3 00:12
 */

public interface Logger {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");

    ExecutorService executor = Executors.newSingleThreadExecutor();

    void setName(String name);

    String getName();

    void info(String message);

    void warn(String message);

    void error(String message);

    default void print(String level, String message) {
        executor.execute(() -> {
            System.out.println("[" + level + "] " + getDataTime() + ": " + message);
        });
    }

    default String getDataTime() {
        return LocalDateTime.now().format(formatter);
    }

    static Logger getLogger() {
        return new Logger() {
            String name;

            @Override
            public void setName(String name) {
                this.name = name;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public void info(String message) {
                print("INFO", message);
            }

            @Override
            public void warn(String message) {
                print("WARN", message);
            }

            @Override
            public void error(String message) {
                print("ERROR", message);
            }
        };
    }
}
