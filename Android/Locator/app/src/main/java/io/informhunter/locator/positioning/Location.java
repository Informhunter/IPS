package io.informhunter.locator.positioning;

/**
 * Created by informhunter on 29.05.2017.
 */

class Location {
    private Float X;
    private Float Y;

    public Location(float x, float y) {
        X = x;
        Y = y;
    }

    public float GetX() {
        return X;
    }

    public void SetX(float x) {
        X = x;
    }

    public float GetY() {
        return Y;
    }

    public void GetY(float y) {
        Y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (!X.equals(location.X)) return false;
        return Y.equals(location.Y);

    }

    @Override
    public int hashCode() {
        int result = X.hashCode();
        result = 31 * result + Y.hashCode();
        return result;
    }
}
