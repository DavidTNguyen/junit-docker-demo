package demo

import org.springframework.stereotype.Component

@Component
class DefaultGreeting : GreetingClient {
    override fun greeting(): Greeting {
        return Greeting("Hola!")
    }
}