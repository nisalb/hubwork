package dev.nisalb.hubwork.service;

import com.github.javafaker.Faker;
import dev.nisalb.hubwork.model.Address;
import dev.nisalb.hubwork.model.Business;
import dev.nisalb.hubwork.model.User;
import dev.nisalb.hubwork.model.UserRole;
import dev.nisalb.hubwork.service.repo.UserRepository;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

@Service
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Setter(onMethod = @__({@Autowired}))
    private UserRepository userRepository;

    public void initUsersAndBusinesses(int userCount, int businessCount) {

        long existingCount = userRepository.count();
        if (existingCount > 0) {
            logger.atWarn().log("Users are already initialized: " + existingCount);
            userRepository.findAll();
            return;
        }

        var businesses = getFakeBusinesses(businessCount);
        var users = getFakeUsers(userCount, businesses);

        try {
            userRepository.saveAll(users);
        } catch (Exception ex) {
            logger.atError().log("Unable to initialize user entities: " + ex.getMessage());
        }

    }

    public List<Business> getFakeBusinesses(int count) {
        final var faker = new Faker();
        final var rnd = new Random();
        var businesses = new ArrayList<Business>();

        var names = getAUniqueListOf(count, () -> faker.rockBand().name());
        var ABNs = getAUniqueListOf(count, () -> Long.toString(Math.abs(rnd.nextLong() + 14726835918L) % 100_000_000_000L));
        var ACNs = getAUniqueListOf(count, () -> Integer.toString(Math.abs(rnd.nextInt() + 247359168) % 1_000_000_000));

        for (int i = 0; i < count; i++) {
            var b = new Business();

            b.setName(names.get(i));
            b.setABN(ABNs.get(i));
            b.setACN(ACNs.get(i));
            b.setAddress(getAFakeAddress(faker));

            businesses.add(b);
        }

        return businesses;
    }

    public List<User> getFakeUsers(int count, List<Business> businesses) {
        final var faker = new Faker();

        // usernames and firstName+lastName will be out of sync.
        // trying to sync them will make this step very slow.
        // not worthy of an optimization
        var usernames = getAUniqueListOf(count, () -> faker.name().username());
        var firstnames = getAUniqueListOf(count, () -> faker.name().firstName());
        var lastnames = getAUniqueListOf(count, () -> faker.name().lastName());
        var emails = usernames.stream()
                .map(username -> faker.internet().emailAddress(username)).toList();
        var mobiles = getAUniqueListOf(count, () -> faker.phoneNumber().phoneNumber());
        var roles = UserRole.values();

        var rnd = new Random();
        var users = new ArrayList<User>();

        for (int i = 0; i < count; i++) {
            var u = new User();
            u.setUsername(usernames.get(i));
            u.setFirstName(firstnames.get(i));
            u.setLastName(lastnames.get(i));
            u.setEmail(emails.get(i));
            u.setMobile(mobiles.get(i));
            u.setRole(roles[rnd.nextInt(roles.length)]);
            u.setAddress(getAFakeAddress(faker));
            u.setBusiness(businesses.get(rnd.nextInt(businesses.size())));
            users.add(u);
        }

        return users;
    }

    private <T> List<T> getAUniqueListOf(int count, Supplier<? extends T> supplier) {
        var l = new HashSet<T>();

        while(l.size() <= count) {
            l.add(supplier.get());
        }

        return List.copyOf(l);
    }

    private Address getAFakeAddress(Faker faker) {
        var addr = new Address();
        var fakeAddress = faker.address();
        addr.setHouseNo(fakeAddress.buildingNumber());
        addr.setStreetLineOne(fakeAddress.streetName());
        addr.setCity(fakeAddress.city());
        addr.setPostalCode(fakeAddress.zipCode());
        addr.setState(fakeAddress.state());

        return addr;
    }
}
