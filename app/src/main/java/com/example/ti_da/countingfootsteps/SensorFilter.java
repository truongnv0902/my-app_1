package com.example.ti_da.countingfootsteps;

/**
 * Created by Ti_Da on 4/4/2018.
 */

public class SensorFilter {
    private SensorFilter() {
    }

    public static float sum(float[] array) {
        float retval = 0;
        for (int i = 0; i < array.length; i++) {
            retval += array[i];
        }
        return retval;
    }
    public static float[] cross(float[] arrayA, float[] arrayB) {
        float[] retArray = new float[3];
        retArray[0] = arrayA[1] * arrayB[2] - arrayA[2] * arrayB[1];
        retArray[1] = arrayA[2] * arrayB[0] - arrayA[0] * arrayB[2];
        retArray[2] = arrayA[0] * arrayB[1] - arrayA[1] * arrayB[0];
        return retArray;
    }
    public static float norm(float[] array){
        float retval = 0;
        for (int i = 0; i < array.length; i++){
            retval += array[i] * array[i];
        }
        return (float) Math.sqrt(retval);
    }/*
    public static  float filt( float[] xn, float[] A, float[] B)
    {
        if ( A[0] == 0) return  1;
        float[] x = new float[xn.length + 4];
        for( int i= 0 ; i< 4; i++) x[i] = 0;
        for( int i= 4; i< x.length; i++) x[i] = xn[i-4];

        float[] y = new float[x.length+4];
        for( int i=0; i<y.length; i++) y[i] = 0;
        int n;
        for ( int i=0; i<x.length; i++)
        {
            n = i+4;
            y[n] = (B[0]*x[n] + B[1]*x[n-1] + B[2]*x[n-2] + B[3]*x[n-3] + B[4]*x[n-4] - A[1]*y[n-1] - A[2]*y[n-2] - A[3]*y[n-3] - A[4]*y[n-4])/A[0];
        }



    }
*/
    public static float dot(float[] a, float[] b) {
        float retval = 0;
        for (int i = 0; i < a.length; i++) {
            retval += a[i] * b[i];
        }
        return retval;
    }

    public static float[] normalize(float[] a){
        float[] retval = new float[a.length];
        float norm = norm(a);
        for (int i = 0; i < a.length; i++) {
            retval[i] = a[i] / norm;
        }
        return retval;
    }

}