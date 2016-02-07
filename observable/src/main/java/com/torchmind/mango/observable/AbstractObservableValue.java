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
package com.torchmind.mango.observable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Provides an abstract observable value implementation which provides implementations for adding and removing of
 * listeners as well as calling of such listeners.
 *
 * @param <T> the represented value type.
 * @author Johannes Donath
 */
@ThreadSafe
public abstract class AbstractObservableValue<T> implements ObservableValue<T> {
        private final Deque<ObservableValueListener<T>> listenerQueue = new ConcurrentLinkedDeque<>();

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public ObservableValue<T> addListener(@Nonnull ObservableValueListener<T> listener) {
                this.listenerQueue.push(listener);
                return this;
        }

        /**
         * Notifies all registered listeners about an update.
         *
         * @param oldValue the previous value.
         * @param newValue the new value.
         */
        protected void notify(@Nullable T oldValue, @Nullable T newValue) {
                this.listenerQueue.forEach((l) -> l.change(this, oldValue, newValue));
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public ObservableValue<T> removeListener(@Nonnull ObservableValueListener<T> listener) {
                this.listenerQueue.remove(listener);
                return this;
        }
}
