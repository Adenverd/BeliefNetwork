package helpers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BiMap<K, V> implements Map{
    protected Map map;
    protected Map inverseMap;

    public BiMap(){
        map = new HashMap<K, V>();
        inverseMap = new HashMap<V, K>();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return map.get(key);
    }

    @Override
    public Object put(Object key, Object value) {
        Object val = null;
        if(map.containsKey(key)){
            val = map.get(key);
        }
        map.put((K)key, (V)value);
        inverseMap.put((V)value, (K)key);
        return val;
    }

    @Override
    public Object remove(Object key) {
        Object value = map.get(key);
        inverseMap.remove(value);
        return map.remove(key);
    }

    @Override
    public void putAll(Map m) {
        return;
    }

    @Override
    public void clear() {
        map.clear();
        inverseMap.clear();
    }

    @Override
    public Set keySet() {
        return map.keySet();
    }

    @Override
    public Collection values() {
        return map.values();
    }

    @Override
    public Set<Entry> entrySet() {
        return map.entrySet();
    }

    public Object inverseGet(Object value){
        return inverseMap.get(value);
    }
}
