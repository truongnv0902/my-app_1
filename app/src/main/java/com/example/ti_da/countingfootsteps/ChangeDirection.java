package com.example.ti_da.countingfootsteps;

/**
 * Created by Ti_Da on 4/14/2018.
 */

public class ChangeDirection {
    // gắn mốc biểu thị hướng
    private final int FLAG_TURN_LEFT = 1;
    private final int FLAG_TURN_RIGHT = 2;
    private final int FLAG_GO_TRAIGHT = 0;

    public int DetectDirection(float value1, float value2, float value3){
        int flag;
        float a = value1 * value1;
        float b = value2 * value2;
        float c = value3 * value3;

        float Angle = (float) Math.sqrt(a + b + c);

        // chuyển đổi góc quay của sensor từ radian sang góc

        int AngleDg = (int) Math.toDegrees(Angle);

        if (AngleDg > 45){
            flag = FLAG_TURN_RIGHT;
        } else if(AngleDg < - 45){
            flag = FLAG_TURN_LEFT;
        }else {
            flag = FLAG_GO_TRAIGHT;
        }
        // từ đây có thể detect được các hướng ( quay lại 120 < a < 180 || ngược lại )
        return flag;
    }

}
