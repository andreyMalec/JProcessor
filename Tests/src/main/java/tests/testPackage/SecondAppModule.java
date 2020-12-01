package tests.testPackage;

import jProcessor.Module;
import jProcessor.Provides;
import tests.B;
import tests.C;
import tests.Cat;
import tests.D;

@Module
public class SecondAppModule {
    @Provides
    public Cat provideCat() {
        return new Cat(2.3, 5, "Vasya");
    }

    @Provides
    public D d(B b, C c) {
        return new D(b, c);
    }

    @Provides
    public B b() {
        return new B(10);
    }
}
