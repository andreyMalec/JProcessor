package samples;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class CarTest {
    @Test
    public void testCar() {
        Car carOne = Injector.get().getCar();
        Car carTwo = Injector.get().getCar();

        assertNotNull(carOne);
        assertNotNull(carTwo);
        assertNotNull(carOne.getEngine());
        assertNotNull(carTwo.getEngine());
        assertNotNull(carOne.getBrand());
        assertNotNull(carTwo.getBrand());
        assertNotEquals(carOne.getEngine(), carTwo.getEngine());
        assertEquals(carOne.getBrand(), carTwo.getBrand());
    }
}
