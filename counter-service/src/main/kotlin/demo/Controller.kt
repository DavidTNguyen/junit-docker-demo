package demo

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicLong

@RestController
class Controller {

    val counter = AtomicLong()

    @RequestMapping("/counter")
    fun count(): Counter {
        return Counter(counter.incrementAndGet())
    }
}
