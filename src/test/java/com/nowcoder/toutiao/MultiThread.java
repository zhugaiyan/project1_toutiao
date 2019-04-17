package com.nowcoder.toutiao;



import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class MyThread extends  Thread{//线程是继承过来的
    private int tid;
    public MyThread(int tid){
        this.tid = tid;
    }

    //跑的内容放在run里面
    @Override
    public void run() {
        try{
            for(int i = 0; i < 10; ++i){
                Thread.sleep(1000);//每隔1s
                System.out.println(String.format("T%d:%d", tid, i));//打印
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

//Producer用于发事件
class Producer implements Runnable{
    private BlockingQueue<String> q;

    public Producer(BlockingQueue<String> q){
        this.q = q;
    }

    @Override
    public void run() {
        try{
            for(int i = 0; i < 10; ++i){
                Thread.sleep(1000);
                q.put(String.valueOf(i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

//用于取数据
class Consumer implements Runnable{
    private BlockingQueue<String> q;

    public Consumer(BlockingQueue<String> q){
        this.q = q;
    }

    @Override
    public void run() {
        try{
            //用循环一直取数据
            while(true){
                //打印当前线程和数据
                System.out.println(Thread.currentThread().getName() + ":" + q.take());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
public class MultiThread {
    public static void testThread(){
        for(int i = 0; i < 10; ++i){
            new MyThread(i).start();//new10个线程，并开启
        }

        //通过匿名函数，必须时final变量
        for(int i = 0; i < 10; ++i){
            final int tid = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        for(int i = 0; i < 10; i++){
                            Thread.sleep(1000);
                            System.out.println(String.format("T2%d:%d", tid, i));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }

    private static Object obj = new Object();//锁对象

    //synchronized (obj)内置锁，先把obj对象锁住，
    // 如果两个一起执行，只能有一个进去，被testSynchronized1()或testSynchronized2()拿走
    public static void testSynchronized1(){
        synchronized (obj){
            try{
                for(int i = 0; i < 10; ++i){
                    Thread.sleep(1000);
                    System.out.println(String.format("T3%d", i));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void testSynchronized2(){
        // 因为和testSynchronized1()是同一个obj对象，同时运行的时候，由于锁定，只能运行一个，谁拿到锁谁跑
        // 比如所以testSynchronized1()运行，或者testSynchronized2()运行，不会交叉运行
       // synchronized (obj){

        //由于synchronized中的变量对象不同，所以T3和T4交叉打印，因为延迟，T4再后边打印
        synchronized (new Object()){
        try{
                for(int i = 0; i < 10; ++i){
                    Thread.sleep(1000);
                    System.out.println(String.format("T4%d", i));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void testSynchronized(){
        for(int i = 0; i < 10; ++i){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    testSynchronized1();
                    testSynchronized2();
                }
            }).start();
        }
    }

    public static void testBlockingQueue(){
        BlockingQueue<String> q = new ArrayBlockingQueue<String>(10);
        //开启一个线程放数据
        new Thread(new Producer(q)).start();
        //开启两个消费线程
        new Thread(new Consumer(q), "Consumer1").start();
        new Thread(new Consumer(q), "Consumer2").start();
    }

    //BlockingQueue方法，服务器只能是一台
    //redis可以是多个服务器


    //两种计数方式
    //都是起10个线程，每个线程都是对数字加10
    private static int counter = 0;
    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void sleep(int mills){
        try{
            //Thread.sleep(new Random().nextInt(mills));//随机指定秒数
            Thread.sleep(mills);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void testWithAtomic(){
        for(int i = 0; i < 10; ++i){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sleep(1000);
                    for(int j = 0; j < 10; ++j){
                        System.out.println(atomicInteger.incrementAndGet());//加1
                    }
                }
            }).start();
        }
    }
    public static void testWithoutAtomic(){
        for(int i = 0; i < 10; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sleep(100);
                    for(int j = 0; j < 10; ++j){
                        counter++;
                        System.out.println(counter);
                    }
                }
            }).start();
        }
    }
    public static void testAtomic(){
        testWithAtomic();//最后不一定是100，因为++线程非安全
        testWithoutAtomic();//最后是100
    }

    private static ThreadLocal<Integer> threadLocalUserIds = new ThreadLocal<>();//每个线程都可以保存自己的一个副本
    private static int userId;//内存里只有一个变量

    //线程之间互不影响
    public static void testThreadLocal(){
        //起10个线程
        for(int i = 0; i < 10; ++i){
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    threadLocalUserIds.set(finalI);
                    sleep(1000);
                    System.out.println("ThreadLocal:" + threadLocalUserIds.get());
                }
            }).start();
        }

        for(int i = 0; i < 10; ++i){
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    userId = finalI;//userId是公共的变量
                    sleep(1000);
                    System.out.println("NonThreadLocal:" + userId);
                }
            }).start();
        }
    }

    public static void testExecutor(){//线程池任务框架
        //所有任务有service统一执行，Execute1做完后，做Execute2
        //ExecutorService service = Executors.newSingleThreadExecutor();//单线程的执行器

        //线程池，2个任务同时执行
        ExecutorService service = Executors.newFixedThreadPool(2);
        service.submit(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 10; ++i){
                    sleep(1000);
                    System.out.println("Executel" + i);
                }
            }
        });//不会关，等着任务进来或被关闭

        service.submit(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 10; ++i){
                    sleep(1000);
                    System.out.println("Execute2" + i);
                }
            }
        });
        service.shutdown();//关闭
        while (!service.isTerminated()){
            sleep(1000);
            System.out.println("Wait for termination.");//查看是否结束，没结束就打印
        }
    }

    public static void testFutrue(){
        //任务框架提交了任务，1s后返回1
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                sleep(1000);//延迟
                return 1;
                //throw new IllegalArgumentException("异常");
            }
        });

        service.shutdown();

        try{
            System.out.println(future.get(100, TimeUnit.MILLISECONDS));//设置时间点100ms
            //System.out.println(future.get());//等待获取返回值
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public static void main(String[] argv){
        //testThread();
        //testSynchronized();
        //testBlockingQueue();
        //testAtomic();
        //testThreadLocal();
        //testExecutor();
        testFutrue();
    }
}
