public class Main {
    public static void main(String[] args) {
        System.out.println("Starting test..");

        Class<?>[] classes = {ImpureClass.class, ClassToTest.class};

        TestRunner runner = new TestRunner(classes);
        runner.Run();
    }
}
