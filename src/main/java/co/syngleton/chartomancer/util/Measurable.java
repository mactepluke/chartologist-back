package co.syngleton.chartomancer.util;

public interface Measurable {
    /**
     * Computes and returns the average measure for an array of Measurables
     *
     * @param list an array of Measurables
     * @return the average measure for the objects in the array
     */
    static double getAverage(Measurable[] list) {

        if (list.length == 0)
            return 0.0;


        double sum = 0.0;

        for (Measurable object : list) {
            sum += object.getMeasure();
        }
        return sum / list.length;
    }

    /**
     * Computes the measure of the object.
     *
     * @return the measure
     */
    double getMeasure();       // abstract method

    /**
     * Is the measure of this object greater than the measure of another?
     *
     * @param other the Measurable to which this object is compared
     * @return true if the measure of this object > the measure of other; else
     * returns false
     */
    default boolean isGreaterThan(Measurable other) {
        return this.getMeasure() > other.getMeasure();
    }
}