package bg.softuni.serviceScheduler.user.service.dto;

public record SiteStatisticsServiceModelView (
    Long registeredUsers,
    Long oilChanges
) {
}
