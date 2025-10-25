package cl.maraneda.previred;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MainTest {
    @Test
    public void contextLoads() {
        /*Nothing*/
    }

    @Test
    public void mainApplicationTest(){
        Main.main(new String[]{});
    }
}
