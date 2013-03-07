public class Triple<X, Y, Z> { 
  public final X x; 
  public final Y y; 
  public final Z z; 
  public Triple(X x, Y y, Z z) { 
    this.x = x; 
    this.y = y;
    this.z = z;
  } 
  
  public boolean equals(Object other) {
	  if (!(other instanceof Triple<?, ?, ?>))
		  return false;
	  Triple<?, ?, ?> o = (Triple<?, ?, ?>)other;
	  return x.equals(o.x) && y.equals(o.y) && z.equals(o.z);
  }
  public int hashCode() {
	  return x.hashCode() +  y.hashCode() + z.hashCode();
  }
}