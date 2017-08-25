import org.pure4j.annotations.immutable.ImmutableValue;

@ImmutableValue
public class ImpureClass {
    private final int value = 0;

    public void impureMethod(String string) {
        System.out.println("test");
        string = string + " lol";
    }

    public void pureMethod() {
        int test = value;
        test++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImpureClass that = (ImpureClass) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return "ImpureClass{" +
                "value=" + value +
                '}';
    }
}
