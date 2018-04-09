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

import com.torchmind.mango.observable.ObservableValue;
import com.torchmind.mango.observable.ObservableValueListener;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provides a base interface for observable properties.
 *
 * @param <T> the property type.
 * @author Johannes Donath
 */
public interface ObservableProperty<T> extends ObservableValue<T> {

  /**
   * {@inheritDoc}
   */
  @Nonnull
  @Override
  ObservableProperty<T> addListener(@Nonnull ObservableValueListener<T> listener);

  /**
   * Appends a listener to the queue of listeners.
   *
   * @param listener the listener.
   * @return a reference to this property.
   */
  @Nonnull
  ObservableProperty<T> addListener(@Nonnull ObservablePropertyListener<T> listener);

  /**
   * Binds the property value to the value of another property.
   *
   * @param property a reference to the property to bind to.
   * @return a reference to this property.
   * @see #bindToBidirectionally(ObservableProperty) for creation of bidirectional bindings.
   */
  @Nonnull
  ObservableProperty<T> bindTo(@Nonnull ObservableProperty<T> property);

  /**
   * Creates a bidirectional binding which allows modifications of this property to be forwarded to
   * the bound property value.
   *
   * @param property a reference to the property to bind to.
   * @return a reference to this property.
   * @see #bindTo(ObservableProperty) for creation of unidirectional bindings.
   */
  @Nonnull
  ObservableProperty<T> bindToBidirectionally(@Nonnull ObservableProperty<T> property);

  /**
   * Retrieves the property value.
   *
   * @return a reference to the (possibly boxed) value.
   */
  @Nullable
  T getValue();

  /**
   * Sets the property value.
   *
   * @param value a reference to the new (possibly boxed) value.
   * @return a reference to this property.
   */
  @Nonnull
  ObservableProperty<T> setValue(@Nullable T value);

  /**
   * Checks whether the binding present on this property is bidirectional.
   *
   * @return true if bidirectional, false otherwise.
   */
  boolean hasBidirectionalBinding();

  /**
   * Checks whether this property is bound to another property's value.
   *
   * @return true if bound, false otherwise.
   */
  boolean isBound();

  /**
   * Checks whether this property is bound to another property's value and if so passes the property
   * which governs this property's value to the supplied consumer.
   *
   * @param consumer a consumer to pass the bound property to.
   * @return a reference to this property.
   */
  @Nonnull
  ObservableProperty<T> isBound(@Nonnull Consumer<ObservableProperty<T>> consumer);

  /**
   * Checks whether this property is bound to the specified property.
   *
   * @param property the property to check for a binding.
   * @return true if bound to the property, false otherwise.
   */
  boolean isBoundTo(@Nonnull ObservableProperty<T> property);

  /**
   * Removes a listener from the queue of listeners.
   *
   * @param listener the listener.
   * @return a reference to this property.
   */
  @Nonnull
  ObservableProperty<T> removeListener(@Nonnull ObservablePropertyListener<T> listener);

  /**
   * {@inheritDoc}
   */
  @Nonnull
  @Override
  ObservableProperty<T> removeListener(@Nonnull ObservableValueListener<T> listener);

  /**
   * Unbinds this property from any unidirectional or bidirectional bindings.
   *
   * @return a reference to this property.
   */
  @Nonnull
  ObservableProperty<T> unbind();
}
