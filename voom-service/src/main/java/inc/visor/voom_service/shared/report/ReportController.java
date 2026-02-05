package inc.visor.voom_service.shared.report;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.VoomUserDetails;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.person.service.UserProfileService;
import inc.visor.voom_service.shared.report.dto.AdminReportResponseDto;
import inc.visor.voom_service.shared.report.dto.ReportResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final UserProfileService userProfileService;
    private final DriverService driverService;
    
    @GetMapping
    public ResponseEntity<ReportResponseDto> getReport(
            @AuthenticationPrincipal VoomUserDetails userDetails,
            @RequestParam("from")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam("to")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to,

            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "driverId", required = false) Long driverId
    ) {

        String username = userDetails != null ? userDetails.getUsername() : null;
        User user = userProfileService.getUserByEmail(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(LocalTime.MAX);

        ReportResponseDto response;

        if (user.getUserRole().getId() == 2) {

            Driver driver = driverService.getDriver(user.getId())
                    .orElseThrow(() -> new RuntimeException("Driver not found"));

            response = reportService.getDriverReport(driver.getId(), fromDateTime, toDateTime);
            return ResponseEntity.ok(response);
        } else if (user.getUserRole().getId() == 1) {
                    if (driverId != null) {
                    return ResponseEntity.ok(
                        reportService.getDriverReport(driverId, fromDateTime, toDateTime)
                    );
                }

                if (userId != null) {
                    return ResponseEntity.ok(
                        reportService.getUserReport(userId, fromDateTime, toDateTime)
                    );
                }

                return ResponseEntity.ok(
                    reportService.getSystemReport(fromDateTime, toDateTime)
                );
        } else {
            response = reportService.getUserReport(user.getId(), fromDateTime, toDateTime);
            System.out.println("USER REPORT" + response);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    public ResponseEntity<ReportResponseDto> getAdminReport(
            @AuthenticationPrincipal VoomUserDetails userDetails,
            @RequestParam("from")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,
            @RequestParam("to")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to
    ) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userProfileService.getUserByEmail(userDetails.getUsername());

        if (user == null ||
                !user.getUserRole().getRoleName().equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(LocalTime.MAX);

        ReportResponseDto response =
                reportService.getSystemReport(fromDateTime, toDateTime);

        return ResponseEntity.ok(response);
    }

}
