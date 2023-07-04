package dev.nisalb.hubwork.testUtil;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import dev.nisalb.hubwork.model.*;
import dev.nisalb.hubwork.model.key.RequestId;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Mocker {

    private static final Faker faker = new Faker();
    private static final Random random = new Random();
    private static final AtomicLong idSeq = new AtomicLong(0);
    private static final AtomicLong abnSeq = new AtomicLong(14726835918L);
    private static final AtomicInteger acnSeq = new AtomicInteger(247359168);

    public static Job newJob() {
        return newJob(null, null, null);
    }

    public static Job newJob(User owner) {
        return newJob(owner, null, null);
    }

    public static Job newJob(JobState state) {
        return newJob(null, null, state);
    }

    public static Job newJob(User owner, User worker) {
        return newJob(owner, worker, JobState.GRANTED);
    }

    public static Job newJob(User owner, JobState state) {
        return newJob(owner, null, state);
    }

    public static Job newJob(User owner, User worker, JobState state) {
        var j = new Job();
        j.setId(idSeq.getAndIncrement());
        j.setTitle(faker.harryPotter().spell());

        String description = faker.harryPotter().quote();
        if (description.length() > 160) {
            description = description.substring(0, 160);
        }
        j.setDescription(description);

        j.setDueDate(Date.valueOf(String.format("%1$d-%2$d-%3$d",
                random.nextInt(2023, 2040), // random year 2023-2040
                random.nextInt(1, 12),
                random.nextInt(1, 32))));

        j.setPrice(BigDecimal.valueOf(random.nextInt(10, 1000)));
        j.setCurrency(faker.currency().code());

        if (owner == null)
            owner = newCustomer();
        j.setOwner(owner);

        if (worker != null)
            j.setWorker(worker);

        var allPaymentMethods = PaymentMethod.values();
        // determine a number of payment methods in this job
        var numOfPMs = random.nextInt(allPaymentMethods.length);
        // add randomly chosen `numOfPMs' payment methods to the job
        var pms = new HashSet<PaymentMethod>();
        while (--numOfPMs > 0)
            pms.add(allPaymentMethods[random.nextInt(allPaymentMethods.length)]);
        j.setPaymentMethods(pms);

        j.setPostedAt(Date.from(Instant.now()));

        if (state == null)
            state = JobState.PENDING;
        j.setState(state);

        return j;
    }

    public static User newUser(UserRole role) {
        var c = new User();
        c.setId(idSeq.getAndIncrement());
        c.setRole(role);

        Name fakeName = faker.name();
        c.setUsername(fakeName.username());
        c.setFirstName(fakeName.firstName());
        c.setLastName(fakeName.lastName());
        c.setMobile(faker.phoneNumber().cellPhone());
        c.setEmail(faker.internet().emailAddress(c.getUsername()));
        c.setAddress(newAddress());
        c.setBusiness(newBusiness());

        return c;
    }

    public static User newCustomer() {
        return newUser(UserRole.CUSTOMER);
    }

    public static User newWorker() {
        return newUser(UserRole.WORKER);
    }

    public static Address newAddress() {
        var a = new Address();
        a.setId(idSeq.getAndIncrement());

        var fakeAddress = faker.address();
        a.setCity(fakeAddress.cityName());
        a.setState(fakeAddress.state());
        a.setStreetLineOne(fakeAddress.streetName());
        a.setStreetLineTwo("N/A");
        a.setPostalCode(fakeAddress.zipCode());
        a.setHouseNo(fakeAddress.buildingNumber());

        return a;
    }

    public static Business newBusiness() {
        var b = new Business();
        b.setId(idSeq.getAndIncrement());
        b.setName(faker.rockBand().name());
        b.setABN(String.valueOf(abnSeq.getAndIncrement()));
        b.setACN(String.valueOf(acnSeq.getAndIncrement()));
        b.setAddress(newAddress());

        return b;
    }

    public static Request newRequest(Job job, User worker) {
        return newRequest(job, worker, null);
    }

    public static Request newRequest(Job job, User worker, RequestState state) {
        var r = new Request();
        r.setId(new RequestId(UUID.randomUUID(), job, worker));

        if (state == null)
            state = RequestState.PENDING;
        r.setState(state);

        return r;
    }
}
