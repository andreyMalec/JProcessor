# JProcessor

JProcessor is a compile-time framework for dependency injection. It uses no
reflection or runtime bytecode generation, does all its analysis at
compile-time, and generates plain Java source code.

The dependency injection pattern leads to code that's modular and testable,
and JProcessor makes it easy to write.

## Dependency Injection with JProcessor

For our example, we'll try to build a car by injecting its components.
We can add the annotations to fields or the constructor. But, since JProcessor
doesn't support injection on private fields, we'll go for constructor injection
to preserve encapsulation:
```java
public class Car {
    private Engine engine;
    private Brand brand;

    @Inject
    public Car(Engine engine, Brand brand) {
        this.engine = engine;
        this.brand = brand;
    }
}
```
Next, we'll implement the code to perform the injection. More specifically, we'll
create a module, which is a class that provides the objects.

### Module

To create a module, we need to annotate the class with the @Module annotation.
This annotation indicates that the class can make dependencies available to the
Injector:
```java
@Module
public class VehiclesModule {}
```
Then, we need to add the @Provides annotation on methods that construct our dependencies:
```java
@Module
public class VehiclesModule {
    @Provides
    public Engine provideEngine() {
        return new Engine();
    }

    @Provides
    @Singleton
    public Brand provideBrand() { 
        return new Brand("BrandName"); 
    }
}
```
In this case, we give the @Singleton annotation to our Brand instance so all the car instances
share the same brand object.

### Client code

Finally, we can run mvn compile in order to trigger the annotation processor
and generate the injector code. After that, we'll find our injector implementation
with the name Injector.
```java
@Test
public void testDependenciesInjected() {
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
```

### Public fields

```java
public class Car {
    @Inject
    public Engine engine;
    
    @Inject
    public Brand brand;
}
```
In the case of injection into public fields, we can inject directly in the constructor:
```java
public class Car {
    @Inject
    public Engine engine;
    
    @Inject
    public Brand brand;

    public Car() {
        Injector.get().inject(this);
    }
}
```
Or in any other place convenient for us:
```java
@Test
public void testDependenciesInjected() {
    Car car = new Car();

    assertNull(car.getEngine());
    assertNull(car.getBrand());
    
    Injector.get().inject(car);

    assertNotNull(car.getEngine());
    assertNotNull(car.getBrand());
}
```