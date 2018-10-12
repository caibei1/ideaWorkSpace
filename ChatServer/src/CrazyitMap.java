import java.util.*;

public class CrazyitMap<K,V> {
    //创建一个线程安全的hashMap
    public Map<K,V> map = Collections.synchronizedMap(new HashMap<K,V>());
    //根据value来删除指定项
    public synchronized void removeByValue(Object value){
        for(Object key:map.keySet()){
            if(map.get(key)==value){
                map.remove(key);
                break;
            }
        }
    }

    //获取所有value组成的set集合
    public synchronized Set<V> valueSet(){
        Set<V> result = new HashSet<V>();
        //将map中所有value添加到result集合中
        for (Map.Entry<K,V> entry:map.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }

    //根据value查找key
    public synchronized K getKeyByValue(V val){
        for(K key : map.keySet()){
            if(map.get(key)==val||map.get(key).equals(val)){
                return key;
            }
        }
        return null;
    }

    //重写put方法  不允许重复value
    public synchronized V put(K key,V value){
        for(V val : valueSet()){
            if(val.equals(value)&&val.hashCode()==value.hashCode()){
                throw new RuntimeException("map中不允许有重复value!");
            }
        }
        return map.put(key,value);
    }


}



























