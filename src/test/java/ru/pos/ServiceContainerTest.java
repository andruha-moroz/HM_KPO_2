package ru.pos;

import org.junit.jupiter.api.Test;
import ru.pos.bootstrap.ServiceContainer;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class ServiceContainerTest {
    @Test void resolvesAndCachesRegisteredSingleton() {
        ServiceContainer container = new ServiceContainer();
        AtomicInteger creations = new AtomicInteger();
        container.register(String.class, () -> "service-" + creations.incrementAndGet());

        assertEquals("service-1", container.resolve(String.class));
        assertEquals("service-1", container.resolve(String.class));
        assertEquals(1, creations.get());
    }

    @Test void failsClearlyWhenDependencyWasNotRegistered() {
        ServiceContainer container = new ServiceContainer();
        IllegalStateException error = assertThrows(IllegalStateException.class, () -> container.resolve(String.class));
        assertTrue(error.getMessage().contains(String.class.getName()));
    }
}
