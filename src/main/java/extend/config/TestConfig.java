package extend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author 田奇杭
 * @Description
 * @Date 2022/9/10 10:43
 */
@Slf4j
@Component
public class TestConfig {

    @PostConstruct
    public void init () {
        log.info("公共包的容器来加载成功");
    }
}
