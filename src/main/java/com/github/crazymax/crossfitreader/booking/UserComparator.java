package com.github.crazymax.crossfitreader.booking;

import java.util.Comparator;

/**
 * Booking User comparator
 * @author CrazyMax
 * @license MIT License
 * @link https://github.com/crazy-max/crossfit-reader
 */
public class UserComparator implements Comparator<User> {

    @Override
    public int compare(User o1, User o2) {
        final String u1 = o1.getLastName() + " " + o1.getFirstName();
        final String u2 = o2.getLastName() + " " + o2.getFirstName();
        return u1.compareToIgnoreCase(u2);
    }

}
