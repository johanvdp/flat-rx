// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.rx;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Subscription;

import mockit.Mocked;
import mockit.Verifications;
import nl.jvdploeg.flat.Model;
import nl.jvdploeg.flat.Path;
import nl.jvdploeg.flat.impl.DefaultChange;

public class ModelSubscriberTest {

  @Mocked
  private Model<?> model;
  private ModelSubscriber subscriber;
  @Mocked
  private Subscription subscription;

  @After
  public void after() throws Exception {
  }

  @Before
  public void before() {
    subscriber = new ModelSubscriber(model);
  }

  @Test
  public void testOnComplete() {
    // given
    subscriber.onSubscribe(subscription);
    // when
    subscriber.onComplete();
    // then
    try {
      // can not send next
      subscriber.onNext(DefaultChange.add(new Path("A")));
      Assert.fail("exception expected");
    } catch (final Exception e) {
      // expected
    }
  }

  @Test
  public void testOnError() {
    // given
    subscriber.onSubscribe(subscription);
    // when
    subscriber.onError(new Throwable("message"));
    // then
    try {
      // can not send next
      subscriber.onNext(DefaultChange.add(new Path("A")));
      Assert.fail("exception expected");
    } catch (final Exception e) {
      // expected
    }
  }

  @Test
  @SuppressWarnings("unused")
  public void testOnNext() {
    // given
    subscriber.onSubscribe(subscription);
    // when
    subscriber.onNext(DefaultChange.add(new Path("A")));
    // then
    new Verifications() {

      {
        // affect model
        final Path path;
        model.add(path = withCapture());
        Assert.assertEquals(new Path("A"), path);
        // request one
        final long n;
        subscription.request(n = ((Long) withCapture()).longValue());
        Assert.assertEquals(1, n);
      }
    };
  }

  @Test
  @SuppressWarnings("unused")
  public void testOnSubscribe() {
    // when
    subscriber.onSubscribe(subscription);
    // then
    new Verifications() {

      {
        // request one
        final long n;
        subscription.request(n = ((Long) withCapture()).longValue());
        Assert.assertEquals(1, n);
      }
    };
  }
}
