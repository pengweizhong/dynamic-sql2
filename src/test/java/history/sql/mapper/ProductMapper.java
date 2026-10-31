package history.sql.mapper;

import history.sql.entites.Product;

public interface ProductMapper extends EntityMapper<Product> {

    default void printSayHello() {
        System.out.println("hello world!");
    }
}
