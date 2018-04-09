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

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides a registry which maps event types to their respective subscriptions and caches
 * subscription queues.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class SubscriptionRegistry {

  private final Set<Subscription> subscriptions = new HashSet<>();
  // TODO: This should probably be replaced with a real caching solution to prevent excessive memory usage
  private final Map<Class<?>, List<Subscription>> queueCache = new HashMap<>();

  /**
   * <p>Registers a new subscription with this registry.</p>
   *
   * <p>When the subscription is new to this registry, this call will automatically invalidate any
   * caches for event types which are generally accepted by the new subscription.</p>
   *
   * @param subscription a subscription.
   */
  public void register(@NonNull Subscription subscription) {
    if (this.subscriptions.add(subscription)) {
      this.invalidateCache(subscription);
    }
  }

  /**
   * <p>Removes a subscription from this registry.</p>
   *
   * <p>When the subscription is currently present within this registry, the call will automatically
   * invalidate any caches for event types which are generally accepted by the subscription.</p>
   *
   * @param subscription a subscription.
   */
  public void unregister(@NonNull Subscription subscription) {
    if (this.subscriptions.remove(subscription)) {
      this.invalidateCache(subscription);
    }
  }

  /**
   * Evaluates whether the indicated subscription is currently registered with this registry.
   *
   * @param subscription a subscription.
   * @return true if registered, false otherwise.
   */
  public boolean isRegistered(@NonNull Subscription subscription) {
    return this.subscriptions.contains(subscription);
  }

  /**
   * Retrieves a list of subscriptions which may intend to be notified about events of the indicated
   * type.
   *
   * @param eventType an event type.
   * @return a list of subscriptions (in their intended order of execution).
   */
  @NonNull
  public List<Subscription> getSubscriptions(@NonNull Class<?> eventType) {
    return Collections
        .unmodifiableList(this.queueCache.computeIfAbsent(eventType, this::computeCache));
  }

  /**
   * Computes the matching subscriptions for a given event type and their respective order of
   * execution.
   *
   * @param eventType an event type.
   * @return a list of subscriptions.
   */
  @NonNull
  private List<Subscription> computeCache(@NonNull Class<?> eventType) {
    return this.subscriptions.stream()
        .filter((subscription) -> subscription.accepts(eventType))
        .sorted(Comparator.comparingInt(Subscription::getPriority))
        .collect(Collectors.toList());
  }

  /**
   * <p>Deletes the entire listener cache for all event types within this registry.</p>
   *
   * <p>Invocation of this method will cause all subsequent calls of {@link
   * #getSubscriptions(Class)} to re-generate their list of subscriptions and should thus be avoided
   * as much as possible to prevent unnecessary load.</p>
   */
  public void invalidateCache() {
    this.queueCache.clear();
  }

  /**
   * <p>Deletes the listener cache for a given event type.</p>
   *
   * <p>Note that this method will only clear the cache for the exact indicated event type and
   * leaves all heir and ancestor caches in tact.</p>
   *
   * @param eventType an event type.
   */
  public void invalidateCache(@NonNull Class<?> eventType) {
    this.queueCache.remove(eventType);
  }

  /**
   * Deletes the listener cache for all types which are theoretically accepted by the supplied
   * subscription.
   *
   * @param subscription a subscription.
   */
  public void invalidateCache(@NonNull Subscription subscription) {
    this.queueCache.keySet().removeIf(subscription::accepts);
  }
}
