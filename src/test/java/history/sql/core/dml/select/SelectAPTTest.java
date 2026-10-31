package history.sql.core.dml.select;

import history.sql.InitializingContext;
import history.sql.entites.User;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SelectAPTTest extends InitializingContext {
    private static final Logger log = LoggerFactory.getLogger(SelectTest.class);

    @Test
    void test() {
        log.info("test");
        List<User> list = sqlContext.select()
                .column(User::getUserId)
                .column(User::getName)
                .from(User.class)
                .fetch()
                .toList();
        System.out.println(list);
        System.out.println(list.size());
    }
}
