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

/**
 * Provides a base interface for values which may be observed for value changes.
 *
 * @param <T> the represented value type.
 * @author Johannes Donath
 */
public interface ObservableValue<T> {

  /**
   * Appends a new listener to the queue of listeners.
   *
   * @param listener the listener instance to append.
   * @return a reference to this observable value.
   */
  @Nonnull
  ObservableValue<T> addListener(@Nonnull ObservableValueListener<T> listener);

  /**
   * Removes a new listener from the queue of listeners.
   *
   * @param listener the listener instance to remove.
   * @return a reference to this observable value.
   */
  @Nonnull
  ObservableValue<T> removeListener(@Nonnull ObservableValueListener<T> listener);
}
