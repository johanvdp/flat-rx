// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.rx;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.reactivex.subscribers.TestSubscriber;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import nl.jvdploeg.flat.Change;
import nl.jvdploeg.flat.Model;
import nl.jvdploeg.flat.Node;
import nl.jvdploeg.flat.Path;
import nl.jvdploeg.flat.Version;
import nl.jvdploeg.flat.impl.DefaultChange;
import nl.jvdploeg.flat.impl.DefaultModel;
import nl.jvdploeg.flat.impl.Enforce;
import nl.jvdploeg.flat.impl.NumberedVersion;

public class RxModelTest {

  private static final Model<?> SIMPLE_MODEL = createModel();

  private static Model<?> createModel() {
    final Model<?> model = new DefaultModel("createModel", Enforce.LENIENT);
    final Path pathChildA = new Path("A");
    model.createChild(pathChildA);
    model.setValue(pathChildA, "a");
    model.setVersion(pathChildA, new NumberedVersion(2));
    final Path pathChildB = pathChildA.createChildPath("B");
    model.createChild(pathChildB);
    model.setValue(pathChildB, "b");
    model.setVersion(pathChildB, new NumberedVersion(3));
    return model;
  }

  @Mocked
  private Model<?> backing;
  private RxModel model;

  // subscriber is harder to mock, it requires interaction with publisher
  private final TestSubscriber<Change> subscriber = new TestSubscriber<>();

  @Before
  public void before() {
    model = new RxModel(backing);
  }

  @Test
  @SuppressWarnings("unused")
  public void testAdd() {
    // given
    model.getPublisher().subscribe(subscriber);
    // when
    model.add(new Path("A"));
    // then
    // stored
    new Verifications() {

      {
        backing.add(withEqual(new Path("A")));
      }
    };
    // published

    Assert.assertEquals(1, subscriber.valueCount());
    subscriber.assertValueAt(0, DefaultChange.add(new Path("A")));
  }

  @Test
  public void testClose() throws Exception {
    // given
    model.getPublisher().subscribe(subscriber);
    // when
    model.close();
    // then
    subscriber.assertComplete();
  }

  @Test
  @SuppressWarnings("unused")
  public void testGetName() {
    // given
    new Expectations() {

      {
        backing.getName();
        result = "name";
      }
    };
    // when
    final String name = model.getName();
    // then
    Assert.assertEquals("name", name);
  }

  @Test
  @SuppressWarnings("unused")
  public void testGetNode() {
    // given
    new Expectations() {

      {
        backing.getNode(withEqual(new Path("A")));
        result = SIMPLE_MODEL.getRoot();
      }
    };
    // when
    final Node<?> node = model.getNode(new Path("A"));
    // then
    Assert.assertEquals(SIMPLE_MODEL.getRoot(), node);
  }

  @Test
  @SuppressWarnings("unused")
  public void testGetRoot() {
    // given
    new Expectations() {

      {
        backing.getRoot();
        result = SIMPLE_MODEL.getRoot();
      }
    };
    // when
    final Node<?> root = model.getRoot();
    // then
    Assert.assertEquals(SIMPLE_MODEL.getRoot(), root);
  }

  @Test
  @SuppressWarnings("unused")
  public void testGetValue() {
    // given
    new Expectations() {

      {
        backing.getValue(withEqual(new Path("A")));
        result = "a";
      }
    };
    // when
    final String value = model.getValue(new Path("A"));
    // then
    Assert.assertEquals("a", value);
  }

  @Test
  @SuppressWarnings("unused")
  public void testGetVersion() {
    // given
    new Expectations() {

      {
        backing.getVersion(withEqual(new Path("A")));
        result = new NumberedVersion(123);
      }
    };
    // when
    final Version version = model.getVersion(new Path("A"));
    // then
    Assert.assertEquals(new NumberedVersion(123), version);
  }

  @Test
  @SuppressWarnings("unused")
  public void testRemove() {
    // given
    model.getPublisher().subscribe(subscriber);
    // when
    model.remove(new Path("A"));
    // then
    // stored
    new Verifications() {

      {
        backing.remove(withEqual(new Path("A")));
      }
    };
    // published
    Assert.assertEquals(1, subscriber.valueCount());
    subscriber.assertValueAt(0, DefaultChange.remove(new Path("A")));
  }

  @Test
  @SuppressWarnings("unused")
  public void testSetValue() {
    // given
    model.getPublisher().subscribe(subscriber);
    new Expectations() {

      {
        backing.setValue(withEqual(new Path("A")), withEqual("aa"));
        result = "a";
      }
      {
        backing.getVersion(withEqual(new Path("A")));
        result = new NumberedVersion(1);
      }
    };
    // when
    final String oldValue = model.setValue(new Path("A"), "aa");
    // then
    // stored
    Assert.assertEquals("a", oldValue);
    // published
    Assert.assertEquals(1, subscriber.valueCount());
    subscriber.assertValueAt(0, DefaultChange.set(new Path("A"), new NumberedVersion(1), "aa"));
  }

  @Test
  @SuppressWarnings("unused")
  public void testSubscribe() {
    // given
    new Expectations() {

      {
        backing.getRoot();
        result = SIMPLE_MODEL.getRoot();
      }
    };
    model.getPublisher().subscribe(subscriber);
    // then
    // published
    Assert.assertEquals(4, subscriber.valueCount());
    subscriber.assertValueAt(0, DefaultChange.add(new Path("A")));
    subscriber.assertValueAt(1, DefaultChange.set(new Path("A"), new NumberedVersion(2), "a"));
    subscriber.assertValueAt(2, DefaultChange.add(new Path("A", "B")));
    subscriber.assertValueAt(3, DefaultChange.set(new Path("A", "B"), new NumberedVersion(3), "b"));
  }
}
