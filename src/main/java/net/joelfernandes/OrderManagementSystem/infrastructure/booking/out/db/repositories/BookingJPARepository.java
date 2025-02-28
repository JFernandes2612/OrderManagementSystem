package net.joelfernandes.OrderManagementSystem.infrastructure.booking.out.db.repositories;

import java.util.Optional;
import net.joelfernandes.OrderManagementSystem.infrastructure.booking.out.db.entities.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingJPARepository extends JpaRepository<BookingEntity, String> {
    Optional<BookingEntity> findByOrder_OrderId(String orderId);
}
