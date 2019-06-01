package com.client.hazelcast.client;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.client.hazelcast.client.helper.StorageNodeFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**

 */
@Configuration
@ComponentScan(basePackages = {
        "com.shared.hazelcast.shared",
        "com.client.hazelcast.client",
        "com.storagenode.hazelcast.storage",
})
public class HazelcastClientTestConfiguration {

    @Bean(name = "ClientInstance")
    public HazelcastInstance clientInstance(StorageNodeFactory storageNodeFactory, ClientConfig config) throws Exception {
        //Ensure there is at least 1 running instance();
        storageNodeFactory.ensureClusterSize(1);
        return HazelcastClient.newHazelcastClient(config);
    }

}
