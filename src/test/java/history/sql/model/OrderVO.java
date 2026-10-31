package history.sql.model;

import history.sql.entites.Order;
import history.sql.entites.User;
import lombok.Data;

@Data
public class OrderVO {
    private Order order;
    private User user;
}

