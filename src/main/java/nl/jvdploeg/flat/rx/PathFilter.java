// The author disclaims copyright to this source code.
package nl.jvdploeg.flat.rx;

import io.reactivex.functions.Predicate;
import nl.jvdploeg.flat.Change;
import nl.jvdploeg.flat.Path;
import nl.jvdploeg.nfa.TokenMatcher;

public final class PathFilter implements Predicate<Change> {

  private final TokenMatcher matcher;

  public PathFilter(final TokenMatcher matcher) {
    this.matcher = matcher;
  }

  @Override
  public boolean test(final Change change) {
    final Path path = change.getPath();
    return matcher.matches(path.getPath());
  }
}
