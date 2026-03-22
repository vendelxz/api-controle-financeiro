package com.controlefinaneiro.api.infra.seguranca;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        //Isso serve para injetar nas threads novas de email as configurações de autenticação
        //Pois como emailService roda em outra, ele precisa disso para poder enviar e conseguir carregar o usuário logado
        ThreadPoolTaskExecutor delegate = new ThreadPoolTaskExecutor();
        delegate.setCorePoolSize(2);
        delegate.setMaxPoolSize(5);
        delegate.setQueueCapacity(20);
        delegate.setThreadNamePrefix("AsyncRelatorio-");
        delegate.initialize();

        return new DelegatingSecurityContextAsyncTaskExecutor(delegate);
    }
}
