package net.joelfernandes.OrderManagementSystem.infrastructure.order.in.eventqueuelistener;

public interface OrderBasicListener<T> {
    void receive(T in);
}
