package co.syngleton.chartomancer.signaling;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "min-api.cryptocompare", url = "https://min-api.cryptocompare.com/data")
public interface CryptoCompareApiProxy {

    @GetMapping("/v2/histohour")
    String getHourGraph(@RequestParam String fsym,
                        @RequestParam String tsym,
                        @RequestParam int limit,
                        @RequestParam String api_key);

    @GetMapping("/v2/histoday")
    String getDayGraph(@RequestParam String fsym,
                       @RequestParam String tsym,
                       @RequestParam int limit,
                       @RequestParam String api_key);

    @GetMapping("/price")
    String getCurrentPrice(@RequestParam String fsym,
                           @RequestParam String tsyms,
                           @RequestParam String api_key);

}
