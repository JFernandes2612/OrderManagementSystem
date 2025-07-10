package net.joelfernandes.ordermanagementsystem.infrastructure.order.out.db.repositories;

import net.joelfernandes.ordermanagementsystem.infrastructure.order.out.db.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJPARepository extends JpaRepository<OrderEntity, String> {}
