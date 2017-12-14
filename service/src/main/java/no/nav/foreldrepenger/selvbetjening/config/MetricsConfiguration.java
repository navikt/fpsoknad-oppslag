package no.nav.foreldrepenger.selvbetjening.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import no.nav.foreldrepenger.selvbetjening.cxfclient.STSConfig;

@Configuration
public class MetricsConfiguration {

  @Bean
  public JvmThreadMetrics threadMetrics() {
    return new JvmThreadMetrics();
  }

  @Bean
  public ClassLoaderMetrics classLoaderMetrics() {
    return new ClassLoaderMetrics();
  }

  @Bean
  public JvmGcMetrics jvmGcMetrics() {
    return new JvmGcMetrics();
  }

  @Bean
  public ProcessorMetrics processorMetrics() {
    return new ProcessorMetrics();
  }


  @Bean
  public JvmMemoryMetrics jvmMemoryMetrics() {
    return new JvmMemoryMetrics();
  }

  @Bean
  public LogbackMetrics logbackMetrics() {
    return new LogbackMetrics();
  }

  @Bean
  public UptimeMetrics uptimeMetrics() {
    return new UptimeMetrics();
  }

  @Bean
  public FileDescriptorMetrics fileDescriptorMetrics() {
    return new FileDescriptorMetrics();
  }
  
  @Bean
	public STSConfig stsConfig(@Value("${SECURITYTOKENSERVICE_URL:\"https://sts-t1.test.local/SecurityTokenServiceProvider/}") String stsUrl,
	                           @Value("${FPSELVBETJENING_USERNAME:srvengangsstonad}") String systemUserName,
	                           @Value("${FPSELVBETJENING_PASSWORD:AsbwRoF101WrjtP}") String systemUserPassword) {
		return new STSConfig(stsUrl,systemUserName,systemUserPassword);
	}

}
