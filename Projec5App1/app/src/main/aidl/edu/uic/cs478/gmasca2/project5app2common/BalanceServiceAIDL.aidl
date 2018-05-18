// BalanceServiceAIDL.aidl
package edu.uic.cs478.gmasca2.project5app2common;
//
import edu.uic.cs478.gmasca2.project5app2common.DailyCash;
//
//// Declare any non-default types here with import statements
//
interface BalanceServiceAIDL {
//    /**
//     * Demonstrates some basic types that you can use as parameters
//     * and return values in AIDL.
//     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);
//
     boolean createDatabase();
     DailyCash[] dailyCash(int day, int month, int year, int number_of_days);
}
