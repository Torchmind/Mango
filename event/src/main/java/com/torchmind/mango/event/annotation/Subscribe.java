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
package com.torchmind.mango.event.annotation;

import com.torchmind.mango.event.EventBus;
import com.torchmind.mango.event.subscription.Subscription;
import com.torchmind.mango.event.utility.CancelableEvent;
import com.torchmind.reflect.annotation.AliasFor;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Subscribes the annotated method to a set of event types.</p>
 *
 * <p>This annotation is only functional when used in combination with {@link
 * EventBus#register(Object)} and will cause all annotated methods to be registered with the bus
 * automatically.</p>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Documented
@Target(ElementType.METHOD) // Support for meta annotations?
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {

  /**
   * @see #eventType()
   */
  @AliasFor("eventType")
  Class<?>[] value() default DefaultValue.class;

  /**
   * <p>Defines a list of acceptable event types for this subscription.</p>
   *
   * <p>When this value is left on its default (e.g. {@link DefaultValue}), the event type will be
   * inferred from the first and only parameter accepted by the annotated method. When no parameter
   * is present while this property is on its default value, the registration will fail.</p>
   *
   * <p>When the annotated method accepts a parameter, it needs to be a common ancestor of
   * <strong>ALL</strong> accepted event types.</p>
   *
   * @return an array of acceptable event types.
   */
  Class<?>[] eventType() default DefaultValue.class;

  /**
   * <p>Defines the priority of this subscription in relation to other applicable
   * subscriptions.</p>
   *
   * <p>For the purposes of this definition, subscriptions which indicate {@link
   * Subscription#HIGHEST_PRIORITY} as their priority number will be invoked first while those which
   * indicate {@link Subscription#LOWEST_PRIORITY} will be invoked last.</p>
   *
   * <p>When two subscriptions express the same priority, their execution order is undefined.</p>
   *
   * @return a priority number.
   */
  int priority() default 0;

  /**
   * <p>Defines whether this subscription will accept events which have been cancelled by their
   * respective publisher ahead of time or by another subscription with a higher priority.</p>
   *
   * <p>This property only applies to events which implement {@link CancelableEvent} and has no
   * effect otherwise.</p>
   *
   * @return true if cancelled events are accepted, false otherwise.
   */
  boolean acceptCancelled() default false;

  /**
   * Defines whether this subscription will accept event instances which do not match any of the
   * event types exactly.
   *
   * @return true if heirs are accepted, false otherwise.
   */
  boolean acceptHeirs() default true;

  /**
   * Provides a default for class reference properties within this annotation.
   */
  final class DefaultValue {

    private DefaultValue() {
    }
  }
}
