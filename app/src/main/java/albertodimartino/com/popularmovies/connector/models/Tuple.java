package albertodimartino.com.popularmovies.connector.models;

public class Tuple<T, D> {

    public Tuple() {
    }

    public Tuple(T t1, D t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    private T t1;
    private D t2;

    public T getT1() {
        return t1;
    }

    public void setT1(T t1) {
        this.t1 = t1;
    }

    public D getT2() {
        return t2;
    }

    public void setT2(D t2) {
        this.t2 = t2;
    }
}
