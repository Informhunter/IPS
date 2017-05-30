package io.informhunter.locator.positioning;

/**
 * Created by informhunter on 29.05.2017.
 */
class Window {
    private int size;
    private int current;
    private float[] window;

    public Window(int wSize) {
        size = wSize;
        current = 0;
        window = new float[wSize];
    }

    public void AddPoint(float point) {
        window[current] = point;
        current += 1;
        if(current >= size) {
            current = 0;
        }
    }

    public float Average() {
        float sum = 0.0f;
        for(float x : window) {
            sum += x;
        }
        return sum / (float)size;
    }
}
