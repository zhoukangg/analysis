package bupt.edu.cn.web.util.realtime;

import java.util.Iterator;
import java.util.Map;

class RunnableDemo implements Runnable {
    private Thread t;
    private String threadName;

    RunnableDemo(String name) {
        threadName = name;
        System.out.println("Creating " + threadName);
    }

    public void run() {
        System.out.println("Running " + threadName);
        try {
            while (true) {
                int time = 10;
                Iterator<Map.Entry<String, Long>> entries = HiveListener.FileChangeDate.entrySet().iterator();

                while (entries.hasNext()) {
                    Map.Entry<String, Long> entry = entries.next();
                    if (System.currentTimeMillis() - entry.getValue() > time * 1000) {
                        entries.remove();
                    }
                    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                }
                try {
                    Thread.sleep(time * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("Thread " + threadName + " interrupted.");
        }
        System.out.println("Thread " + threadName + " exiting.");
    }

    public void start() {
        System.out.println("Starting " + threadName);
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
}