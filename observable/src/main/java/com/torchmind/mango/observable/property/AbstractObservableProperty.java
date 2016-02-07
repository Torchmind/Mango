/*
 * Copyright 2016 Johannes Donath <johannesd@torchmind.com>
 * and other copyright owners as documented in the project's IP log.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.torchmind.mango.observable.property;

import com.torchmind.mango.observable.AbstractObservableValue;
import com.torchmind.mango.observable.ObservableValueListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;

/**
 * Provides an abstract observable property implementation for simplified creation of observable property types.
 *
 * @author Johannes Donath
 */
abstract class AbstractObservableProperty<T> extends AbstractObservableValue<T> implements ObservableProperty<T> {
        private final Deque<ObservablePropertyListener<T>> listenerQueue = new ConcurrentLinkedDeque<>();
        private T value;
        private final ObservablePropertyListener<T> propertyListener = (ob, o, n) -> {
                this.value = n;
                this.notify(o, n);
        };
        private ObservableProperty<T> boundProperty = null;
        private boolean bidirectional = false;

        public AbstractObservableProperty(@Nullable T value) {
                this.setValue(value);
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public ObservableProperty<T> addListener(@Nonnull ObservablePropertyListener<T> listener) {
                this.listenerQueue.push(listener);
                return this;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public ObservableProperty<T> addListener(@Nonnull ObservableValueListener<T> listener) {
                super.addListener(listener);
                return this;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public ObservableProperty<T> bindTo(@Nonnull ObservableProperty<T> property) {
                return this.bindTo(property, false);
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        private ObservableProperty<T> bindTo(@Nonnull ObservableProperty<T> property, boolean bidirectional) {
                this.boundProperty = property;
                this.bidirectional = bidirectional;

                // append a listener to the bound property to ensure our values are locally updated in case the
                // property is unbound later on
                // also ensure the initial value is copied over
                property.addListener(this.propertyListener);
                this.propertyListener.change(property, null, property.getValue());

                return this;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public ObservableProperty<T> bindToBidirectionally(@Nonnull ObservableProperty<T> property) {
                return this.bindTo(property, true);
        }

        /**
         * {@inheritDoc}
         */
        @Nullable
        @Override
        public T getValue() {
                if (this.boundProperty == null) {
                        return this.value;
                }

                return this.boundProperty.getValue();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasBidirectionalBinding() {
                return this.isBound() && this.bidirectional;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isBound() {
                return this.boundProperty != null;
        }

        @Nonnull
        @Override
        public ObservableProperty<T> isBound(@Nonnull Consumer<ObservableProperty<T>> consumer) {
                Optional.ofNullable(this.boundProperty).ifPresent(consumer);
                return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isBoundTo(@Nonnull ObservableProperty<T> property) {
                return this.boundProperty == property;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void notify(@Nullable T oldValue, @Nullable T newValue) {
                super.notify(oldValue, newValue);

                this.listenerQueue.forEach((l) -> l.change(this, oldValue, newValue));
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public ObservableProperty<T> removeListener(@Nonnull ObservablePropertyListener<T> listener) {
                this.listenerQueue.remove(listener);
                return this;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public ObservableProperty<T> removeListener(@Nonnull ObservableValueListener<T> listener) {
                super.removeListener(listener);
                return this;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public ObservableProperty<T> setValue(@Nullable T value) {
                // handle different binding types first as they directly affect the way updates are handled internally
                // 1) A unidirectional binding was created and thus all modifications to this value will be rejected, or
                // 2) a bidirectional binding was created and thus all modifications are forwarded
                if (this.isBound()) {
                        if (this.hasBidirectionalBinding()) {
                                this.boundProperty.setValue(value);
                                return this;
                        }

                        throw new IllegalStateException("Cannot change the value of a bound property directly");
                }

                // before actually applying our value update in local memory we'll have to notify all of our listeners
                // of this update
                this.notify(this.value, value);

                // perform actual update in memory
                this.value = value;
                return this;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public ObservableProperty<T> unbind() {
                // ensure we are un-subscribed from the bound property as we do not intend to update our local value
                // any more
                this.boundProperty.removeListener(this.propertyListener);

                this.boundProperty = null;
                this.bidirectional = false;

                return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
                if (this == o) {
                        return true;
                }

                if (!(o instanceof AbstractObservableProperty)) {
                        return false;
                }

                AbstractObservableProperty<?> that = (AbstractObservableProperty<?>) o;
                return Objects.equals(this.value, that.value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
                return Objects.hash(value);
        }
}
