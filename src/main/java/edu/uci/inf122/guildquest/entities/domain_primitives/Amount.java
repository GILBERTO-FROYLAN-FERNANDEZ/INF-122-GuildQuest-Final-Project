package edu.uci.inf122.guildquest.entities.domain_primitives;

public class Amount implements Comparable<Amount>, Cloneable{
    protected final int count;
    public Amount(int c){
        if (c<0){
            throw new IllegalArgumentException("Cannot have negative amount");
        }
        count = c;
    }

    public int getCount() {
        return count;
    }


    /**
     * Returns a NEW amount multiplied. Does not modify this amount;
     * @param i amount to multiply by
     */
    public Amount multiply(int i) {
            return new Amount(count*i);
    }
    /**
     * Returns a NEW amount multiplied. Does not modify this amount;
     * @param i amount to multiply by
     */
    public DecimalAmount multiply(double i) {
        return new DecimalAmount(count*i);
    }

    @Override
    public String toString() {
        return Integer.toString(count);
    }
    @Override
    public int compareTo(Amount o) {
        return Integer.compare(count, o.count);
    }
}
