package ru.ifmo.puls.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;
import ru.blps.openapi.api.ComplaintApi;
import ru.blps.openapi.model.ComplaintCreateResponseTo;
import ru.blps.openapi.model.ComplaintRequestTo;
import ru.blps.openapi.model.ComplaintResponseTo;
import ru.blps.openapi.model.ResolutionRequestTo;
import ru.ifmo.puls.auth.model.User;
import ru.ifmo.puls.auth.service.UserService;
import ru.ifmo.puls.domain.ComplaintConv;
import ru.ifmo.puls.service.ComplaintManagementService;
import ru.ifmo.puls.service.ComplaintQueryService;

@RestController
@RequiredArgsConstructor
public class ComplaintController implements ComplaintApi {
    private final UserService userService;
    private final ComplaintQueryService complaintQueryService;
    private final ComplaintManagementService complaintManagementService;

    @Override
    @Secured("USER")
    public ResponseEntity<ComplaintCreateResponseTo> createComplaint(ComplaintRequestTo request) {
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
    @Secured("ADMIN")
    public ResponseEntity<ComplaintResponseTo> getComplaintByTenderId(Long id) {
        return ResponseEntity.ok(complaintManagementService.getById(id));
    }

    @Override
    @Secured("ADMIN")
    public ResponseEntity<Void> resolveComplaint(ResolutionRequestTo resolutionRequestTo) {
        complaintManagementService.resolve(resolutionRequestTo);
        return ResponseEntity.ok().build();
    }
}
