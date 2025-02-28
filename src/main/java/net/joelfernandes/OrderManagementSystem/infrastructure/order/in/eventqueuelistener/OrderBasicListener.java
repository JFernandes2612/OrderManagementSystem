package net.joelfernandes.OrderManagementSystem.infrastructure.order.in.eventqueuelistener;

public interface OrderBasicListener<T> {
    String topic = "inOrder";

    void receive(T in);
}
