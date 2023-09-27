package org.example;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static void main(String[] args) {
        BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
        BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
        BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);

        for (int i = 0; i < 100; i++) {
            String text = generateText("abc", 100);
            try {
                queueA.put(text);
                queueB.put(text);
                queueC.put(text);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        TextAnalyzer analyzerA = new TextAnalyzer(queueA, 'a');
        TextAnalyzer analyzerB = new TextAnalyzer(queueB, 'b');
        TextAnalyzer analyzerC = new TextAnalyzer(queueC, 'c');


        Thread threadA = new Thread(analyzerA);
        Thread threadB = new Thread(analyzerB);
        Thread threadC = new Thread(analyzerC);

        threadA.start();
        threadB.start();
        threadC.start();

        try {
            threadA.join();
            threadB.join();
            threadC.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Max 'a' count " + analyzerA.getCount());
        System.out.println("Max 'b' count " + analyzerB.getCount());
        System.out.println("Max 'c' count " + analyzerC.getCount());
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static class TextAnalyzer implements Runnable {
        private final BlockingQueue<String> queue;
        private final char targetChar;
        private int count = 0;

        public TextAnalyzer(BlockingQueue<String> queue, char targetChar) {
            this.queue = queue;
            this.targetChar = targetChar;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    String text = queue.take();
                    int count = countChar(text, targetChar);
                    updateMaxCount(count);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public static int countChar(String text, char targetChar) {
            int count = 0;
            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) == targetChar) {
                    count++;
                }
            }
            return count;
        }

        public void updateMaxCount(int count) {
            if (count > this.count) {
                this.count = count;
            }
        }

        public int getCount() {
            return count;
        }

    }
}