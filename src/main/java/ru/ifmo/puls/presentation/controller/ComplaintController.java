package ru.ifmo.puls.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.blps.openapi.api.ComplaintApi;
import ru.blps.openapi.model.ComplaintCreateResponseTo;
import ru.blps.openapi.model.ComplaintRequestTo;
import ru.blps.openapi.model.ComplaintResponseTo;
import ru.blps.openapi.model.ResolutionRequestTo;
import ru.ifmo.puls.auth.service.UserService;
import ru.ifmo.puls.domain.ComplaintConv;
import ru.ifmo.puls.domain.Role;
import ru.ifmo.puls.domain.User;
import ru.ifmo.puls.service.ComplaintManagementService;
import ru.ifmo.puls.service.ComplaintQueryService;

@RestController
public class ComplaintController extends BaseController implements ComplaintApi {
    private final UserService userService;
    private final ComplaintQueryService complaintQueryService;
    private final ComplaintManagementService complaintManagementService;

    public ComplaintController(
            UserService userService,
            ComplaintQueryService complaintQueryService,
            ComplaintManagementService complaintManagementService
    ) {
        super(userService);
        this.userService = userService;
        this.complaintQueryService = complaintQueryService;
        this.complaintManagementService = complaintManagementService;
    }

    @Override
    public ResponseEntity<ComplaintCreateResponseTo> createComplaint(ComplaintRequestTo request) {
        hasRole(Role.USER);

        User user = userService.getCurrentUser();
        ComplaintConv complaint = complaintQueryService.create(
                user.getId(),
                request.getTenderId(),
                request.getMessage()
        );

        return ResponseEntity.ok(
                new ComplaintCreateResponseTo().id(complaint.getId())
        );
    }

    @Override
    public ResponseEntity<ComplaintResponseTo> getComplaintByTenderId(Long id) {
        hasRole(Role.ADMIN);

        return ResponseEntity.ok(complaintManagementService.getByTenderId(id));
    }

    @Override
    public ResponseEntity<Void> resolveComplaint(ResolutionRequestTo resolutionRequestTo) {
        hasRole(Role.ADMIN);

        complaintManagementService.resolve(resolutionRequestTo);
        return ResponseEntity.ok().build();
    }
}
