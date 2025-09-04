package org.phoenix.planet.dto.admin.phti;

public record IssueAndOrderPatternsByPhtiItem(
    String type,
    long orders,
    long exchanges
) {

}