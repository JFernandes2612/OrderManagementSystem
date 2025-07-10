package net.joelfernandes.ordermanagementsystem.infrastructure.order.in.eventqueuelistener;

public interface OrderBasicListener<T> {
    void receive(T in);
}
