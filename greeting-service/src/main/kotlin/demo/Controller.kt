package demo

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller {

    @RequestMapping("/greeting")
    fun greet(): Greeting {
        return Greeting("Hello World")
    }
}
