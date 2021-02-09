package ${package}.support.redis;

public interface EventHandler<E> {
    void doEvent(String channel,E e);
}
