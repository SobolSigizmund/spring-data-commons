/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.domain.reactive;

import org.reactivestreams.Publisher;

/**
 * A reactive {@link Publisher} for find operations allowing skipping and limiting elements.
 *
 * @author Mark Paluch
 * @since 1.13
 */
public interface FindPublisher<T> extends Publisher<T> {

	/**
	 * Sets the limit to apply.
	 *
	 * @param limit the limit, which may be zero
	 * @return a new {@link FindPublisher} with sorting applied.
	 */
	FindPublisher<T> limit(int limit);

	/**
	 * Sets the number of elements to skip.
	 *
	 * @param skip the number of elements to skip
	 * @return this
	 * @return a new {@link FindPublisher} with sorting applied.
	 */
	FindPublisher<T> skip(int skip);
}
