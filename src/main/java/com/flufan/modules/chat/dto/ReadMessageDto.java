package com.flufan.modules.chat.dto;

import java.time.Instant;
import java.util.UUID;

public record ReadMessageDto(
        Instant date,
        UUID receiverPublicId
) {}
