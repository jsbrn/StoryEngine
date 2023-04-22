package misc;

import java.util.Random;

public class MiscMath {

    public static int DELTA_TIME = 1;
    private static Random r;
    
    /**
     * Randomly select an element from the specified array.
     */
    public static Object randomElement(Object[] a) { return a[MiscMath.randomInt(0, a.length-1)]; }
    
    
    /**
     * Generate a random integer between the range [min, max] (inclusive)
     * @return An integer.
     */
    public static int randomInt(int min, int max) {
        if (r == null) r = new Random();
        if (max < Integer.MAX_VALUE) max += 1;
        return min + r.nextInt(Math.abs(min-max));
    }

    /**
     * Calculate the amount to add per frame to reach a certain value from a certain amount of time.
     * Accounts for delta time and entity update schedules.
     *
     * @param amount_to_add         The amount to add from the time specified.
     * @param per_x_seconds         The time specified.
     * @return The amount to add per frame.
     */
    public static double getConstant(double amount_to_add, double per_x_seconds) {
        if (per_x_seconds <= 0) return 0;
        double time_in_mills = per_x_seconds * 1000;
        double add_per_frame = amount_to_add / time_in_mills;
        add_per_frame *= DELTA_TIME;
        return add_per_frame;
    }

    /**
     * Calculates distance between two points. sqrt()
     *
     * @return The distance between (x1, y1) and (x2, y2).
     */
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public static double distanceSquared(double x1, double y1, double x2, double y2) {
        return (Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * Finds the angle between (x, y) and (x2, y2) with 0 degrees being a vertical line.
     *
     * @return A double representing the angle from degrees.
     */
    public static double angleBetween(double x1, double y1, double x2, double y2) {
        //slope formula = (Y2 - Y1) / (X2 - X1)
        double x, y, new_rotation;
        if (x1 < x2) {
            x = (x1 - x2) * -1;
            y = (y1 - y2) * -1;
            new_rotation = (((float) Math.atan(y / x) * 60));
        } else {
            x = (x2 - x1) * -1;
            y = (y1 - y2);
            new_rotation = (((float) Math.atan(y / x) * 60) + 180);
        }
        new_rotation += 90;
        return new_rotation % 360;
    }

    /**
     * Determines if point (x,y) intersects rectangle (rx, ry, rw, rh).
     *
     * @param x  The x value of the point.
     * @param y  The y value of the point.
     * @param rx The x value of the rectangle.
     * @param ry The y value of the rectangle.
     * @param rw The width of the rectangle.
     * @param rh The height of the rectangle.
     * @return A boolean indicating whether the point intersects.
     */
    public static boolean pointIntersectsRect(double x, double y,
                                              double rx, double ry, double rw, double rh) {
        return x > rx && x < rx + rw && y > ry && y < ry + rh;
    }

    /**
     * Determines if the rectangle with origin (x, y) and dimensions of (w, h)
     * intersects the line between points (lx1, ly1) and (lx2, ly2).
     *
     * @param x   Rectangle x value.
     * @param y   Rectangle y value.
     * @param w   Rectangle width.
     * @param h   Rectangle height.
     * @param lx1 x value of line endpoint #1
     * @param ly1 y value of line endpoint #1
     * @param lx2 x value of line endpoint #2
     * @param ly2 y value of line endpoint #2
     * @return A boolean indicating intersection.
     */
    public static boolean rectContainsLine(double x, double y, int w, int h, double lx1, double ly1, double lx2, double ly2) {
        if (pointIntersectsRect(lx1, ly1, x, y, w, h) || pointIntersectsRect(lx2, ly2, x, y, w, h)) return true;
        if (linesIntersect(lx1, ly1, lx2, ly2, x, y, x + w, y)) return true;
        if (linesIntersect(lx1, ly1, lx2, ly2, x, y + h, x + w, y + h)) return true;
        if (linesIntersect(lx1, ly1, lx2, ly2, x, y, x, y + h)) return true;
        if (linesIntersect(lx1, ly1, lx2, ly2, x + w, y, x + w, y + h)) return true;
        return false;
    }

    /**
     * Determines if two rectangles intersect each other. Checks each point to see if they
     * intersect the other rectangle. Calls on pointIntersects().
     *
     * @param x  The x of the first rectangle.
     * @param y  The y of the first rectangle.
     * @param w  The width of the first rectangle.
     * @param h  The height of the first rectangle.
     * @param x2 The x of the second rectangle.
     * @param y2 The y of the second rectangle.
     * @param w2 The width of the second rectangle.
     * @param h2 The height of the second rectangle.
     * @return A boolean indicating intersection.
     */
    public static boolean rectanglesIntersect(double x, double y, double w, double h, double x2, double y2, double w2, double h2) {
        if (MiscMath.pointIntersectsRect(x, y, x2, y2, w2, h2) || MiscMath.pointIntersectsRect(x + w, y, x2, y2, w2, h2)
                || MiscMath.pointIntersectsRect(x + w, y + h, x2, y2, w2, h2) || MiscMath.pointIntersectsRect(x, y + h, x2, y2, w2, h2)) {
            return true;
        }
        if (MiscMath.pointIntersectsRect(x2, y2, x, y, w, h) || MiscMath.pointIntersectsRect(x2 + w2, y2, x, y, w, h)
                || MiscMath.pointIntersectsRect(x2 + w2, y2 + h2, x, y, w, h) || MiscMath.pointIntersectsRect(x2, y2 + h2, x, y, w, h)) {
            return true;
        }
        return false;
    }

    /**
     * Check if a rectangle intersects a circle.
     *
     * @param cx The origin x of the circle.
     * @param cy The origin y of the circle.
     * @param r  The radius of the circle.
     */
    public static boolean rectangleIntersectsCircle(double x, double y, int w, int h, double cx, double cy, int r) {
        double r_x = x + (w / 2), r_y = y + (h / 2);
        double min_width = (w / 2) + r, min_height = (h / 2) + r;
        return MiscMath.distance(r_x, 0, cx, 0) < min_width
                && MiscMath.distance(r_y, 0, cy, 0) < min_height;
    }

    public static double min(double a, double b) {
        return a <= b ? a : b;
    }

    public static double max(double a, double b) {
        return a >= b ? a : b;
    }

    public static double clamp(double x, double min, double max) {
        if (x < min) return min;
        if (x > max) return max;
        return x;
    }

    /**
     * Rounds a to the nearest b.
     *
     * @return The value of a after rounding.
     */
    public static double round(double a, double b) {
        return Math.round(a / b) * b;
    }
    
    /**
     * Rounds a to the nearest (smallest) b.
     *
     * @return The value of a after rounding.
     */
    public static double floor(double a, double b) {
        return Math.floor(a / b) * b;
    }

    public static boolean linesIntersect(int[] l, int[] l2) {
        if (l == l2) return true;
        if (l.length != 4 || l2.length != 4) return false;
        return linesIntersect(l[0], l[1], l[2], l[3], l2[0], l2[1], l2[2], l2[3]);
    }

    public static boolean linesIntersect(double l1x1, double l1y1, double l1x2, double l1y2,
                                         double l2x1, double l2y1, double l2x2, double l2y2) {

        //determine the 4 points (of lines P1->P2, P3->P4; where P1 and P3 are the leftmost points of each line)
        double[] p1, p2, p3, p4;
        p1 = l1x1 <= l1x2 ? new double[]{l1x1, l1y1} : new double[]{l1x2, l1y2};
        p2 = l1x1 > l1x2 ? new double[]{l1x1, l1y1} : new double[]{l1x2, l1y2};
        p3 = l2x1 <= l2x2 ? new double[]{l2x1, l2y1} : new double[]{l2x2, l2y2};
        p4 = l2x1 > l2x2 ? new double[]{l2x1, l2y1} : new double[]{l2x2, l2y2};

        //calculate the slopes and y-intercepts of both lines
        double run1 = (p2[0] - p1[0]), run2 = (p4[0] - p3[0]), m1, m2, b1, b2;
        //set the slope to the highest integer value if the line is vertical
        //this value is never used, as I have special cases below
        m1 = run1 != 0 ? (p2[1] - p1[1]) / run1 : Integer.MAX_VALUE;
        m2 = run2 != 0 ? (p4[1] - p3[1]) / run2 : Integer.MAX_VALUE;
        b1 = p1[1] - (p1[0] * m1);
        b2 = p3[1] - (p3[0] * m2);

        //special case for if both lines are vertical
        if (run1 == 0 && run2 == 0) {
            if (p1[0] != p3[0]) return false; //if x values don't match, no intersection
            //if any of the 4 y values are touching the other line segment, intersection
            if (p1[1] >= min(p3[1], p4[1]) && p1[1] <= max(p3[1], p4[1])) return true;
            if (p2[1] >= min(p3[1], p4[1]) && p2[1] <= max(p3[1], p4[1])) return true;
            if (p3[1] >= min(p1[1], p2[1]) && p3[1] <= max(p1[1], p2[1])) return true;
            if (p4[1] >= min(p1[1], p2[1]) && p4[1] <= max(p1[1], p2[1])) return true;
        }
        //special cases for if only one of the lines are vertical
        //use the line formula to find the point of intersection at x = line 2, and then compare x/y values
        if (run1 == 0) {
            if (p1[0] > p4[0] || p1[0] < p3[0]) return false;
            double y2 = (m2 * p1[0]) + b2;
            return y2 >= min(p1[1], p2[1]) && y2 <= max(p1[1], p2[1]);
        }
        if (run2 == 0) {
            if (p3[0] > p2[0] || p3[0] < p1[0]) return false;
            double y1 = (m1 * p3[0]) + b1;
            return y1 >= min(p3[1], p4[1]) && y1 <= max(p3[1], p4[1]);
        }

        //for when neither lines are vertical, use Cramer's rule to determine a point of intersection
        double a = 1, b = -m1, c = 1, d = -m2;
        double det = (a * d) - (b * c);
        if (det != 0) {
            double detx = (a * b2) - (b1 * c);
            double dety = (b1 * d) - (b * b2);
            double x = detx / det, y = dety / det;
            if ((x >= p1[0] && x <= p2[0]) && (x >= p3[0] && x <= p4[0])) return true;
        } else { //if no point to be found, then they are paralell
            if ((int) b1 != (int) b2) return false; //if they have different y-intercepts, no intersection
            //otherwise, check each x value to see if they are touching the other segment
            if (p1[0] >= p3[0] && p1[0] <= p4[0]) return true;
            if (p2[0] >= p3[0] && p2[0] <= p4[0]) return true;
            if (p3[0] >= p1[0] && p3[0] <= p2[0]) return true;
            if (p4[0] >= p1[0] && p4[0] <= p2[0]) return true;
        }
        return false;
    }

    /**
     * Returns a rotated point about the origin (0, 0).
     *
     * @param offset_x The x coord.
     * @param offset_y The y coord.
     * @param rotation The angle, from degrees.
     * @return
     */
    public static int[] getRotatedOffset(int offset_x, int offset_y, double rotation) {
        rotation = Math.toRadians(rotation);
        return new int[]{(int) (offset_x * Math.cos(rotation) - offset_y * Math.sin(rotation)),
                (int) (offset_x * Math.sin(rotation) + offset_y * Math.cos(rotation))};
    }

    public static double[] calculateVelocity(double x, double y) {
        double dt = distance(0, 0, x, y);
        return new double[]{dt != 0 ? x / dt : 0, dt != 0 ? y / dt : 0};
    }
    
    
    public static int[] toIntArray(String s, int size) {
        int[] arr = toIntArray(s);
        if (arr.length == 0) return new int[size];
        return arr;
    }
    
    /**
     * Turns a String of numbers into an int array.
     * @param s The string to parse.
     * @return An int array.
     */
    public static int[] toIntArray(String s) {
        if (s == null) return new int[]{};
        if (s.length() == 0) return new int[]{};
        String parsed[] = s.toLowerCase().split("[^0-9-+]");
        return toIntArray(parsed);
    }
    
    public static int[] toIntArray(String[] s) {
        if (s == null) return new int[]{};
        int[] result = new int[s.length];
        for (int i = 0; i < s.length; i++) result[i] = Integer.parseInt(s[i]);
        return result;
    }
    
    public static double[] toDoubleArray(String[] s) {
        if (s == null) return new double[]{};
        double[] result = new double[s.length];
        for (int i = 0; i < s.length; i++) result[i] = Double.parseDouble(s[i]);
        return result;
    }
    
    /**
     * Turns a String of numbers into a double array.
     * @param s The string to parse.
     * @return A double array.
     */
    public static double[] toDoubleArray(String s) {
        if (s == null) return new double[]{};
        String parsed[] = s.toLowerCase().split("[^0-9-.+]");
        return toDoubleArray(parsed);
    }
    
    public static boolean[] toBooleanArray(String s) {
        if (s == null) return new boolean[]{};
        String parsed[] = s.trim().toLowerCase().split(" ");
        return toBooleanArray(parsed);
    }
    
    public static boolean[] toBooleanArray(String[] s) {
        if (s == null) return new boolean[]{};
        boolean[] result = new boolean[s.length];
        for (int i = 0; i < s.length; i++) result[i] = Boolean.parseBoolean(s[i]);
        return result;
    }

}