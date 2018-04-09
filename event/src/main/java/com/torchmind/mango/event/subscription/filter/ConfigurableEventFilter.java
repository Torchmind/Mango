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
package com.torchmind.mango.event.subscription.filter;

import com.torchmind.mango.event.utility.CancelableEvent;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Set;

/**
 * Provides an event filter implementation which is exclusively used when dynamically creating
 * filters using the builder API.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
class ConfigurableEventFilter implements EventFilter {

  private Set<Class<?>> eventTypes;

  private boolean acceptingCancelled;
  private boolean acceptingHeirs;

  public ConfigurableEventFilter(
      @NonNull Set<Class<?>> eventTypes,
      boolean acceptingCancelled,
      boolean acceptingHeirs) {
    this.eventTypes = eventTypes;
    this.acceptingCancelled = acceptingCancelled;
    this.acceptingHeirs = acceptingHeirs;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean accepts(@NonNull Class<?> type) {
    if (!this.acceptingHeirs) {
      return this.eventTypes.contains(type);
    }

    return this.eventTypes.stream().anyMatch((clazz) -> clazz.isAssignableFrom(type));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean accepts(@NonNull Object event) {
    return !(event instanceof CancelableEvent) || this.acceptingCancelled
        || !((CancelableEvent) event).isCancelled();
  }
}
