package com.sdu.bot.consumer;

import com.sdu.bot.entity.Bot;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotPool extends Thread {

    private final ReentrantLock lock = new ReentrantLock();    // 锁
    private final Condition condition = lock.newCondition();      // 条件变量
    private final Queue<Bot> bots = new LinkedList<>();      // 不一定线程安全 但可以通过操作锁变成线程安全的

    // 向队列中插入一个bot
    public void addBot(Integer userId, String botCode, String input) {
        lock.lock();
        try {
            bots.add(new Bot(userId, botCode, input));
            condition.signal();     // 这里要唤醒
        } finally {
            lock.unlock();
        }
    }

    /**
     *  消费一个任务 为了增加安全性 可以将这个函数放到沙箱中操作，比如docker
     *  Java在终端执行代码，设置好docker的最大运行空间和timeout 执行代码后将结果返回
     *  这里为了简单使用joor执行java代码 不支持其他语言的扩展
     *  将joor代码的执行放到一个线程中执行，放到线程内执行的好处是，如果一段代码超时，可以将这个线程断掉
     */
    private void consume(Bot bot) {
        JoorConsumer joorConsumer = new JoorConsumer();
        joorConsumer.startTimeout(5000, bot);
    }

    @Override
    public void run() {
        while (true) {
            // 两个线程会操作队列 需要加锁
            lock.lock();
            if (bots.isEmpty()) {
                try {
                    condition.await();  // 会自动释放锁
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    lock.unlock();
                    break;
                }
            } else {
                Bot bot = bots.remove();
                lock.unlock();
                consume(bot);   // 编译执行代码比较耗时 需要后执行
            }
        }
    }
}
