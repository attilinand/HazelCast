package com.client.hazelcast.client.services.customer;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.shared.hazelcast.shared.*;
import com.shared.hazelcast.storage.StorageNodeApplication;
import com.shared.hazelcast.client.HazelcastClientTestConfiguration;
import com.shared.hazelcast.client.helper.StorageNodeFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(
        classes = {
                HazelcastClientTestConfiguration.class,
                StorageNodeApplication.class
        }
)
public class CustomerServiceTest {


    @Autowired
    CustomerService customerService;


    @Autowired
    @Qualifier("ClientInstance")
    HazelcastInstance hazelcastInstance;

    @Autowired
    StorageNodeFactory storageNodeFactory;


    @Before
    public void tearDown() {
        hazelcastInstance.getMap(CustomerService.CUSTOMERS_MAP).clear();
    }

    @Test
    public void testAddCustomer() {
        Customer customer = new Customer(1L, "Sachin", new Date(), "Sachin@Sachin.me");
        customerService.addCustomer(customer);

        IMap<Long, Customer> customersMap = hazelcastInstance.getMap(CustomerService.CUSTOMERS_MAP);
        assertEquals(1, customersMap.size());
        assertEquals(customer, customersMap.get(1L));
    }


    @Test
    public void testAddCustomers() {
        Customer customer1 = new Customer(1L, "Sachin", new Date(), "Sachin@gmail.com");
        Customer customer2 = new Customer(2L, "Sachin1", new Date(), "Sachin@test.com");
        Customer customer3 = new Customer(3L, "Sachin3", new Date(), "Sachin@test.com");

        List<Customer> customers = Arrays.asList(customer1, customer2, customer3);

        customerService.addCustomers(customers);

        IMap<Long, Customer> customersMap = hazelcastInstance.getMap(CustomerService.CUSTOMERS_MAP);
        assertEquals(3, customersMap.size());
        assertEquals(customer1, customersMap.get(1L));
        assertEquals(customer2, customersMap.get(2L));
        assertEquals(customer3, customersMap.get(3L));
    }


    @Test
    public void testNoDataLossWithOnlyOneNode() throws Exception {
        storageNodeFactory.ensureClusterSize(4);

        int maxCustomers = 1000;
        List<Customer> customers = generateCustomers(maxCustomers);
        customerService.addCustomers(customers);

        IMap<Long, Customer> customersMap = hazelcastInstance.getMap(CustomerService.CUSTOMERS_MAP);
        assertEquals(maxCustomers, customersMap.size());
        storageNodeFactory.ensureClusterSize(1);
        assertEquals(maxCustomers, customersMap.size());
    }

    private List<Customer> generateCustomers(int maxCustomers) throws Exception {
        List<Customer> customers = new ArrayList<>(maxCustomers);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date d = sdf.parse("1980/01/01");
        Calendar c = Calendar.getInstance();
        c.setTime(d);

        for (long x=0; x<maxCustomers; x++) {
            customers.add(new Customer(
                    x //Customer ID
                    , "Customer " + x //
                    , c.getTime() // DOB based on calendar above
                    , "customer" + x + "@test.com") //Email
            );
            //Add 1 year to the calendar for DOBs
            c.add(Calendar.YEAR, 1);
        }
        return customers;
    }












    @Test
    public void testNoDataLossAfterClusterShutdown() throws Exception {

        storageNodeFactory.ensureClusterSize(4);

        int maxCustomers = 1000;

        List<Customer> customers = generateCustomers(maxCustomers);

        customerService.addCustomers(customers);

        IMap<Long, Customer> customersMap = hazelcastInstance.getMap(CustomerService.CUSTOMERS_MAP);
        assertEquals(maxCustomers, customersMap.size());

        storageNodeFactory.ensureClusterSize(0); // Shutdown all storage nodes

        storageNodeFactory.ensureClusterSize(3); //Start another 3 storage nodes

        assertEquals(maxCustomers, customersMap.size());

    }










    @Test
    @SuppressWarnings("unchecked")
    public void testSearchForCustomersWithDob() throws Exception {
        customerService.addCustomers(generateCustomers(10));


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date startDate = sdf.parse("1980/01/01");
        Date endDate = sdf.parse("1981/01/01");

        Collection<Customer> customers = customerService.findCustomer(startDate, endDate);
        assertEquals(1, customers.size());
    }













    @Test
    public void testFindCustomersByEmail() throws Exception {
        customerService.addCustomers(generateCustomers(10));

        Collection<Customer> customers = customerService.findCustomersByEmail("%@test.com");
        assertEquals(10, customers.size());

    }
















    @Bean(name = "ClientInstance")
    public HazelcastInstance clientInstance(StorageNodeFactory storageNodeFactory, ClientConfig config) throws Exception {
        //Ensure there is at least 1 running instance();
        storageNodeFactory.ensureClusterSize(1);
        return HazelcastClient.newHazelcastClient(config);
    }








    @Test
    public void testUpdateCustomer() throws Exception {
        customerService.addCustomers(generateCustomers(2));

        assertEquals("Customer 1", customerService.getCustomer(1L).getName());

        boolean result = customerService.updateCustomer(1L,
                customer -> {
                    customer.setName("tendulkar");
                    return customer;
                });

        assertTrue(result);
        assertEquals("tendulkar", customerService.getCustomer(1L).getName());
    }











    @Test
    public void testUpdateCustomerWithEntryProcessor() throws Exception {

        customerService.addCustomers(generateCustomers(2));

        Long customerId = 1L;

        Date now = new Date();
        UpdateCustomerDOBEP ep = new UpdateCustomerDOBEP(now);
        Boolean result = customerService.updateCustomerWithEntryProcessor(customerId, ep);
        assertTrue(result);

        Customer customer = customerService.getCustomer(customerId);
        assertEquals(now, customer.getDob());
    }






  






















}