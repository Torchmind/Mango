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
package com.torchmind.mango.event;

import com.torchmind.mango.concurrency.lock.FunctionalReadWriteLock;
import com.torchmind.mango.event.annotation.Subscribe;
import com.torchmind.mango.event.annotation.Subscribe.DefaultValue;
import com.torchmind.mango.event.internal.FunctionalFactory;
import com.torchmind.mango.event.subscription.ConsumerSubscription;
import com.torchmind.mango.event.subscription.RunnableSubscription;
import com.torchmind.mango.event.subscription.Subscription;
import com.torchmind.mango.event.subscription.SubscriptionHandle;
import com.torchmind.mango.event.subscription.SubscriptionRegistry;
import com.torchmind.mango.event.subscription.filter.EventFilter;
import com.torchmind.reflect.AnnotationUtility;
import com.torchmind.reflect.ReflectionUtility;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/**
 * Provides an event bus which keeps track of event subscriptions and permits the broadcasting of
 * events to various listeners.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class EventBus {

  private final FunctionalReadWriteLock lock = FunctionalReadWriteLock
      .wrap(new ReentrantReadWriteLock());

  private final EventBus parent;
  private final Set<EventBus> children = Collections.newSetFromMap(new WeakHashMap<>());
  private final SubscriptionRegistry registry = new SubscriptionRegistry();

  public EventBus() {
    this.parent = null;
  }

  protected EventBus(@NonNull EventBus parent) {
    this.parent = parent;
  }

  /**
   * <p>Creates a child event bus which will be notified about all events which are broadcasted
   * within this bus as well as its own events.</p>
   *
   * <p>Child buses are weakly referenced and thus it is the caller's sole responsibility to keep
   * child buses strongly referenced. If a bus is no longer strongly referenced, it will eventually
   * stop broadcasting events to its listeners.</p>
   *
   * @return a child event bus.
   */
  @NonNull
  public EventBus createChild() {
    EventBus eventBus = new EventBus(this);
    this.registerChild(eventBus);
    return eventBus;
  }

  /**
   * <p>Registers a new child event bus.</p>
   *
   * <p>Note that child buses should <strong>always</strong> be constructed using the {@link
   * #EventBus(EventBus)} constructor in order to inform them of their parent.</p>
   *
   * @param eventBus a child event bus.
   */
  protected final void registerChild(@NonNull EventBus eventBus) {
    this.lock.writeLock().runProtected(() -> this.children.add(eventBus));
  }

  /**
   * <p>Broadcasts an event to all listeners which expressed their interest in this event type as as
   * well as all child event buses.</p>
   *
   * <p>Note that listeners within this bus will automatically gain precedence over any listeners
   * within child event buses and will thus be called first regardless of their order relative to
   * child listeners.</p>
   *
   * @param event an event.
   * @see #post(Object)
   */
  public void broadcast(@NonNull Object event) {
    this.lock.readLock().runProtected(() -> {
      this.doPost(event);
      this.children.forEach((child) -> child.broadcast(event));
    });
  }

  /**
   * <p>Posts an event to all listeners which expressed their interest in this event type.</p>
   *
   * <p>Note that this method will not notify any child buses.</p>
   *
   * @param event an event.
   * @see #broadcast(Object)
   */
  public void post(@NonNull Object event) {
    this.lock.readLock().runProtected(() -> this.doPost(event));
  }

  /**
   * @see #post(Object)
   */
  private void doPost(@NonNull Object event) {
    this.registry.getSubscriptions(event.getClass()).forEach((subscription) -> {
      if (!subscription.accepts(event)) {
        return;
      }

      subscription.handle(event);
    });
  }

  /**
   * Creates a new empty subscription builder.
   *
   * @return an empty builder.
   */
  @NonNull
  public SubscriptionBuilder createSubscription() {
    return new SubscriptionBuilder();
  }

  /**
   * Registers an arbitrarily defined subscription with this event bus.
   *
   * @param subscription a subscription.
   */
  public void register(@NonNull Subscription subscription) {
    this.lock.writeLock().runProtected(() -> this.registry.register(subscription));
  }

  /**
   * Registers all annotated handlers within a given object with this event bus.
   *
   * @param object an arbitrary object.
   * @return a subscription handle.
   */
  @NonNull
  public SubscriptionHandle register(@NonNull Object object) {
    Set<SubscriptionHandle> handles = new HashSet<>();

    ReflectionUtility.getUniqueMethods(object.getClass()).forEach((method) -> {
      Subscribe annotation = method.getAnnotation(Subscribe.class);

      if (annotation == null) {
        return;
      }
      annotation = AnnotationUtility.decorate(annotation);

      Class<?>[] eventTypes;

      if (method.getParameterCount() > 1) {
        throw new IllegalArgumentException("Illegal subscription for method " + method
            + ": Expecting exactly zero or one parameter");
      }

      if (annotation.eventType().length == 0) {
        throw new IllegalArgumentException(
            "Illegal subscription for method " + method + ": Expected one or more event types");
      }

      if (annotation.eventType()[0] != DefaultValue.class) {
        eventTypes = annotation.eventType();
      } else {
        if (method.getParameterCount() != 1) {
          throw new IllegalArgumentException(
              "Illegal subscription for method " + method + ": Cannot infer accepted event type");
        }

        eventTypes = new Class[]{method.getParameterTypes()[0]};
      }

      SubscriptionBuilder builder = new SubscriptionBuilder()
          .withFilter(
              new EventFilter.Builder()
                  .withEventType(eventTypes)
                  .withAcceptCancelled(annotation.acceptCancelled())
                  .withAcceptHeirs(annotation.acceptHeirs())
                  .build()
          )
          .withPriority(annotation.priority());

      if (method.getParameterCount() == 0) {
        handles.add(builder.register(FunctionalFactory.createRunnable(object, method)));
      } else {
        handles.add(builder.register(FunctionalFactory.createConsumer(object, method)));
      }
    });

    return new CollectionSubscriptionHandle(handles);
  }

  /**
   * Evaluates whether the indicated subscription is currently registered with this bus.
   *
   * @param subscription a subscription.
   * @return true if registered, false otherwise.
   */
  public boolean isRegistered(@NonNull Subscription subscription) {
    return this.lock.readLock().runProtected(() -> this.registry.isRegistered(subscription));
  }

  /**
   * Removes an arbitrarily defined subscription from this event bus.
   *
   * @param subscription a subscription.
   */
  public void unregister(@NonNull Subscription subscription) {
    this.lock.writeLock().runProtected(() -> this.registry.unregister(subscription));
  }

  /**
   * Provides a factory for subscription registrations.
   */
  public final class SubscriptionBuilder {

    private EventFilter filter = EventFilter.DEFAULT_FILTER;
    private int priority;

    private SubscriptionBuilder() {
    }

    /**
     * Registers a new runnable with the event bus using the configuration within this factory.
     *
     * @param runnable a runnable.
     * @return a subscription handle.
     */
    @NonNull
    public SubscriptionHandle register(@NonNull Runnable runnable) {
      Subscription subscription = new RunnableSubscription(runnable, this.filter, this.priority);
      EventBus.this.register(subscription);
      return new SubscriptionHandleImpl(subscription);
    }

    /**
     * <p>Registers a new consumer with the event bus using the configuration within this
     * factory.</p>
     *
     * <p><strong>Warning:</strong> The caller is responsible for configuring a filter which is
     * compatible with the bounds of the supplied consumer.</p>
     *
     * @param consumer a consumer.
     * @return a subscription handle.
     */
    @NonNull
    public SubscriptionHandle register(@NonNull Consumer<?> consumer) {
      Subscription subscription = new ConsumerSubscription(consumer, this.filter, this.priority);
      EventBus.this.register(subscription);
      return new SubscriptionHandleImpl(subscription);
    }

    /**
     * <p>Registers a new method with the event bus using the configuration within this
     * factory.</p>
     *
     * <p><strong>Warning:</strong> The caller is responsible for configuring a filter which is
     * compatible with the bounds of the supplied method.</p>
     *
     * @param instance an object instance.
     * @param method a method.
     * @return a subscription handle.
     * @throws IllegalArgumentException when the method definition is incompatible with this event
     * bus.
     */
    @NonNull
    public SubscriptionHandle register(@Nullable Object instance, @NonNull Method method) {
      if (method.getParameterCount() == 0) {
        return this.register(FunctionalFactory.createRunnable(instance, method));
      }

      if (method.getParameterCount() == 1) {
        return this.register(FunctionalFactory.createConsumer(instance, method));
      }

      throw new IllegalArgumentException(
          "Illegal method subscription: Expected zero or one parameters");
    }

    /**
     * Selects a filter which decides whether or not to the subscription can and will handle an
     * event of a given type.
     *
     * @param filter a filter.
     * @return a reference to this builder.
     */
    @NonNull
    public SubscriptionBuilder withFilter(@Nullable EventFilter filter) {
      if (filter == null) {
        filter = EventFilter.DEFAULT_FILTER;
      }

      this.filter = filter;
      return this;
    }

    /**
     * Selects a relative priority which defines the order in which the subscription will be
     * executed when multiple subscriptions qualify.
     *
     * @param priority a priority.
     * @return a reference to this builder.
     */
    @NonNull
    public SubscriptionBuilder withPriority(int priority) {
      this.priority = priority;
      return this;
    }
  }

  /**
   * Provides a wrapper around collections of subscription handles.
   */
  private final class CollectionSubscriptionHandle implements SubscriptionHandle {

    private final Set<SubscriptionHandle> handles;

    private CollectionSubscriptionHandle(@NonNull Set<SubscriptionHandle> handles) {
      this.handles = handles;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public EventBus getEventBus() {
      return EventBus.this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isActive() {
      return this.handles.stream().allMatch(SubscriptionHandle::isActive);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove() {
      this.handles.forEach(SubscriptionHandle::remove);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void restore() {
      this.handles.forEach(SubscriptionHandle::restore);
    }
  }

  /**
   * Provides a standard subscription handle.
   */
  private final class SubscriptionHandleImpl implements SubscriptionHandle {

    private final Subscription subscription;

    private SubscriptionHandleImpl(@NonNull Subscription subscription) {
      this.subscription = subscription;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public EventBus getEventBus() {
      return EventBus.this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isActive() {
      return EventBus.this.isRegistered(this.subscription);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove() {
      EventBus.this.unregister(this.subscription);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void restore() {
      EventBus.this.register(this.subscription);
    }
  }
}
