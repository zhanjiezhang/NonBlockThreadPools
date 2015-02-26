package com.mobcent.nonblock;

public class TestTimeoutTask extends AbstractTimeoutTask {

    @Override
    public void doTask() throws Exception {
        Thread.sleep(4 * 30 * 1000);
        System.out.println("task..com");
    }
}
