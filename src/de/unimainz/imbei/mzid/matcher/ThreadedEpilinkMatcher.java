package de.unimainz.imbei.mzid.matcher;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import de.unimainz.imbei.mzid.Patient;
import de.unimainz.imbei.mzid.matcher.MatchResult.MatchResultType;

public class ThreadedEpilinkMatcher extends EpilinkMatcher {

	
	private class MatchResultContainer {
		public MatchResult matchResult;
	}
	
	private class MatchCallable implements Runnable {
		
		private Iterator<Patient> iterator;
		private Patient left;
		private MatchResultContainer bestMatchResult;

		public MatchCallable(Patient left, Iterator<Patient> iterator, MatchResultContainer bestMatchResult) {
			this.iterator = iterator;
			this.left = left;
			this.bestMatchResult = bestMatchResult;
		}
	
		@Override
		public void run() {
			Patient right;

			do {
				synchronized (iterator) {
					if (!iterator.hasNext()) break;
					right = iterator.next();
				}
				double thisWeight = calculateWeight(left, right);
				synchronized(bestMatchResult) {
					if (thisWeight > bestMatchResult.matchResult.getBestMatchedWeight()) {
						MatchResultType t;
						if (thisWeight >= getThresholdMatch())
							t = MatchResultType.MATCH;
						else if (thisWeight >= getThresholdNonMatch())
							t = MatchResultType.POSSIBLE_MATCH;
						else
							t = MatchResultType.NON_MATCH;
						bestMatchResult.matchResult = new MatchResult(t, right, thisWeight);
					}
				}
			} while (true);
		}
	}
	
	/* (non-Javadoc)
	 * @see de.unimainz.imbei.mzid.matcher.Matcher#match(de.unimainz.imbei.mzid.Patient, java.lang.Iterable)
	 */
	@Override
	public MatchResult match(Patient a, Iterable<Patient> patientList) {

		MatchResult bestMatchResult = new MatchResult(MatchResultType.NON_MATCH, null, -Double.MAX_VALUE);
		MatchResultContainer resultContainer = new MatchResultContainer();
		resultContainer.matchResult = bestMatchResult;
		int nThreads = Runtime.getRuntime().availableProcessors();
		ExecutorService service = Executors.newFixedThreadPool(nThreads);
		Iterator<Patient> patientIterator = patientList.iterator();
		for (int i=0; i < nThreads; i++) {
			service.execute(new MatchCallable(a, patientIterator, resultContainer));
		}
		service.shutdown();
		while (!service.isTerminated()) {};
		return resultContainer.matchResult;
	}
	
	
}
