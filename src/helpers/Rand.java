package helpers;

import java.util.Random;

public class Rand {
    public static Random random;

    public static double nextDouble(){
        if(random==null){
            random = new Random();
        }
        return random.nextDouble();
    }

    public static double nextGaussian(){
        if(random==null){
            random = new Random();
        }
        return random.nextGaussian();
    }

    public static int nextInt(){
        if(random==null){
            random = new Random();
        }
        return random.nextInt();
    }

    public static int nextInt(int n){
        if(random==null){
            random = new Random();
        }
        return random.nextInt(n);
    }
}
