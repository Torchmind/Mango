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

/**
 * Provides a definition for listeners
 *
 * @param <T> the represented value type.
 * @author Johannes Donath
 */
@FunctionalInterface
public interface ObservableValueListener<T> {

        /**
         * Handles changes to observed values.
         *
         * @param observable the observable triggering the change notification.
         * @param oldValue   the old value (if any).
         * @param newValue   the new value (if any).
         */
        void change(@Nonnull ObservableValue<T> observable, @Nullable T oldValue, @Nullable T newValue);
}