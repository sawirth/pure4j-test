import org.pure4j.annotations.immutable.ImmutableValue;

@ImmutableValue
public class ClassToTest {
    private int id;
    private String name;
    private int age;
    private ImpureClass impureClass;

    public ClassToTest(int id, String name, int age, ImpureClass impureClass) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.impureClass = impureClass;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
//        System.out.println(id);
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void impureAction() {
        impureClass.impureMethod("test");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassToTest that = (ClassToTest) o;

        if (id != that.id) return false;
        if (age != that.age) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return impureClass != null ? impureClass.equals(that.impureClass) : that.impureClass == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + age;
        result = 31 * result + (impureClass != null ? impureClass.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClassToTest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", impureClass=" + impureClass +
                '}';
    }
}
