package com.example.kaist;

import java.util.LinkedList;
import java.util.Queue;

public class LimitedQueue {
    private int limit;
    private Queue<Float> queue;

    public LimitedQueue(int limit){
        this.limit = limit;
        this.queue = new LinkedList<Float>();
    }
    public void add(float e){
        if(this.queue.size()==this.limit)
            this.queue.poll();
        this.queue.offer(e);
    }

    public int getsize(){
            return this.queue.size();
        }
        public int min(int a, int b){ if(a>b)return b; return a; }
        public void get_array(float[] array){
        if(this.queue.size() < this.limit)
            return;
        for(int i = 0; i < min(array.length, this.queue.size()); i++) {
            array[i] = this.queue.poll();
            this.queue.offer(array[i]);
        }
    }
}
