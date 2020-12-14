package samples;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DatabaseTest {
    @Test
    public void testCar() {
        DatabaseService dbOne = Injector.get().getDatabaseService();
        DatabaseService dbTwo = Injector.get().getDatabaseService();

        assertNotNull(dbOne);
        assertNotNull(dbTwo);
        assertEquals(dbOne, dbTwo);
        assertNotNull(dbOne.getConnection());
        assertNotNull(dbTwo.getConnection());
        assertEquals(dbOne.getConnection(), dbTwo.getConnection());
    }
}

