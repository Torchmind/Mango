/*
 * Copyright 2016 Johannes Donath <johannesd@torchmind.com>
 * and other copyright owners as documented in the project's IP log.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.torchmind.mango.concurrency.lock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

/**
 * Provides a few extensions to Java's locks to improve code quality when working with blocking structures.
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Immutable
@ThreadSafe
public interface SmartReadLock extends SmartLock {

        /**
         * Creates a smart read lock instance by wrapping an existing lock.
         * @param lock a lock.
         * @return a wrapped read smart lock.
         */
        @Nonnull
        static SmartReadLock wrap(@Nonnull Lock lock) {
                return new SmartReadLockImpl(lock);
        }

        /**
         * Releases the lock and returns a passed value.
         * @param value a return value.
         * @param <R> a return value type.
         * @return a value.
         * @see #unlock() for a more specific documentation on the unlocking process.
         */
        <R> R unlock(@Nullable R value);

        /**
         * Releases the lock and returns a value generated by the specified supplier.
         * @param supplier a supplier.
         * @param <R> a return value type.
         * @return a value.
         * @see #unlock() for a more specific documentation on the unlocking process.
         */
        <R> R unlock(@Nonnull Supplier<R> supplier);
}