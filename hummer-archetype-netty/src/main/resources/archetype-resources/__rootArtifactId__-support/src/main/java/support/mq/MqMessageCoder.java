package ${package}.support.mq;

public interface MqMessageCoder<T> {
    byte[] encoder(T t);
}
