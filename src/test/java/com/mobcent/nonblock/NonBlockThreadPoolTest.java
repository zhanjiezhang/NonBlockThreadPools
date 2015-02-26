package com.mobcent.nonblock;

public class NonBlockThreadPoolTest {
    public static void main(String[] args) {
        NonBlockThreadPool threadPool = new DefaultNonBlockThreadPool(1, 5);
        NonBlockThreadPool threadPool2 = new DefaultNonBlockThreadPool(1, 5);
//        while(true) {
//        for (int j = 0; j < 2; j++) {
            if (threadPool.canExecuteWait()) {
                System.out.println("commit task...");
                for (int i = 0; i < 1; i++) {
                    threadPool.execute(new TestTimeoutTask());
//                    threadPool2.execute(new TestTimeoutTask());
                }
//            }
//            System.out.println("next...");
            }
//            try {
//                Thread.sleep(1000 * 1);
//                System.out.println("Sleep...");
//            } catch (InterruptedException e1) {
//                e1.printStackTrace();
//            }
            try {
                boolean isCompleted = threadPool.isCompleted(3);
                System.out.println("isCompleted : " + isCompleted);
                if (threadPool.isCompleted(3)) threadPool.shutdownSafely(3);
            } catch (TimeoutException e) {
                e.printStackTrace();
                System.out.println("isCompleted TimeoutException : " + e.getMessage());
                threadPool.shutdownForce();
            }
    }
}
