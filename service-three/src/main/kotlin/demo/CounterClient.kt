package demo


import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(name = "counter", url = "http://service-two:8080", fallback = DefaultCounter::class)
interface CounterClient {
    @RequestMapping(method = arrayOf(RequestMethod.GET), value = "/counter")
    fun counter(): Counter
}
