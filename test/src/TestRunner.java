import org.pure4j.exception.Pure4JException;
import org.pure4j.model.ProjectModel;
import org.pure4j.processor.Callback;
import org.pure4j.processor.ClassFileModelBuilder;
import org.pure4j.processor.PurityChecker;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class TestRunner {

    private Class<?>[] classes;
    private final Set<String> pures = new LinkedHashSet<String>();
    private final Map<Class<? extends Pure4JException>, Integer> errorSet = new HashMap<>();
    private final List<Pure4JException> exceptions = new ArrayList<>();

    public TestRunner(Class<?>[] classes) {
        this.classes = classes;
    }

    public void Run() {
        try {
            ProjectModel pm = createModel(classes, "");
            PurityChecker checker = new PurityChecker(this.getClass().getClassLoader());
            Callback cb = getCallback();
            checker.checkModel(pm, cb);
            pures.forEach(System.out::println);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
    private ProjectModel createModel(Class<?>[] classes, String packageStem) throws IOException {
        ClassFileModelBuilder cfmb = new ClassFileModelBuilder(false);
        ClassLoader cl = this.getClass().getClassLoader();
        DefaultResourceLoader drl = new DefaultResourceLoader(cl);
        Set<Resource> resources = new HashSet<Resource>();

        for (Class<?> c : classes) {
            visitAllOf(c, drl, cfmb, packageStem, new HashSet<Class<?>>(), resources);
        }

        for (Resource resource : resources) {
            cfmb.visit(resource);
        }

        ProjectModel pm = cfmb.getModel();
        return pm;
    }

    private void visitAllOf(Class<?> c, DefaultResourceLoader drl, ClassFileModelBuilder cfmb, String packageStem, Set<Class<?>> done, Set<Resource> resources) throws IOException {
        if ((c != Object.class) && (c != null) && (!done.contains(c))) {
            done.add(c);
            System.out.println("visiting: " + c);
            if (c.getName().startsWith(packageStem)) {
                resources.add(drl.getResource("classpath:/" + c.getName().replace(".", "/") + ".class"));
                for (Class<?> intf : c.getInterfaces()) {
                    visitAllOf(intf, drl, cfmb, packageStem, done, resources);
                }

                for (Class<?> cl : c.getClasses()) {
                    visitAllOf(cl, drl, cfmb, packageStem, done, resources);
                }

                visitAllOf(c.getSuperclass(), drl, cfmb, packageStem, done, resources);
            }
        }
    }

    private Callback getCallback() {
        return new Callback() {

            @Override
            public void send(String s) {
                System.out.println(s);
                System.out.flush();
            }

            @Override
            public void registerError(Pure4JException e) {
                Integer count = errorSet.get(e.getClass());
                if (count == null) {
                    count = 1;
                } else {
                    count = count + 1;
                }

                errorSet.put(e.getClass(), count);
                exceptions.add(e);
                System.err.println(e.getClass() + ": " + e.getMessage());
                System.err.flush();
            }

            @Override
            public void registerPure(String signature, Boolean intf, Boolean impl) {
                if (intf && impl) {
                    pures.add(signature);
                }
            }
        };
    }
}
