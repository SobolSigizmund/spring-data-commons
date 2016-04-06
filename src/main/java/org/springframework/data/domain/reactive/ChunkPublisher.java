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

import java.io.Serializable;
import java.util.function.Function;

import org.reactivestreams.Subscriber;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * A chunk of data restricted by the configured {@link Pageable}.
 *
 * @author Mark Paluch
 * @since 1.8
 */
abstract class ChunkPublisher<T> implements ReactiveSlice<T>, Serializable {

	private static final long serialVersionUID = 867755909294344406L;

	private final Flux<T> upstream;
	private final Pageable pageable;

	/**
	 * Creates a new {@link ChunkPublisher} with the given content and the given governing {@link Pageable}.
	 *
	 * @param upstream the upstream publisher of this chunk, must not be {@literal null}.
	 * @param pageable can be {@literal null}.
	 */
	public ChunkPublisher(Flux<T> upstream, Pageable pageable) {

		Assert.notNull(upstream, "Flux must not be null!");

		this.upstream = upstream;
		this.pageable = pageable;
	}

	/* (non-Javadoc)
	 * @see org.reactivestreams.Publisher#subscribe(org.reactivestreams.Subscriber)
	 */
	@Override
	public void subscribe(Subscriber<? super T> s) {
		upstream.subscribe(s);
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.domain.reactive.ReactiveSlice#getNumber()
	 */
	public int getNumber() {
		return pageable == null ? 0 : pageable.getPageNumber();
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.domain.reactive.ReactiveSlice#getSize()
	 */
	public int getSize() {
		return pageable == null ? 0 : pageable.getPageSize();
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.domain.reactive.ReactiveSlice#count()
	 */
	@Override
	public Mono<Long> count() {
		return upstream.count();
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.domain.reactive.ReactiveSlice#hasPrevious()
	 */
	public Mono<Boolean> hasPrevious() {
		return Mono.just(getNumber() > 0);
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.domain.reactive.ReactiveSlice#isFirst()
	 */
	public Mono<Boolean> isFirst() {
		return hasPrevious().map(b -> !b);
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.domain.reactive.ReactiveSlice#isLast()
	 */
	public Mono<Boolean> isLast() {
		return hasNext().map(b -> !b);
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.domain.reactive.ReactiveSlice#nextPageable()
	 */
	public Mono<Pageable> nextPageable() {

		return hasNext().flatMap(new Function<Boolean, Mono<Pageable>>() {
			@Override
			public Mono<Pageable> apply(Boolean b) {
				return b ? Mono.just(pageable.next()) : Mono.empty();
			}
		}).next();
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.domain.reactive.ReactiveSlice#previousPageable()
	 */
	public Mono<Pageable> previousPageable() {

		return hasPrevious().flatMap(new Function<Boolean, Mono<Pageable>>() {
			@Override
			public Mono<Pageable> apply(Boolean b) {
				return b ? Mono.just(pageable.previousOrFirst()) : Mono.empty();
			}
		}).next();
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.domain.reactive.ReactiveSlice#hasElements()
	 */
	@Override
	public Mono<Boolean> hasElements() {
		return upstream.hasElements();
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.domain.reactive.ReactiveSlice#getSort()
	 */
	public Sort getSort() {
		return pageable == null ? null : pageable.getSort();
	}

	/**
	 * Applies the given {@link Converter} to the content of the {@link ChunkPublisher}.
	 *
	 * @param converter must not be {@literal null}.
	 * @return
	 */
	protected <S> Flux<S> getConvertedContent(final Converter<? super T, ? extends S> converter) {

		Assert.notNull(converter, "Converter must not be null!");
		return upstream.map(new Function<T, S>() {
			public S apply(T element) {
				return converter.convert(element);
			}
		});
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

		if (!(obj instanceof ChunkPublisher<?>)) {
			return false;
		}

		ChunkPublisher<?> that = (ChunkPublisher<?>) obj;

		boolean contentEqual = this.upstream.equals(that.upstream);
		boolean pageableEqual = this.pageable == null ? that.pageable == null : this.pageable.equals(that.pageable);

		return contentEqual && pageableEqual;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		int result = 17;

		result += 31 * (pageable == null ? 0 : pageable.hashCode());
		result += 31 * upstream.hashCode();

		return result;
	}

}
