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
package com.torchmind.mango.observable.test;

import com.torchmind.mango.observable.property.ObservableByteProperty;
import com.torchmind.mango.observable.property.ObservableIntegerProperty;
import com.torchmind.mango.observable.property.ObservableProperty;
import com.torchmind.mango.observable.property.ObservablePropertyListener;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Tests methods declared in {@link com.torchmind.mango.observable.property.ObservableProperty} and their corresponding
 * implementations within {@link com.torchmind.mango.observable.property.AbstractObservableProperty}.
 * @author Johannes Donath
 */
@RunWith(MockitoJUnitRunner.class)
public class ObservablePropertyTest {

        /**
         * Tests {@link ObservableProperty#addListener(ObservablePropertyListener)}.
         */
        @Test(expected = ExpectedPropertyUpdateException.class)
        public void testAddListener() {
                ObservableIntegerProperty property = new ObservableIntegerProperty(1);
                property.addListener((ObservablePropertyListener<Integer>) (ob, o, n) -> {
                        throw new ExpectedPropertyUpdateException();
                });

                property.set(2);
        }

        /**
         * Tests {@link ObservableProperty#removeListener(ObservablePropertyListener)}.
         */
        @Test
        public void testRemoveListener() {
                ObservablePropertyListener<Integer> listener = (ob, o, n) -> {
                        throw new AssertionError("Test listener was not removed");
                };

                ObservableIntegerProperty property = new ObservableIntegerProperty(1);
                property.addListener(listener);
                property.removeListener(listener);

                property.set(2);
        }

        /**
         * Tests {@link ObservableProperty#bindTo(ObservableProperty)}.
         */
        @Test
        public void testBindTo() {
                ObservableIntegerProperty property1 = new ObservableIntegerProperty(1);
                ObservableIntegerProperty property2 = new ObservableIntegerProperty(2);

                // validate initial property state before binding to ensure everything is in order
                Assert.assertEquals(1, property1.get());
                Assert.assertEquals(2, property2.get());

                Assert.assertFalse(property1.isBound());
                Assert.assertFalse(property1.isBoundTo(property2));
                Assert.assertFalse(property1.hasBidirectionalBinding());

                // validate post binding state to ensure initial states are synced across as if a real update was
                // applied
                property1.bindTo(property2);

                Assert.assertTrue(property1.isBound());
                Assert.assertTrue(property1.isBoundTo(property2));
                Assert.assertFalse(property1.hasBidirectionalBinding());

                Assert.assertEquals(2, property1.get());
                Assert.assertEquals(2, property2.get());

                // validate value forwarding due to previous binding
                property2.set(3);

                Assert.assertEquals(3, property1.get());
                Assert.assertEquals(3, property2.get());

                // validate value after unbinding
                property1.unbind();

                Assert.assertFalse(property1.isBound());
                Assert.assertFalse(property1.isBoundTo(property2));
                Assert.assertFalse(property1.hasBidirectionalBinding());

                Assert.assertEquals(3, property1.get());
                Assert.assertEquals(3, property2.get());

                // validate independence of properties
                property2.set(4);

                Assert.assertEquals(3, property1.get());
                Assert.assertEquals(4, property2.get());

                property1.set(5);

                Assert.assertEquals(5, property1.get());
                Assert.assertEquals(4, property2.get());
        }

        /**
         * Tests {@link ObservableProperty#bindTo(ObservableProperty)} for its notification properties.
         */
        @Test(expected = ExpectedPropertyUpdateException.class)
        public void testBindToNotification() {
                ObservableIntegerProperty property1 = new ObservableIntegerProperty(1);
                ObservableIntegerProperty property2 = new ObservableIntegerProperty(2);

                property1.bindTo(property2);
                property1.addListener((ObservablePropertyListener<Integer>) (ob, o, n) -> {
                        throw new ExpectedPropertyUpdateException();
                });

                property2.set(3);
        }

        /**
         * Tests {@link ObservableProperty#bindToBidirectionally(ObservableProperty)}.
         */
        @Test
        public void testBindToBidirectionally() {
                ObservableIntegerProperty property1 = new ObservableIntegerProperty(1);
                ObservableIntegerProperty property2 = new ObservableIntegerProperty(2);

                // validate initial property state before binding to ensure everything is in order
                Assert.assertEquals(1, property1.get());
                Assert.assertEquals(2, property2.get());

                Assert.assertFalse(property1.isBound());
                Assert.assertFalse(property1.isBoundTo(property2));
                Assert.assertFalse(property1.hasBidirectionalBinding());

                // validate post binding state to ensure initial states are synced across as if a real update was
                // applied
                property1.bindToBidirectionally(property2);

                Assert.assertTrue(property1.isBound());
                Assert.assertTrue(property1.isBoundTo(property2));
                Assert.assertTrue(property1.hasBidirectionalBinding());

                Assert.assertEquals(2, property1.get());
                Assert.assertEquals(2, property2.get());

                // verify value forwarding in both directions
                property1.set(3);
                Assert.assertEquals(3, property1.get());
                Assert.assertEquals(3, property2.get());

                property2.set(4);
                Assert.assertEquals(4, property1.get());
                Assert.assertEquals(4, property2.get());

                // validate values after unbinding
                property1.unbind();

                Assert.assertFalse(property1.isBound());
                Assert.assertFalse(property1.isBoundTo(property2));
                Assert.assertFalse(property1.hasBidirectionalBinding());

                Assert.assertEquals(4, property1.get());
                Assert.assertEquals(4, property2.get());

                // verify independent modification after unbinding
                property1.set(5);
                Assert.assertEquals(5, property1.get());
                Assert.assertEquals(4, property2.get());

                property2.set(6);
                Assert.assertEquals(5, property1.get());
                Assert.assertEquals(6, property2.get());
        }

        /**
         * Tests {@link ObservableProperty#getValue()} and {@link ObservableProperty#setValue(Object)}.
         */
        @Test
        public void testGetSetValue() {
                ObservableIntegerProperty property = new ObservableIntegerProperty(1);

                Assert.assertEquals(1, property.get());
                Assert.assertEquals(property, property.set(2));
                Assert.assertEquals(2, property.get());
        }

        /**
         * Tests {@link ObservableProperty#equals(Object)}.
         */
        @Test
        public void testEquals() {
                ObservableIntegerProperty property1 = new ObservableIntegerProperty(1);
                ObservableIntegerProperty property2 = new ObservableIntegerProperty(1);
                ObservableIntegerProperty property3 = new ObservableIntegerProperty(2);

                Assert.assertTrue(property1.equals(property1));
                Assert.assertTrue(property1.equals(property2));
                Assert.assertFalse(property1.equals(property3));
                Assert.assertFalse(property1.equals(null));
        }

        /**
         * Tests {@link ObservableProperty#hashCode()}.
         */
        @Test
        public void testHashCode() {
                ObservableIntegerProperty property1 = new ObservableIntegerProperty(1);
                ObservableIntegerProperty property2 = new ObservableIntegerProperty(1);

                Assert.assertEquals(property1.hashCode(), property1.hashCode());
                Assert.assertEquals(property1.hashCode(), property2.hashCode());
        }

        /**
         * Provides an exception for communicating with the test framework.
         */
        public static class ExpectedPropertyUpdateException extends RuntimeException {

        }
}
