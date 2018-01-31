// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.rx;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import nl.jvdploeg.flat.Path;
import nl.jvdploeg.flat.impl.DefaultChange;
import nl.jvdploeg.nfa.Nfa;
import nl.jvdploeg.nfa.NfaFactory;
import nl.jvdploeg.nfa.NfaService;

public class PathFilterTest {

  @Test
  public void test() throws IOException {
    // given
    @SuppressWarnings("rawtypes")
    final NfaFactory nfaFactory = NfaService.getInstance().createNfaFactory();
    @SuppressWarnings("unchecked")
    final Nfa<?> abAny = nfaFactory.sequence(Arrays.asList(nfaFactory.token("A"), nfaFactory.token("B"), nfaFactory.zeroOrMore(nfaFactory.any())));
    final PathFilter filter = new PathFilter(abAny);
    // when / then
    Assert.assertFalse(filter.test(DefaultChange.add(new Path())));
    Assert.assertFalse(filter.test(DefaultChange.add(new Path("other"))));
    Assert.assertFalse(filter.test(DefaultChange.add(new Path("A"))));
    Assert.assertFalse(filter.test(DefaultChange.add(new Path("A", "other"))));
    Assert.assertTrue(filter.test(DefaultChange.add(new Path("A", "B"))));
    Assert.assertTrue(filter.test(DefaultChange.add(new Path("A", "B", "any"))));
    Assert.assertTrue(filter.test(DefaultChange.add(new Path("A", "B", "any", "any"))));
  }
}
