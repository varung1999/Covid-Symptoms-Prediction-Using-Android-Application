package com.example.assignment1;
import java.util.*;
public class PeakDetection {
    static boolean isPeak(float arr[], int n, float num,
                          int i, int j)
    {
        if (i >= 0 && arr[i] > num)
        {
            return false;
        }
        if (j < n && arr[j] > num)
        {
            return false;
        }
        return true;
    }

    static boolean isTrough(float arr[], int n, float num,
                            int i, int j)
    {
        if (i >= 0 && arr[i] < num)
        {
            return false;
        }

        if (j < n && arr[j] < num)
        {
            return false;
        }
        return true;
    }

    static ArrayList<Float> peaksTroughs(float arr[], int n)
    {
        ArrayList<Float> res;
        res=new ArrayList<>();
        for (int i = 0; i < n; i++)
        {
            if (isPeak(arr, n, arr[i], i - 1, i + 1))
            {
                res.add(arr[i]);
            }
        }
        for (int i = 0; i < n; i++)
        {
            if (isTrough(arr, n, arr[i], i - 1, i + 1))
            {
                System.out.print(arr[i] + " ");
            }
        }
        return res;
    }
}
