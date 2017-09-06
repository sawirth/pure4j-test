import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting test..");
        List<Class<?>> classes = new ArrayList<>();
        try {
            File dir = new File("C:\\Users\\Sandro\\Documents\\GitHub\\pure4j\\out\\production\\test");
            URL[] paths = {dir.toURI().toURL()};
            URLClassLoader classLoader = new URLClassLoader(paths);

            for (File file : dir.listFiles()) {
                if (file.getName().endsWith(".class")) {
                    String className = file.getName().replace(".class", "");
                    Class<?> cl = classLoader.loadClass(className);
                    classes.add(cl);
                    System.out.println("Added class " + className);
                }
            }
        } catch (NullPointerException | MalformedURLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        Class<?>[] classArray = classes.toArray(new Class<?>[classes.size()]);

        TestRunner runner = new TestRunner(classArray);
        runner.Run();
    }
}
