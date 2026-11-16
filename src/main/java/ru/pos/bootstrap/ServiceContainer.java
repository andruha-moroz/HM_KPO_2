package ru.pos.bootstrap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/** Небольшой singleton DI-контейнер: зависимости собираются в composition root, слои не создают адаптеры сами. */
public final class ServiceContainer {
    private final Map<Class<?>, Supplier<?>> bindings = new HashMap<>();
    private final Map<Class<?>, Object> singletons = new HashMap<>();
    public <T> void register(Class<T> type, Supplier<? extends T> provider) { bindings.put(type, provider); }
    public <T> void registerInstance(Class<T> type, T instance) { singletons.put(type, instance); }
    public <T> T resolve(Class<T> type) {
        Object existing = singletons.get(type);
        if (existing != null) return type.cast(existing);
        Supplier<?> provider = bindings.get(type);
        if (provider == null) throw new IllegalStateException("Нет DI-привязки для " + type.getName());
        T value = type.cast(provider.get()); singletons.put(type, value); return value;
    }
}
