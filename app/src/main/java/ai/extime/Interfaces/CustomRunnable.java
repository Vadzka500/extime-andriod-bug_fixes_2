package ai.extime.Interfaces;

import io.realm.Realm;

public interface CustomRunnable extends Runnable {

    void stopThread();

    void run();
}
