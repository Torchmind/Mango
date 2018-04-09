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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides a basic filter specification which permits implementation agnostic filtering of events
 * based on their type and instance before they are passed to a subscription for handling.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface EventFilter {

  /**
   * <p>Evaluates whether this filter can theoretically matches events of the indicated type.</p>
   *
   * <p>The return value of this method must be constant (e.g. cannot change over time) as it is
   * cached by the event bus. Generally it is also recommended for implementations to express
   * interest in as little types as possible to improve performance.</p>
   *
   * @param type an event type.
   * @return true if the event type is theoretically accepted, false otherwise.
   */
  boolean accepts(@NonNull Class<?> type);

  /**
   * <p>Evaluates whether this filter matches the indicated event.</p>
   *
   * <p>This method is only invoked when a filter expresses general support for an event type and
   * gives it an additional chance to make a more informed decision on whether or not to execute
   * more complex handling logic.</p>
   *
   * @param event an event.
   * @return true if the event is accepted, false otherwise.
   */
  boolean accepts(@NonNull Object event);

  /**
   * Provides a factory for event filters.
   */
  class Builder {

    private final Set<Class<?>> eventTypes = new HashSet<>();
    private boolean acceptCancelled;
    private boolean acceptHeirs = true;

    /**
     * Creates a new filter based on the configuration within this builder.
     *
     * @return a filter.
     */
    @NonNull
    public EventFilter build() {
      Set<Class<?>> eventTypes = new HashSet<>(this.eventTypes);

      if (this.eventTypes.isEmpty()) {
        eventTypes = Collections.singleton(Object.class);
      }

      return new ConfigurableEventFilter(eventTypes, this.acceptCancelled, this.acceptHeirs);
    }

    /**
     * <p>Selects a set of event types which shall be accepted by the filter.</p>
     *
     * <p>By default, the filter will accept any instance of the supplied types (e.g. it will
     * include all of its sub types). This may be disabled using {@link
     * #withAcceptHeirs(boolean)}</p>
     *
     * <p>Note that this method will append the list of types to the internal set and thus extend it
     * rather than overwrite it.</p>
     *
     * @param types an array of acceptable event types.
     * @return a reference to this builder.
     */
    @NonNull
    Builder withEventType(@NonNull Class<?>... types) {
      this.eventTypes.addAll(Arrays.asList(types));
      return this;
    }

    /**
     * <p>Selects whether the filter shall accept events which have been explicitly marked as
     * cancelled.</p>
     *
     * <p>This value is ignored unless any of the accepted types (or its heirs if applicable)
     * implement {@link CancelableEvent}.</p>
     *
     * <p>By default filters will reject all cancelled events.</p>
     *
     * @param acceptCancelled if true accepts cancelled events, otherwise rejects them.
     * @return a reference to this builder.
     */
    @NonNull
    public Builder withAcceptCancelled(boolean acceptCancelled) {
      this.acceptCancelled = acceptCancelled;
      return this;
    }

    /**
     * <p>Selects whether the filter shall accept heirs.</p>
     *
     * <p>When disabled, the filter will only accept the exact event types listed within this
     * configuration and no instance checks will be performed.</p>
     *
     * <p>By default filters will accept all heirs.</p>
     *
     * @param acceptHeirs if true accepts heirs, otherwise rejects them.
     * @return a reference to this builder.
     */
    @NonNull
    public Builder withAcceptHeirs(boolean acceptHeirs) {
      this.acceptHeirs = acceptHeirs;
      return this;
    }
  }
}
