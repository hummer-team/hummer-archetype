package ${package}.support.cache;

public interface SimpleCache<K,D> {
    void put(K key, D data);
    D get(K key);
    void removeOf(K key);
    void removeOfKeys(Iterable<K> key);
    void cleanAll();
}
