/*
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.tiefensuche.motionmate.util

/**
 * From openjdk8 for compatibility for lower api versions
 */
internal object Math {

    /**
     * Returns the largest (closest to positive infinity)
     * `int` value that is less than or equal to the algebraic quotient.
     * There is one special case, if the dividend is the
     * [Integer.MIN_VALUE] and the divisor is `-1`,
     * then integer overflow occurs and
     * the result is equal to the `Integer.MIN_VALUE`.
     *
     *
     * Normal integer division operates under the round to zero rounding mode
     * (truncation).  This operation instead acts under the round toward
     * negative infinity (floor) rounding mode.
     * The floor rounding mode gives different results than truncation
     * when the exact result is negative.
     *
     *  * If the signs of the arguments are the same, the results of
     * `floorDiv` and the `/` operator are the same.  <br></br>
     * For example, `floorDiv(4, 3) == 1` and `(4 / 3) == 1`.
     *  * If the signs of the arguments are different,  the quotient is negative and
     * `floorDiv` returns the integer less than or equal to the quotient
     * and the `/` operator returns the integer closest to zero.<br></br>
     * For example, `floorDiv(-4, 3) == -2`,
     * whereas `(-4 / 3) == -1`.
     *
     *
     *
     *
     *
     * @param x the dividend
     * @param y the divisor
     * @return the largest (closest to positive infinity)
     * `int` value that is less than or equal to the algebraic quotient.
     * @throws ArithmeticException if the divisor `y` is zero
     * @see .floorMod
     * @since 1.8
     */
    internal fun floorDiv(x: Int, y: Int): Int {
        var r = x / y
        // if the signs are different and modulo not zero, round down
        if (x xor y < 0 && r * y != x) {
            r--
        }
        return r
    }

    /**
     * Returns the floor modulus of the `int` arguments.
     *
     *
     * The floor modulus is `x - (floorDiv(x, y) * y)`,
     * has the same sign as the divisor `y`, and
     * is in the range of `-abs(y) < r < +abs(y)`.
     *
     *
     *
     * The relationship between `floorDiv` and `floorMod` is such that:
     *
     *  * `floorDiv(x, y) * y + floorMod(x, y) == x`
     *
     *
     *
     * The difference in values between `floorMod` and
     * the `%` operator is due to the difference between
     * `floorDiv` that returns the integer less than or equal to the quotient
     * and the `/` operator that returns the integer closest to zero.
     *
     *
     * Examples:
     *
     *  * If the signs of the arguments are the same, the results
     * of `floorMod` and the `%` operator are the same.  <br></br>
     *
     *  * `floorMod(4, 3) == 1`; &nbsp; and `(4 % 3) == 1`
     *
     *  * If the signs of the arguments are different, the results differ from the `%` operator.<br></br>
     *
     *  * `floorMod(+4, -3) == -2`; &nbsp; and `(+4 % -3) == +1`
     *  * `floorMod(-4, +3) == +2`; &nbsp; and `(-4 % +3) == -1`
     *  * `floorMod(-4, -3) == -1`; &nbsp; and `(-4 % -3) == -1 `
     *
     *
     *
     *
     *
     * If the signs of arguments are unknown and a positive modulus
     * is needed it can be computed as `(floorMod(x, y) + abs(y)) % abs(y)`.
     *
     * @param x the dividend
     * @param y the divisor
     * @return the floor modulus `x - (floorDiv(x, y) * y)`
     * @throws ArithmeticException if the divisor `y` is zero
     * @see .floorDiv
     * @since 1.8
     */
    internal fun floorMod(x: Int, y: Int): Int {
        return x - floorDiv(x, y) * y
    }

}
