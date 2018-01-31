// The author disclaims copyright to this source code.
package nl.jvdploeg.rx;

import org.junit.Test;

import io.reactivex.subscribers.TestSubscriber;

public class DefaultPublisherTest {

  @Test
  public void testSubscribe() {
    // given
    final DefaultPublisher<Object> publisher = new DefaultPublisher<>();
    final TestSubscriber<Object> subscriber = new TestSubscriber<>();
    // when
    publisher.subscribe(subscriber);
    // then
    // subscriber.
  }
}
