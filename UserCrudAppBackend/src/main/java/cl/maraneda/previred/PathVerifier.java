package cl.maraneda.previred;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

@Component
public class PathVerifier {
    @PostConstruct
    public void verify() {
        try {
            ClassLoader cl = ClassLoader.getSystemClassLoader();
            Enumeration<URL> resources = cl.getResources("db/migration");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                System.out.println("Found migration dir: " + url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
