package jProcessor.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class GuavaCollectors {
    public static <T> Collector<T, ?, ImmutableList<T>> toImmutableList() {
        return new ImmutableListCollector<>();
    }

    public static <T> Collector<T, ?, ImmutableSet<T>> toImmutableSet() {
        return new ImmutableSetCollector<>();
    }

    private static class ImmutableListCollector<T>
            implements Collector<T, ImmutableList.Builder<T>, ImmutableList<T>> {
        @Override
        public Supplier<ImmutableList.Builder<T>> supplier() {
            return ImmutableList.Builder::new;
        }

        @Override
        public BiConsumer<ImmutableList.Builder<T>, T> accumulator() {
            return ImmutableList.Builder::add;
        }

        @Override
        public BinaryOperator<ImmutableList.Builder<T>> combiner() {
            return (b1, b2) -> b1.addAll(b2.build());
        }

        @Override
        public Function<ImmutableList.Builder<T>, ImmutableList<T>> finisher() {
            return ImmutableList.Builder::build;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return ImmutableSet.of();
        }
    }

    private static class ImmutableSetCollector<T>
            implements Collector<T, ImmutableSet.Builder<T>, ImmutableSet<T>> {
        @Override
        public Supplier<ImmutableSet.Builder<T>> supplier() {
            return ImmutableSet.Builder::new;
        }

        @Override
        public BiConsumer<ImmutableSet.Builder<T>, T> accumulator() {
            return ImmutableSet.Builder::add;
        }

        @Override
        public BinaryOperator<ImmutableSet.Builder<T>> combiner() {
            return (b1, b2) -> b1.addAll(b2.build());
        }

        @Override
        public Function<ImmutableSet.Builder<T>, ImmutableSet<T>> finisher() {
            return ImmutableSet.Builder::build;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return ImmutableSet.of();
        }
    }
}
