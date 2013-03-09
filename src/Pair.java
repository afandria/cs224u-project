public class Pair<X, Y> { 
  public final X x; 
  public final Y y; 
  public Pair(X x, Y y) { 
    this.x = x; 
    this.y = y; 
  }
  public boolean equals(Object other) {
	  if (!(other instanceof Pair<?, ?>))
		  return false;
	  Pair<?, ?> o = (Pair<?, ?>)other;
	  return x.equals(o.x) && y.equals(o.y);
  }  
  
  public int hashCode() {
	  return x.hashCode() + y.hashCode();
  }
  
  public String toString() {
	  return "<" + x.toString() + "," + y.toString() + ">";
  }
}