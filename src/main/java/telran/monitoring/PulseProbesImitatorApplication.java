package telran.monitoring;

import java.util.function.Supplier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.monitoring.dto.PulseProbe;
import telran.monitoring.service.PulseProbesImitator;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class PulseProbesImitatorApplication {
private static final long TIMEOUT = 30000;
final PulseProbesImitator imitator;
	public static void main(String[] args) throws InterruptedException {
		ConfigurableApplicationContext ctx = SpringApplication.run(PulseProbesImitatorApplication.class, args);
		Thread.sleep(TIMEOUT);
		ctx.close();
	}
	@Bean
	Supplier<PulseProbe> probesSupplier() {
		return () -> {
			PulseProbe probe = imitator.nextProbe();
			log.trace("{}", probe);
			return probe;
		};
	}

}
