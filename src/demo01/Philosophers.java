package demo01;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;


/**
 *  @author : zy
 *  哲学家筷子争夺问题:题目内容：有五位哲学家，围坐在圆桌旁。
 *  他们只做两件事，思考和吃饭，思考一会吃口饭，吃完饭后接着思考。
 *  吃饭时要用两根筷子吃，桌上共有 5 根筷子，每位哲学家左右手边各有一根筷子。
 *  如果筷子被身边的人拿着，自己就得等待.
 *  哲学家进餐死锁解决方法：规定奇数号哲学家先拿他左边的筷子，然后再去拿右边的筷子，而偶数号哲学家则相反。
 *  按此规定，将是 0、1 号哲学家竞争 0号筷子；2、3 号哲学家竞争 2 号筷子。即五位
 *  哲学家都先竞争奇数号筷子，获得后，再去竞争偶数号筷子，最后总会有一位哲学家能获
 *  得两只筷子而进餐。
 */
public class Philosophers extends Thread {

    private final static String NAME = "哲学家";

    /**
     * 哲学家编号
     */
    private final int philosopherNum;

    /**
     * 模拟筷子资源信号量
     */
    private volatile ArrayList<Semaphore> semaphores;

    public Philosophers(int philosopherNum,ArrayList<Semaphore> semaphores){
        super(Philosophers.NAME + (philosopherNum));
        this.philosopherNum = philosopherNum;
        this.semaphores = semaphores;
    }

    @Override
    public void run() {

        try {
            if(this.philosopherNum % 2 == 0){
                //哲学家先拿起左手边筷子
                semaphores.get(this.philosopherNum).acquire();
                //哲学家拿起右手边筷子
                semaphores.get(this.getRightSemaphoreIndex(this.philosopherNum)).acquire();
            }else{
                //哲学家拿起右手边筷子
                semaphores.get(this.getRightSemaphoreIndex(this.philosopherNum)).acquire();
                //哲学家先拿起左手边筷子
                semaphores.get(this.philosopherNum).acquire();
            }
            LocalTime time = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
            System.out.println(time.format(formatter) + "---" + this.getName() + "拿到了筷子" + (this.philosopherNum) + "，" + (this.getRightSemaphoreIndex(this.philosopherNum)) + "  --eating...");
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            semaphores.get(this.philosopherNum).release();
            semaphores.get(this.getRightSemaphoreIndex(this.philosopherNum)).release();
        }
    }

    /**
     * 获取当前哲学家右手边的筷子编号
     * @param philosopherNum ：当前哲学家编号
     * @return ：该哲学家右手边的筷子编号
     */
    private int getRightSemaphoreIndex(int philosopherNum){
        return (philosopherNum - 1 < 0) ? semaphores.size() - 1 : philosopherNum - 1;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) {
        ArrayList<Semaphore> semaphores = new ArrayList<>();
        int count = 5;
        for(int i = 0 ; i < count ; i++) {
            semaphores.add(new Semaphore(1));
        }
        while(true) {
            for(int i = 0 ; i < count ; i++) {
                new Philosophers(i, semaphores).start();
            }
        }
    }
}

