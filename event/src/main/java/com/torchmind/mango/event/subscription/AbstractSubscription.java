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

import com.torchmind.mango.event.subscription.filter.EventFilter;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides an abstract subscription implementation which delegates all of its decision making to an
 * arbitrarily defined event filter.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public abstract class AbstractSubscription implements Subscription {

  private final EventFilter eventFilter;
  private final int priority;

  public AbstractSubscription(@NonNull EventFilter eventFilter, int priority) {
    this.eventFilter = eventFilter;
    this.priority = priority;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getPriority() {
    return this.priority;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean accepts(@NonNull Class<?> type) {
    return this.eventFilter.accepts(type);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean accepts(@NonNull Object event) {
    return this.eventFilter.accepts(event);
  }
}
