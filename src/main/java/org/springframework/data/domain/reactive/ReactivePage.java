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

import reactor.core.publisher.Mono;

import org.springframework.core.convert.converter.Converter;

/**
 * A page is a subset of a collection of objects. It allows gaining information about the position of it in the containing
 * entire collection.
 *
 * @param <T>
 * @author Mark Paluch
 * @since 1.13
 */
public interface ReactivePage<T> extends ReactiveSlice<T> {

	/**
	 * Returns the number of total pages.
	 *
	 * @return the number of total pages
	 */
	Mono<Integer> getTotalPages();

	/**
	 * Returns the total amount of elements.
	 *
	 * @return the total amount of elements
	 */
	Mono<Long> getTotalElements();

	/**
	 * Returns a new {@link ReactivePage} with the content of the current one mapped by the given {@link Converter}.
	 *
	 * @param converter must not be {@literal null}.
	 * @return a new {@link ReactivePage} with the content of the current one mapped by the given {@link Converter}.
	 * @since 1.10
	 */
	<S> ReactivePage<S> map(Converter<? super T, ? extends S> converter);
}
