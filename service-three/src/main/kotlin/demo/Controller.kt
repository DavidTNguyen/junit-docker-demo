package demo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller @Autowired constructor(val greetingClient: GreetingClient,
                                        val counterClient: CounterClient) {

    @RequestMapping("/greeting")
    fun greeting(): Response {
        return Response(
                counterClient.counter().counter,
                greetingClient.greeting().greeting
        )
    }
}