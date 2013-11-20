package helpers;

import java.util.HashMap;
import java.util.Map;

public class Ranker<K> {
    Map<K, Double> map;

    public Ranker(){
        map = new HashMap<K, Double>();
    }

    public void zero (K key){
        map.put(key, 0.0);
    }

    public Double increase(K key, Double increaseBy){
        if (map.containsKey(key)){
            map.put(key, map.get(key)+increaseBy);
        }
        else {
            map.put(key, increaseBy);
        }
        return map.get(key);
    }

    public Double get(K key) {
        return map.get(key);
    }

    public K getMax(){
        K maxKey = null;
        Double maxValue = Double.valueOf(Double.MIN_VALUE);
        for (Map.Entry<K, Double> entry : map.entrySet()){
            //if this value is greater than the maxValue found so far
            if (entry.getValue().compareTo(maxValue)>0){
                maxValue = entry.getValue();
                maxKey = entry.getKey();
            }
        }
        return maxKey;
    }

    public K getMin(){
        K minKey = null;
        Double minValue = Double.valueOf(Double.MAX_VALUE);
        for (Map.Entry<K, Double> entry : map.entrySet()){
            //if this value is less than the minValue found so far
            if (entry.getValue().compareTo(minValue)<0){
                minValue = entry.getValue();
                minKey = entry.getKey();
            }
        }
        return minKey;
    }
}
