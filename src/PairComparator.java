import java.util.Comparator;

public class PairComparator implements Comparator {
	int multiplier = 1;
	
	public PairComparator(boolean ascendingOrder) {
		multiplier = ascendingOrder?1:-1;
	}
	
	public int compare(Object o1, Object o2) {
		Pair<Double, ?> p1 = (Pair<Double, ?>)o1;
		Pair<Double, ?> p2 = (Pair<Double, ?>)o2;
		if (p1.x == p2.x)
			return 0;
		else
			return p1.x > p2.x?multiplier:-multiplier;
	}
	
}
