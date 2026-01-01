package com.flufan.dto;

import java.time.Instant;
import java.util.UUID;

public record ReadMessageDto(
        Instant date,
        UUID receiverPublicId
) {}
