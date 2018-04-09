/*
 * Copyright 2018 Johannes Donath <johannesd@torchmind.com>
 * and other copyright owners as documented in the project's IP log.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.torchmind.mango.event.subscription;

import com.torchmind.mango.event.EventBus;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * <p>Represents a subscription registration within an arbitrary event bus.</p>
 *
 * <p>Handles provide an implementation agnostic method for removing and re-registering generated
 * subscriptions (e.g. subscriptions which have been created for a method or complete object).</p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface SubscriptionHandle {

  /**
   * Retrieves the event bus in which this subscription has been created.
   *
   * @return an event bus.
   */
  @NonNull
  EventBus getEventBus();

  /**
   * Evaluates whether this subscription is currently active (e.g. registered with its event bus).
   *
   * @return true if active, false otherwise.
   * @see EventBus#isRegistered(Subscription)
   */
  boolean isActive();

  /**
   * Removes the subscription from the event bus.
   *
   * @throws IllegalStateException when the subscription is not currently active.
   * @see EventBus#unregister(Subscription)
   */
  void remove();

  /**
   * Restores the subscription within the event bus.
   *
   * @throws IllegalStateException when the subscription is already active.
   * @see EventBus#register(Subscription)
   */
  void restore();
}
