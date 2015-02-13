/*
 * Copyright (c) 2013 - 2015 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2015 FHNW & ETH Zurich
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *  Neither the name of FHNW / ETH Zurich nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */package ch.fhnw.ether.examples.visualizer;

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
