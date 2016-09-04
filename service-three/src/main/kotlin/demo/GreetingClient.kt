package demo


import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(name = "greeting", url = "http://service-one:8080", fallback = DefaultGreeting::class)
interface GreetingClient {
    @RequestMapping(method = arrayOf(RequestMethod.GET), value = "/greeting")
    fun greeting(): Greeting
}
