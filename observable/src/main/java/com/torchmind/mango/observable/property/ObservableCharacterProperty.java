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
package com.torchmind.mango.observable.property;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provides an observable character primitive property.
 *
 * @author Johannes Donath
 */
public class ObservableCharacterProperty extends AbstractObservableProperty<Character> {

        public ObservableCharacterProperty(@Nullable Character value) {
                super(value);
        }

        /**
         * Retrieves the property value.
         *
         * @return a representation of the currently stored property value.
         */
        public char get() {
                Character value = this.getValue();

                if (value == null) {
                        return 0;
                }

                return value;
        }

        /**
         * Sets the property value.
         *
         * @param value a new value.
         * @return a reference to this property.
         */
        @Nonnull
        public ObservableCharacterProperty set(char value) {
                this.setValue(value);
                return this;
        }
}
