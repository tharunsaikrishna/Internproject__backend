package com.example.banking_project_application.dto.responses;

import lombok.Data;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object representing a cheque book request made by a user")
@Data
public class ChequeBookResponse {

    @Schema(description = "Unique ID of the cheque book request", example = "501")
    private Long requestId;

    @Schema(description = "ID of the user who requested the cheque book", example = "10")
    private Long userId;

    @Schema(description = "ID of the account for which the cheque book is requested", example = "2001")
    private Long accountId;

    @Schema(description = "Current status of the cheque book request", example = "PENDING")
    private String status;

    @Schema(description = "Date when the cheque book request was made", example = "2024-01-25T14:30:00")
    private Date requestDate;

}