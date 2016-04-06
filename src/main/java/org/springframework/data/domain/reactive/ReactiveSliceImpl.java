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

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Pageable;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Default implementation of {@link ReactiveSlice}.
 *
 * @author Mark Paluch
 * @since 1.13
 */
public class ReactiveSliceImpl<T> extends ChunkPublisher<T> {

	private static final long serialVersionUID = 867755909294344406L;

	private final boolean hasNext;
	private final Pageable pageable;

	/**
	 * Creates a new {@link Slice} with the given content and {@link Pageable}.
	 *
	 * @param upstream the upstream publisher of this {@link ReactiveSlice}, must not be {@literal null}.
	 * @param pageable the paging information, can be {@literal null}.
	 * @param hasNext whether there's another slice following the current one.
	 */
	public ReactiveSliceImpl(Flux<T> upstream, Pageable pageable, boolean hasNext) {

		super(upstream, pageable);
		this.hasNext = hasNext;
		this.pageable = pageable;
	}

	/**
	 * Creates a new {@link ReactiveSliceImpl} with the given content. This will result in the created {@link Slice} being
	 * identical to the entire {@link Flux}.
	 *
	 * @param content must not be {@literal null}.
	 */
	public ReactiveSliceImpl(Flux<T> content) {
		this(content, null, false);
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.domain.reactive.ReactiveSlice#hasNext()
	 */
	public Mono<Boolean> hasNext() {
		return Mono.just(hasNext);
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.domain.reactive.ReactiveSlice#map(org.springframework.core.convert.converter.Converter)
	 */
	@Override
	public <S> ReactiveSlice<S> map(Converter<? super T, ? extends S> converter) {
		return new ReactiveSliceImpl<S>(getConvertedContent(converter), pageable, hasNext);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		String contentType = "UNKNOWN";

		return String.format("Slice %d containing %s instances", getNumber(), contentType);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof ReactiveSliceImpl<?>)) {
			return false;
		}

		ReactiveSliceImpl<?> that = (ReactiveSliceImpl<?>) obj;

		return this.hasNext == that.hasNext && super.equals(obj);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		int result = 17;

		result += 31 * (hasNext ? 1 : 0);
		result += 31 * super.hashCode();

		return result;
	}
}
