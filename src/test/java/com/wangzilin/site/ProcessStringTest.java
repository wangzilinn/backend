package com.***REMOVED***.site;

import com.***REMOVED***.site.services.AccessCards;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class ProcessStringTest {
    @Autowired
    public AccessCards accessCards;
    @Test
    public void test() {
        System.out.println(accessCards.getLocalCards());
    }
}
