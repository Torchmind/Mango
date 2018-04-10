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
package com.torchmind.mango.event.internal;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

/**
 * Provides utility methods for wrapping arbitrary methods in functionals.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public final class FunctionalFactory {

  private FunctionalFactory() {
  }

  /**
   * Wraps the indicated method in a runnable functional.
   *
   * @param instance an instance or, if the method is static, null.
   * @param method a method.
   * @return a runnable.
   */
  @NonNull
  public static Runnable createRunnable(@Nullable Object instance, @NonNull Method method) {
    boolean isStatic = Modifier.isStatic(method.getModifiers());

    if (isStatic != (instance == null)) {
      throw new IllegalArgumentException(
          "Illegal method call: " + (isStatic ? "Cannot call static method from instance context"
              : "Cannot call virtual method from static context"));
    }

    if (method.getParameterCount() != 0) {
      throw new IllegalArgumentException(
          "Illegal method call: Expected zero arguments but method requires " + method
              .getParameterCount());
    }

    try {
      MethodHandles.Lookup caller = MethodHandles.lookup();
      MethodHandle handle = caller.unreflect(method);
      MethodType type = handle.type();
      MethodType factoryType = MethodType.methodType(Runnable.class);

      if (instance != null) {
        type = type.dropParameterTypes(0, 1);
        factoryType = factoryType.appendParameterTypes(method.getDeclaringClass());
      }

      CallSite site = LambdaMetafactory.metafactory(
          caller,
          "run",
          factoryType,
          type,
          handle,
          type
      );

      try {
        if (instance != null) {
          return (Runnable) site.getTarget().invoke(instance);
        }

        return (Runnable) site.getTarget().invoke();
      } catch (Throwable ex) {
        throw new IllegalArgumentException("Failed to construct runnable for method: " + method,
            ex);
      }
    } catch (IllegalAccessException ex) {
      throw new IllegalArgumentException("Cannot access method: " + method, ex);
    } catch (LambdaConversionException ex) {
      throw new IllegalArgumentException("Failed to convert method to lambda: " + method, ex);
    }
  }

  /**
   * Wraps the indicated method in a consumer functional.
   *
   * @param instance an instance or, if the method is static, null.
   * @param method a method.
   * @return a consumer.
   */
  @SuppressWarnings("unchecked")
  public static <I> Consumer<I> createConsumer(@Nullable Object instance, @NonNull Method method) {
    boolean isStatic = Modifier.isStatic(method.getModifiers());

    if (isStatic != (instance == null)) {
      throw new IllegalArgumentException(
          "Illegal method call: " + (isStatic ? "Cannot call static method from instance context"
              : "Cannot call virtual method from static context"));
    }

    if (method.getParameterCount() != 1) {
      throw new IllegalArgumentException(
          "Illegal method call: Expected one argument but method requires " + method
              .getParameterCount());
    }

    try {
      MethodHandles.Lookup caller = MethodHandles.lookup();
      MethodHandle handle = caller.unreflect(method);
      MethodType type = handle.type();
      MethodType factoryType = MethodType.methodType(Consumer.class);

      if (instance != null) {
        type = type.dropParameterTypes(0, 1);
        factoryType = factoryType.appendParameterTypes(method.getDeclaringClass());
      }

      CallSite site = LambdaMetafactory.metafactory(
          caller,
          "accept",
          factoryType,
          type.changeParameterType(0, Object.class),
          handle,
          type
      );

      try {
        if (instance != null) {
          return (Consumer<I>) site.getTarget().invoke(instance);
        }

        return (Consumer<I>) site.getTarget().invoke();
      } catch (Throwable ex) {
        throw new IllegalArgumentException("Failed to construct runnable for method: " + method,
            ex);
      }
    } catch (IllegalAccessException ex) {
      throw new IllegalArgumentException("Cannot access method: " + method, ex);
    } catch (LambdaConversionException ex) {
      throw new IllegalArgumentException("Failed to convert method to lambda: " + method, ex);
    }
  }
}
