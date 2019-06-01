package com.storagenode.hazelcast.storage;

import com.shared.hazelcast.shared.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Nanda
 */
@Repository
public interface CustomerDao extends CrudRepository<Customer, Long> {

}
