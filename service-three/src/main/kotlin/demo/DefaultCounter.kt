package demo

import org.springframework.stereotype.Component

@Component
class DefaultCounter : CounterClient {
    override fun counter(): Counter {
        return Counter(42)
    }
}