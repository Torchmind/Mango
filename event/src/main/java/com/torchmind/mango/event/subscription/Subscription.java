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

import com.torchmind.mango.event.error.EventException;
import com.torchmind.mango.event.subscription.filter.EventFilter;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a subscription to an arbitrary set of event types.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface Subscription extends EventFilter {

  /**
   * Defines the highest possible priority which theoretically causes a subscription to be executed
   * before any other registration.
   */
  int HIGHEST_PRIORITY = Integer.MIN_VALUE;

  /**
   * Defines the lowest possible priority which theoretically causes a subscription to be executed
   * after any other registration.
   */
  int LOWEST_PRIORITY = Integer.MAX_VALUE;

  /**
   * <p>Retrieves the relative order in which this subscription is to be executed when multiple
   * registrations match the same event.</p>
   *
   * <p>When multiple matching events indicate the same relative priority, the order of execution is
   * undefined (e.g. either of the subscriptions may gain precedence depending on the
   * implementation).</p>
   *
   * @return a priority.
   */
  int getPriority();

  /**
   * <p>Handles an event which the subscription has previously expressed interest in.</p>
   *
   * <p>This method is only invoked if both {@link #accepts(Class)} and {@link #accepts(Object)}
   * return true in response to the event type and object respectively. If any of these methods
   * returns false, the handling will be skipped.</p>
   *
   * @param event an event.
   * @throws EventException when the handler invocation fails.
   */
  void handle(@NonNull Object event);
}
