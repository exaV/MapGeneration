package ch.fhnw.ether.examples.visualizer;

public class AverageBuffer {
    private int     size;
    private float[] values;
    private int     index;
    private float   sum = 0.0f;
    
    public AverageBuffer(int size) {
        this.size = size;
        this.values = new float[size];
        this.index = 0;
    }
    
    public void push(double value) {
        push((float)value);
    }
    
    public void push(float value) {
        sum = sum - values[index];
        values[index] = value;
        sum = sum + value;
        index = (index + 1) % size;
    }
    
    public float getAverage() {
        return sum / size;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public void setSize(int size) {
        if (size == this.size)
            return;
        float[] old = values;
        float average = getAverage();
        this.size = size;
        values = new float[size];
        // Copy as much history as possible
        int min = (size < old.length ? size : old.length);
        int offset = size - min;
        for (int i = 0; i < min; i++)
            values[i + offset] = old[(index + i) % old.length];
        // Fill too old history with avergae
        for (int i = 0; i < offset; i++)
            values[i] = average;
        // Recompute sum
        sum = 0.0f;
        for (int i = 0; i < size; i++)
            sum+= values[i];
        // Reset index
        index = 0;
    }
}
