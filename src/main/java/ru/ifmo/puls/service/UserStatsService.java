package ru.ifmo.puls.service;

import java.util.function.BiFunction;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.blps.openapi.model.UserStatsResponseTo;
import ru.ifmo.puls.auth.service.UserService;
import ru.ifmo.puls.domain.Role;
import ru.ifmo.puls.domain.TenderStatus;
import ru.ifmo.puls.domain.User;
import ru.ifmo.puls.exception.BadRequest;
import ru.ifmo.puls.repository.PgTenderRepository;

@Service
@RequiredArgsConstructor
public class UserStatsService {
    private final PgTenderRepository tenderRepository;
    private final UserService userService;

    public UserStatsResponseTo getUserStats(long userId) {
        User user = userService.getById(userId);
        BiFunction<TenderStatus, Long, Long> count;

        if (user.getRole() == Role.USER) {
            count = tenderRepository::countByStatusAndUserId;
        } else if (user.getRole() == Role.SUPPLIER) {
            count = tenderRepository::countByStatusAndSupplierId;
        } else {
            throw new BadRequest("Incorrect user type");
        }

        return new UserStatsResponseTo()
                .acceptedTenders(count.apply(TenderStatus.ACCEPTED, userId))
                .declinedTenders(count.apply(TenderStatus.NOT_ACCEPTED, userId));
    }
}
