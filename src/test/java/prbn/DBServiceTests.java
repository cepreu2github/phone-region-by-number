package prbn;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PhoneRegionByNumberApplication.class)
@WebAppConfiguration
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class DBServiceTests {
    @Autowired
    IDBService dbService;

    @Test
    public void updateDatetimeTests() { // TODO: make it

    }

    @Test
    public void hashTestsTests() { // TODO: make it

    }

    @Test
    public void mainTableTests() { // TODO: make it

    }

}
