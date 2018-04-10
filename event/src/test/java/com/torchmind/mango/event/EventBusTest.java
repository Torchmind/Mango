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

import com.torchmind.mango.event.annotation.Subscribe;
import com.torchmind.mango.event.subscription.Subscription;
import com.torchmind.mango.event.subscription.SubscriptionHandle;
import com.torchmind.mango.event.subscription.filter.EventFilter;
import com.torchmind.mango.event.utility.CancelableEvent;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

/**
 * Evaluates whether the event bus correctly handles subscriptions and broadcasts.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class EventBusTest {

  private EventBus eventBus;

  /**
   * Prepares the instance for upcoming tests.
   */
  @Before
  public void prepare() {
    this.eventBus = new EventBus();
  }

  /**
   * Evaluates whether the event bus correctly registers subscriptions and invokes
   */
  @Test
  public void testPost() {
    Subscription subscriptionA = Mockito.mock(Subscription.class);
    Subscription subscriptionB = Mockito.mock(Subscription.class);
    Subscription subscriptionC = Mockito.mock(Subscription.class);
    Subscription subscriptionD = Mockito.mock(Subscription.class);
    Subscription subscriptionE = Mockito.mock(Subscription.class);

    Mockito.when(subscriptionA.accepts(Mockito.any())).thenReturn(true);
    Mockito.when(subscriptionA.accepts(Mockito.<Object>any())).thenReturn(true);
    Mockito.when(subscriptionB.accepts(Mockito.any())).thenReturn(true);
    Mockito.when(subscriptionB.accepts(Mockito.<Object>any())).thenReturn(true);
    Mockito.when(subscriptionC.accepts(Mockito.any())).thenReturn(true);
    Mockito.when(subscriptionC.accepts(Mockito.<Object>any())).thenReturn(true);
    Mockito.when(subscriptionD.accepts(Mockito.any())).thenReturn(false);
    Mockito.when(subscriptionD.accepts(Mockito.<Object>any())).thenReturn(true);
    Mockito.when(subscriptionE.accepts(Mockito.any())).thenReturn(true);
    Mockito.when(subscriptionE.accepts(Mockito.<Object>any())).thenReturn(false);

    Mockito.when(subscriptionA.getPriority()).thenReturn(-1);
    Mockito.when(subscriptionB.getPriority()).thenReturn(0);
    Mockito.when(subscriptionC.getPriority()).thenReturn(1);

    this.eventBus.register(subscriptionB);
    this.eventBus.register(subscriptionC);
    this.eventBus.register(subscriptionA);

    Object event = new Object();
    this.eventBus.post(event);

    InOrder order = Mockito.inOrder(subscriptionA, subscriptionB, subscriptionC);

    order.verify(subscriptionA, Mockito.calls(1)).handle(event);
    order.verify(subscriptionB, Mockito.calls(1)).handle(event);
    order.verify(subscriptionC, Mockito.calls(1)).handle(event);
    order.verifyNoMoreInteractions();

    Mockito.verify(subscriptionD, Mockito.never()).handle(Mockito.any());
    Mockito.verify(subscriptionE, Mockito.never()).handle(Mockito.any());
  }

  /**
   * Evaluates whether the event bus correctly removes subscriptions from its backing registry.
   */
  @Test
  public void testUnsubscribe() {
    Subscription subscription = Mockito.mock(Subscription.class);

    Mockito.when(subscription.accepts(Mockito.any())).thenReturn(true);
    Mockito.when(subscription.accepts((Object) Mockito.any())).thenReturn(true);

    this.eventBus.register(subscription);

    Object event = new Object();
    this.eventBus.post(event);

    Mockito.verify(subscription, Mockito.times(1)).handle(event);

    this.eventBus.unregister(subscription);
    Mockito.reset(subscription);
    this.eventBus.post(event);

    Mockito.verifyZeroInteractions(subscription);
  }

  /**
   * Evaluates whether the event bus correctly wraps consumers and passes events to them.
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testConsumer() {
    Consumer<Object> consumer = Mockito.mock(Consumer.class);

    this.eventBus.createSubscription()
        .register(consumer);

    Object event = new Object();
    this.eventBus.post(event);

    Mockito.verify(consumer, Mockito.times(1)).accept(event);
  }

  /**
   * Evaluates whether the event bus correctly wraps a runnable and passes events to it and whether
   * the returned handle correctly removes and restores the registration.
   */
  @Test
  public void testRunnable() {
    Runnable runnable = Mockito.mock(Runnable.class);

    SubscriptionHandle handle = this.eventBus.createSubscription()
        .register(runnable);

    Assert.assertNotNull(handle);
    Assert.assertTrue(handle.isActive());

    Object event = new Object();
    this.eventBus.post(event);

    Mockito.verify(runnable, Mockito.times(1)).run();

    Mockito.reset(runnable);
    Mockito.clearInvocations(runnable);

    handle.remove();
    Assert.assertFalse(handle.isActive());

    this.eventBus.post(event);

    Mockito.verifyZeroInteractions(runnable);

    handle.restore();
    Assert.assertTrue(handle.isActive());

    this.eventBus.post(event);

    Mockito.verify(runnable, Mockito.times(1)).run();
  }

  /**
   * Evaluates whether the event bus respects the decisions of a custom event filter.
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testFilter() {
    Consumer<Object> consumerA = Mockito.mock(Consumer.class);
    Consumer<Object> consumerB = Mockito.mock(Consumer.class);

    Object eventA = new Object();
    Object eventB = new Object();
    Object eventC = new Object();

    EventFilter filterA = Mockito.mock(EventFilter.class);
    EventFilter filterB = Mockito.mock(EventFilter.class);

    Mockito.when(filterA.accepts(Object.class)).thenReturn(true);
    Mockito.when(filterA.accepts(eventA)).thenReturn(true);
    Mockito.when(filterA.accepts(eventB)).thenReturn(false);
    Mockito.when(filterA.accepts(eventC)).thenReturn(true);
    Mockito.when(filterB.accepts(Object.class)).thenReturn(true);
    Mockito.when(filterB.accepts(eventA)).thenReturn(false);
    Mockito.when(filterB.accepts(eventB)).thenReturn(true);
    Mockito.when(filterB.accepts(eventC)).thenReturn(true);

    this.eventBus.createSubscription()
        .withFilter(filterA)
        .register(consumerA);
    this.eventBus.createSubscription()
        .withFilter(filterB)
        .register(consumerB);

    this.eventBus.post(eventA);
    this.eventBus.post(eventB);
    this.eventBus.post(eventC);

    Mockito.verify(consumerA, Mockito.times(1)).accept(eventA);
    Mockito.verify(consumerA, Mockito.never()).accept(eventB);
    Mockito.verify(consumerA, Mockito.times(1)).accept(eventC);

    Mockito.verify(consumerB, Mockito.never()).accept(eventA);
    Mockito.verify(consumerB, Mockito.times(1)).accept(eventB);
    Mockito.verify(consumerB, Mockito.times(1)).accept(eventC);

    Mockito.verifyNoMoreInteractions(
        consumerA,
        consumerB
    );
  }

  /**
   * Evaluates whether the event bus correctly generates a subscription for multiple methods within
   * a listener.
   */
  @Test
  public void testSubscribeObject() {
    TestListener listener = Mockito.mock(TestListener.class);

    this.eventBus.register(listener);

    Object eventA = new Object();
    CancelableEvent eventB = Mockito.mock(CancelableEvent.class);
    String eventC = "Test";

    Mockito.when(eventB.isCancelled()).thenReturn(true);

    this.eventBus.post(eventA);

    InOrder order = Mockito.inOrder(listener);

    order.verify(listener, Mockito.calls(1)).runnableSubscriptionA();
    order.verify(listener, Mockito.calls(1)).runnableSubscriptionB();
    order.verify(listener, Mockito.calls(1)).consumerSubscriptionA(eventA);
    order.verify(listener, Mockito.calls(1)).consumerSubscriptionB(eventA);
    order.verify(listener, Mockito.calls(1)).consumerSubscriptionC(eventA);
    order.verify(listener, Mockito.calls(1)).consumerSubscriptionD(eventA);
    order.verifyNoMoreInteractions();

    Mockito.clearInvocations(listener);

    this.eventBus.post(eventB);

    order.verify(listener, Mockito.calls(1)).consumerSubscriptionC(eventB);
    order.verifyNoMoreInteractions();

    Mockito.clearInvocations(listener);

    this.eventBus.post(eventC);

    order.verify(listener, Mockito.calls(1)).runnableSubscriptionA();
    order.verify(listener, Mockito.calls(1)).runnableSubscriptionB();
    order.verify(listener, Mockito.calls(1)).consumerSubscriptionA(eventC);
    order.verify(listener, Mockito.calls(1)).consumerSubscriptionB(eventC);
    order.verify(listener, Mockito.calls(1)).consumerSubscriptionC(eventC);
    order.verifyNoMoreInteractions();
  }

  /**
   * Evaluates whether the implementation correctly creates a child bus and passes its events
   * along.
   */
  @Test
  public void testChild() {
    Subscription subscriptionA = Mockito.mock(Subscription.class);
    Subscription subscriptionB = Mockito.mock(Subscription.class);
    Subscription subscriptionC = Mockito.mock(Subscription.class);
    Subscription subscriptionD = Mockito.mock(Subscription.class);
    Subscription subscriptionE = Mockito.mock(Subscription.class);
    Subscription subscriptionF = Mockito.mock(Subscription.class);

    Mockito.when(subscriptionA.accepts(Mockito.any())).thenReturn(true);
    Mockito.when(subscriptionA.accepts(Mockito.<Object>any())).thenReturn(true);
    Mockito.when(subscriptionB.accepts(Mockito.any())).thenReturn(true);
    Mockito.when(subscriptionB.accepts(Mockito.<Object>any())).thenReturn(true);
    Mockito.when(subscriptionC.accepts(Mockito.any())).thenReturn(true);
    Mockito.when(subscriptionC.accepts(Mockito.<Object>any())).thenReturn(true);
    Mockito.when(subscriptionD.accepts(Mockito.any())).thenReturn(true);
    Mockito.when(subscriptionD.accepts(Mockito.<Object>any())).thenReturn(true);
    Mockito.when(subscriptionE.accepts(Mockito.any())).thenReturn(true);
    Mockito.when(subscriptionE.accepts(Mockito.<Object>any())).thenReturn(true);
    Mockito.when(subscriptionF.accepts(Mockito.any())).thenReturn(true);
    Mockito.when(subscriptionF.accepts(Mockito.<Object>any())).thenReturn(true);

    EventBus child = this.eventBus.createChild();

    Assert.assertNotNull(child);
    Assert.assertNotEquals(this.eventBus, child);

    this.eventBus.register(subscriptionA);
    this.eventBus.register(subscriptionB);
    this.eventBus.register(subscriptionC);
    child.register(subscriptionD);
    child.register(subscriptionE);
    child.register(subscriptionF);

    Object event = new Object();
    this.eventBus.post(event); // no broadcast

    Mockito.verify(subscriptionA, Mockito.times(1)).handle(event);
    Mockito.verify(subscriptionB, Mockito.times(1)).handle(event);
    Mockito.verify(subscriptionC, Mockito.times(1)).handle(event);
    Mockito.verify(subscriptionD, Mockito.never()).handle(Mockito.any());
    Mockito.verify(subscriptionE, Mockito.never()).handle(Mockito.any());
    Mockito.verify(subscriptionF, Mockito.never()).handle(Mockito.any());

    Mockito.clearInvocations(
        subscriptionA,
        subscriptionB,
        subscriptionC,
        subscriptionD,
        subscriptionE,
        subscriptionF
    );

    this.eventBus.broadcast(event);

    Mockito.verify(subscriptionA, Mockito.times(1)).handle(event);
    Mockito.verify(subscriptionB, Mockito.times(1)).handle(event);
    Mockito.verify(subscriptionC, Mockito.times(1)).handle(event);
    Mockito.verify(subscriptionD, Mockito.times(1)).handle(event);
    Mockito.verify(subscriptionE, Mockito.times(1)).handle(event);
    Mockito.verify(subscriptionF, Mockito.times(1)).handle(event);

    Mockito.clearInvocations(
        subscriptionA,
        subscriptionB,
        subscriptionC,
        subscriptionD,
        subscriptionE,
        subscriptionF
    );

    child.broadcast(event);

    Mockito.verify(subscriptionA, Mockito.never()).handle(Mockito.any());
    Mockito.verify(subscriptionB, Mockito.never()).handle(Mockito.any());
    Mockito.verify(subscriptionC, Mockito.never()).handle(Mockito.any());
    Mockito.verify(subscriptionD, Mockito.times(1)).handle(event);
    Mockito.verify(subscriptionE, Mockito.times(1)).handle(event);
    Mockito.verify(subscriptionF, Mockito.times(1)).handle(event);
  }

  public interface TestListener {

    @Subscribe(eventType = Object.class, priority = -1)
    void runnableSubscriptionA();

    @Subscribe(Object.class)
    void runnableSubscriptionB();

    @Subscribe(priority = 1)
    void consumerSubscriptionA(@NonNull Object event);

    @Subscribe(value = Object.class, priority = 2)
    void consumerSubscriptionB(@NonNull Object event);

    @Subscribe(acceptCancelled = true, priority = 3)
    void consumerSubscriptionC(@NonNull Object event);

    @Subscribe(acceptHeirs = false, priority = 4)
    void consumerSubscriptionD(@NonNull Object event);
  }
}
